package pl.lodz.p.manager;

import org.springframework.stereotype.Service;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.exception.BusinessLogicException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.*;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomManager {
    private final RoomRepositoryAdapterInterface adapter;
    private final RentManager rentManager;

    public RoomManager(RoomRepositoryAdapterInterface repository, RentManager rentManager) {
        this.adapter = repository;
        this.rentManager = rentManager;
    }

    public Room createRoom(Room room) {
        if (adapter.existsByNumber(room.getNumber())) {
            throw new UniqueValueException("Pokój o numerze " + room.getNumber() + " już istnieje");
        }

        return adapter.save(room);
    }

    public void updateRoom(Room externalRoom, UUID uuid) {
        Room actualRoom = findRoomByUuid(uuid);

        actualRoom.setBedsAmount(externalRoom.getBedsAmount());
        actualRoom.setBasePrice(externalRoom.getBasePrice());

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
