package pl.lodz.p.manager;


import com.mongodb.client.MongoDatabase;
import pl.lodz.p.adapter.MongoRoomRepositoryAdapter;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.MongoRoomRepository;

import java.util.List;

public class RoomManager {
    private final MongoRoomRepositoryAdapter adapter;

    public RoomManager(MongoDatabase database) {
        MongoRoomRepository repository = new MongoRoomRepository(database);
        adapter = new MongoRoomRepositoryAdapter(repository);
    }

    public Room registerRoom(String number, int bedsAmount, double price) {
        try {
            adapter.findRoomByNumber(number);
        } catch (EntityNotFoundException _) {
            Room room = new Room(number, bedsAmount, price);
            return adapter.saveRoom(room);
        }

        throw new UniqueValueException("Pokój o numerze = " + number + " już istnieje");
    }

    public void unregisterRoom(String number) {
        adapter.setRoomArchivedStatus(number, true);
    }

    public void activateRoom(String number) {
        adapter.setRoomArchivedStatus(number, false);
    }

    public Room findRoomByNumber(String number) {
        return adapter.findRoomByNumber(number);
    }

    public List<Room> findAllRooms() {
        return adapter.findAllRooms();
    }

}
