package pl.lodz.p.adapter;

import pl.lodz.p.model.domain.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepositoryAdapterInterface {

    Room create(String number, int bedsAmount, double price);

    Optional<Room> findById(String id);

    Optional<Room> findByNumber(String number);

    boolean existsByNumber(String number);

    List<Room> findAll();

    void save(Room room);

    void delete(Room room);

    void deleteAll();
}

