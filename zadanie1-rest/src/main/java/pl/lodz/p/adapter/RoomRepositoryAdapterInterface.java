package pl.lodz.p.adapter;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.lodz.p.model.db.RoomEnt;
import pl.lodz.p.model.domain.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepositoryAdapterInterface extends MongoRepository<Room, UUID> {

    Room findByNumber(String number);
    boolean existsByNumber(String number);

}
