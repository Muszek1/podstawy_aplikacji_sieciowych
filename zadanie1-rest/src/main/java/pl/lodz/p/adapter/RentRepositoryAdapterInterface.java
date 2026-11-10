package pl.lodz.p.adapter;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.lodz.p.model.domain.Rent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentRepositoryAdapterInterface extends MongoRepository<Rent, UUID> {

    boolean existsByRoomNumberAndEndTimeIsNull(String roomNumber);
    Optional<Rent> findByRoomNumberAndEndTimeIsNull(String roomNumber);
    List<Rent> findByClientLogin(String clientLogin);
}
