package pl.lodz.p.adapter;

import org.springframework.data.mongodb.repository.MongoRepository;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RentRepositoryAdapterInterface extends MongoRepository<Rent, UUID> {

    long deleteByUuidAndEndTimeIsNull(UUID uuid);
    boolean existsByRoomAndEndTimeIsNull(Room room);
    List<Rent> findByClientAndEndTimeIsNull(Client client);
    List<Rent> findByClientAndEndTimeIsNotNull(Client client);
    List<Rent> findByRoomAndEndTimeIsNull(Room room);
    List<Rent> findByRoomAndEndTimeIsNotNull(Room room);
    default Rent create(LocalDateTime beginTime, Client client, Room room) {

        if (beginTime == null) {
            throw new IllegalArgumentException("Czas rozpoczęcia wynajmu nie może być pusty");
        }
        if (client == null) {
            throw new IllegalArgumentException("Klient nie może być pusty");
        }
        if (room == null) {
            throw new IllegalArgumentException("Pokój nie może być pusty");
        }

        Rent rent = new Rent(
                UUID.randomUUID(),
                beginTime,
                null,
                client,
                room
        );

        return insert(rent);
    }
}
