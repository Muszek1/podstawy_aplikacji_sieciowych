package pl.lodz.p.manager;

import com.mongodb.client.MongoDatabase;
import pl.lodz.p.adapter.MongoRentRepositoryAdapter;
import pl.lodz.p.adapter.RentRepositoryAdapterInterface;
import pl.lodz.p.exception.BusinessLogicException;
import pl.lodz.p.exception.InvalidValueException;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;
import pl.lodz.p.repository.MongoRentRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RentManager {
    private final RentRepositoryAdapterInterface adapter;

    public RentManager(MongoDatabase database) {
        MongoRentRepository repository = new MongoRentRepository(database);
        this.adapter = new MongoRentRepositoryAdapter(repository);
    }

    public RentManager(RentRepositoryAdapterInterface adapter) {
        this.adapter = adapter;
    }

    public Rent rentRoom(LocalDateTime beginTime, LocalDateTime endTime, Client client, Room room) {
        if (endTime.isBefore(beginTime)) {
            throw new InvalidValueException("Data zakończenia: " + endTime + " nie może być wcześniej niż data rozpoczęcia: " + beginTime);
        }

        if (client.isArchived()) {
            throw new BusinessLogicException("Gość o personalId = " + client.getPersonalId() + " jest zarchiwizowany");
        }

        if (room.isRented() || room.isArchived()) {
            throw new BusinessLogicException("Pokój o numerze = " + room.getNumber() + " jest już wypożyczony lub zarchiwizowany");
        }

        Rent rent = new Rent(beginTime, endTime, client, room);

        return adapter.saveRent(rent);
    }

    public void endRent(UUID uuid) {
        adapter.endRent(uuid);
    }

    public Rent findRentByUuid(UUID uuid) {
        return adapter.findRentByUuid(uuid);
    }

    public List<Rent> findAllRents() {
        return adapter.findAllRents();
    }

    public List<Rent> findAllUserCurrentRents(String personalId) {
        return adapter.findAllGuestRents(personalId);
    }
    public List<Rent> findAllUserPreviousRents(String personalId) {
        return adapter.findAllGuestRents(personalId);
    }

    public List<Rent> findAllRoomCurrentRents(String number) {
        return adapter.findAllGuestRents(personalId);
    }
    public List<Rent> findAllRoomPreviousRents(String number) {
        return adapter.findAllGuestRents(personalId);
    }

    public void deleteRent(UUID uuid) {

    }
}
