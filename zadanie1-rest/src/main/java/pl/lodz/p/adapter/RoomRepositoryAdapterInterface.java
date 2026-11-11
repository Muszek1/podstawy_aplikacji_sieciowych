package pl.lodz.p.adapter;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.repository.MongoRepository;

import pl.lodz.p.exception.InvalidValueException;
import pl.lodz.p.exception.UniqueValueException;

import pl.lodz.p.model.domain.Room;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepositoryAdapterInterface extends MongoRepository<Room, UUID> {

    Optional<Room> findByNumber(String number);
    boolean existsByNumber(String number);
    default Room create(String number, int bedsAmount, double price) {

        if (number == null || number.trim().isEmpty()) {
            throw new InvalidValueException("Numer pokoju nie może być pusty");
        }

        Room room = new Room(
                UUID.randomUUID(),
                number,
                bedsAmount,
                price
        );

        try {
            return insert(room);
        } catch (DuplicateKeyException e) {
            throw new UniqueValueException("Pokój o numerze " + number + " już istnieje");
        }
    }
}
