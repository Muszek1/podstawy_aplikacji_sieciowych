package pl.lodz.p.manager;

import org.springframework.stereotype.Service;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.exception.BusinessLogicException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.*;


import java.util.List;
import java.util.UUID;

@Service
public class RoomManager {
    private final RoomRepositoryAdapterInterface adapter;
    private final RentManager rentManager;

    public RoomManager(RoomRepositoryAdapterInterface repository, RentManager rentManager) {
        this.adapter = repository;
        this.rentManager = rentManager;
    }

    public Room createRoom(String number, int bedsAmount, double price) {

        return adapter.create(number, bedsAmount, price);
    }

    public void updateRoom(UUID uuid, int bedsAmount, double price, Long expectedVersion) {
        Room actualRoom = findRoomByUuid(uuid);

        if (!actualRoom.getVersion().equals(expectedVersion)) {
            throw new BusinessLogicException("Obiekt został w międzyczasie zmodyfikowany przez kogoś innego (konflikt wersji)");
        }

        actualRoom.setBedsAmount(bedsAmount);
        actualRoom.setPrice(price);

        adapter.save(actualRoom);
    }


    public Room findRoomByNumber(String number) {
        return adapter.findByNumber(number)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono pokoju o numerze: " + number));
    }

    public Room findRoomByUuid(UUID uuid) {
        return adapter.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono pokoju o UUID: " + uuid));
    }

    public List<Room> findAllRooms() {
        return adapter.findAll();
    }

    public void deleteRoom(UUID uuid) {
        Room room = findRoomByUuid(uuid);

        if (rentManager.hasRoomActiveRents(room)) {
            throw new BusinessLogicException("Pokój o numerze " + room.getNumber() + " jest aktualnie wynajmowany i nie może zostać usunięty");
        }

        adapter.delete(room);
    }
}
