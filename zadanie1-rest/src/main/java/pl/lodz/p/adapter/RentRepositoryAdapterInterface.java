package pl.lodz.p.adapter;

import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentRepositoryAdapterInterface {

    Rent create(LocalDateTime beginTime, Client client, Room room);

    Optional<Rent> findById(UUID uuid);

    List<Rent> findByClientAndEndTimeIsNull(Client client);
    List<Rent> findByClientAndEndTimeIsNotNull(Client client);

    List<Rent> findByRoomAndEndTimeIsNull(Room room);
    List<Rent> findByRoomAndEndTimeIsNotNull(Room room);

    List<Rent> findAll();
    void save(Rent rent);
    void delete(Rent rent);

    void deleteAll();
}
