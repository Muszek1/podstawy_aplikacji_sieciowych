package pl.lodz.p.adapter;

import pl.lodz.p.model.domain.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepositoryAdapterInterface {

    Room create(String number, int bedsAmount, double price);

    Optional<Room> findById(UUID uuid);

    Optional<Room> findByNumber(String number);

    boolean existsByNumber(String number);

    List<Room> findAll();

    void save(Room room);

    void delete(Room room);

    void deleteAll();
}

