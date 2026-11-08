package pl.lodz.p.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.RoomEnt;

import java.util.ArrayList;
import java.util.List;

public class MongoRoomRepository {
    private final MongoCollection<RoomEnt> roomsCollection;

    public MongoRoomRepository(MongoDatabase database) {
        this.roomsCollection = database.getCollection("rooms", RoomEnt.class);
    }

    public RoomEnt saveRoom(RoomEnt roomEnt) {
        try {
            roomsCollection.insertOne(roomEnt);
            return roomEnt;
        } catch (Exception e) {
            throw new RepositoryException("Wystąpił błąd przy zapisie pokoju o numerze = " + roomEnt.getNumber() + ". " + e.getMessage());
        }
    }

    public void setRoomArchivedStatus(String number, boolean isArchived) {
        UpdateResult result;
        try {
            result = roomsCollection.updateOne(
                    Filters.and(
                            Filters.eq("number", number),
                            Filters.eq("archived", !isArchived)
                    ),
                    Updates.set("archived", isArchived)
            );
        } catch (Exception e) {
            throw new RepositoryException("Wystąpił błąd przy zmianie statusu archiwizacji pokoju o numerze = " + number + ", " + e.getMessage());
        }

        if (result.getModifiedCount() == 0) {
            // Jeśli pokój nie istnieje, rzuci EntityNotFoundException
            findRoomByNumber(number);
            // Jeśli istnieje, stan był już zmieniony przez inną operację
            throw new ConcurrentOperationException("Stan archiwizacji pokoju o numerze = " + number + " został już zmieniony");
        }
    }

    public RoomEnt findRoomByNumber(String number) {
        RoomEnt roomEnt = roomsCollection.find(Filters.eq("number", number)).first();
        if (roomEnt == null) {
            throw new EntityNotFoundException("Nie znaleziono pokoju o numerze = " + number);
        }
        return roomEnt;
    }

    public List<RoomEnt> findAllRooms() {
        return roomsCollection.find().into(new ArrayList<>());
    }
}