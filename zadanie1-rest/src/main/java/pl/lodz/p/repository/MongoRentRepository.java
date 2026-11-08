package pl.lodz.p.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.UserEnt;
import pl.lodz.p.model.db.RentEnt;
import pl.lodz.p.model.db.RoomEnt;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MongoRentRepository {
    private final MongoCollection<RentEnt> rentsCollection;
    private final MongoCollection<RoomEnt> roomsCollection;
    private final MongoCollection<UserEnt> guestsCollection;

    public MongoRentRepository(MongoDatabase database) {
        this.rentsCollection = database.getCollection("rents", RentEnt.class);
        this.roomsCollection = database.getCollection("rooms", RoomEnt.class);
        this.guestsCollection = database.getCollection("guests", UserEnt.class);
    }

    public RentEnt saveRent(RentEnt rentEnt, String roomNumber, String guestPersonalId) {
        // 1. Pobranie i weryfikacja gościa (rzuca EntityNotFoundException, jeśli nie istnieje)
        UserEnt userEnt = findGuestByPersonalId(guestPersonalId);

        if (userEnt.isArchived()) {
            throw new ConcurrentOperationException("Gość o personalId = " + guestPersonalId + " został zarchiwizowany");
        }

        // 2. ATOMOWE sprawdzenie dostępności pokoju i zmiana flagi rented -> true w jednym kroku
        RoomEnt updatedRoom = roomsCollection.findOneAndUpdate(
                Filters.and(
                        Filters.eq("number", roomNumber),
                        Filters.eq("rented", false),
                        Filters.eq("archived", false)
                ),
                Updates.set("rented", true),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );

        if (updatedRoom == null) {
            findRoomByNumber(roomNumber);
            throw new ConcurrentOperationException("Pokój o numerze " + roomNumber + " został już wynajęty lub zarchiwizowany");
        }

        // 3. Zapis dokumentu wypożyczenia wraz z kompensacją w razie błędu
        try {
            rentEnt.setGuest(userEnt);
            rentEnt.setRoom(updatedRoom);
            rentsCollection.insertOne(rentEnt);
            return rentEnt;
        } catch (Exception e) {
            // Kompensacja: cofamy flagę wynajęcia pokoju, jeśli nie udało się zapisać dokumentu RentEnt
            roomsCollection.updateOne(
                    Filters.eq("number", roomNumber),
                    Updates.set("rented", false)
            );
            throw new RepositoryException("Wystąpił błąd przy zapisie rezerwacji o UUID = " + rentEnt.getUuid() + ". " + e.getMessage());
        }
    }

    public RentEnt findRentByUuid(UUID uuid) {
        RentEnt rentEnt = rentsCollection.find(Filters.eq("entity_uuid", uuid)).first();
        if (rentEnt == null) {
            throw new EntityNotFoundException("Nie znaleziono rezerwacji o UUID = " + uuid);
        }
        return rentEnt;
    }

    public List<RentEnt> findAllRents() {
        return rentsCollection.find().into(new ArrayList<>());
    }

    public List<RentEnt> findGuestRents(String personalId) {
        return rentsCollection.find(Filters.eq("guest.personal_id", personalId))
                .into(new ArrayList<>());
    }

    public void endRent(UUID uuid) {
        // Atomowa zmiana statusu active z true na false
        RentEnt endedRent = rentsCollection.findOneAndUpdate(
                Filters.and(
                        Filters.eq("entity_uuid", uuid),
                        Filters.eq("active", true)
                ),
                Updates.combine(
                        Updates.set("active", false),
                        Updates.set("room.rented", false)
                ),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );

        if (endedRent == null) {
            // Sprawdzamy czy rezerwacja w ogóle istnieje
            findRentByUuid(uuid);
            // Jeśli istnieje, to znaczy że miała już active == false
            throw new ConcurrentOperationException("Rezerwacja o UUID = " + uuid + " została już zakończona");
        }

        try {
            roomsCollection.updateOne(
                    Filters.eq("number", endedRent.getRoom().getNumber()),
                    Updates.set("rented", false)
            );
        } catch (Exception e) {
            throw new RepositoryException("Wystąpił błąd przy zakończeniu rezerwacji o UUID = " + uuid + ", " + e.getMessage());
        }
    }

    private UserEnt findGuestByPersonalId(String personalId) {
        UserEnt userEnt = guestsCollection.find(Filters.eq("personal_id", personalId)).first();
        if (userEnt == null) {
            throw new EntityNotFoundException("Nie znaleziono gościa o personalId = " + personalId);
        }
        return userEnt;
    }

    private RoomEnt findRoomByNumber(String number) {
        RoomEnt roomEnt = roomsCollection.find(Filters.eq("number", number)).first();
        if (roomEnt == null) {
            throw new EntityNotFoundException("Nie znaleziono pokoju o numerze = " + number);
        }
        return roomEnt;
    }
}
