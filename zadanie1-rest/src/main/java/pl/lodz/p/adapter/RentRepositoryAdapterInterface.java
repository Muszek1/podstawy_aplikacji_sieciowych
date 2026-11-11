package pl.lodz.p.adapter;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;

import java.util.List;
import java.util.UUID;

public interface RentRepositoryAdapterInterface extends MongoRepository<Rent, UUID> {

    long deleteByUuidAndEndTimeIsNull(UUID uuid);
    boolean existsByRoomAndEndTimeIsNull(Room room);
    List<Rent> findByClientAndEndTimeIsNull(Client client);
    List<Rent> findByClientAndEndTimeIsNotNull(Client client);
    List<Rent> findByRoomAndEndTimeIsNull(Room room);
    List<Rent> findByRoomAndEndTimeIsNotNull(Room room);
}
