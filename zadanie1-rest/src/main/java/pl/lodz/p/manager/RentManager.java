package pl.lodz.p.manager;

import org.springframework.stereotype.Service;
import pl.lodz.p.adapter.RentRepositoryAdapterInterface;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.adapter.UserRepositoryAdapterInterface;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;
import pl.lodz.p.model.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RentManager {
    private final RentRepositoryAdapterInterface rentRepository;
    private final UserRepositoryAdapterInterface userRepository;
    private final RoomRepositoryAdapterInterface roomRepository;

    public RentManager(RentRepositoryAdapterInterface rentRepository, UserRepositoryAdapterInterface userRepository, RoomRepositoryAdapterInterface roomRepository) {
        this.rentRepository = rentRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    public Rent createRent(UUID clientUuid, UUID roomUuid){

        User user = userRepository.findById(clientUuid)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta o uuid: " + clientUuid));

        if (!(user instanceof Client client)) {
            throw new IllegalArgumentException("Pokój może wynająć tylko klient.");
        }

        Room room = roomRepository.findById(roomUuid)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono pokoju o UUID: " + roomUuid));

        if (rentRepository.existsByRoomNumberAndEndTimeIsNull(
                room.getNumber())) {
            throw new IllegalStateException(
                    "Pokój " + room.getNumber() + " jest już wynajęty.");
        }

        Rent rent = new Rent(UUID.randomUUID(), LocalDateTime.now(), client, room);
        return rentRepository.save(rent);
    }

    public void endRent(String roomNumber) {
        Rent activeRent = rentRepository.findByRoomNumberAndEndTimeIsNull(roomNumber)
                .orElseThrow(() -> new EntityNotFoundException("Pokój " + roomNumber + " nie jest aktualnie wynajęty."));

        activeRent.setEndTime(LocalDateTime.now());
        rentRepository.save(activeRent);
    }

    public List<Rent> getClientRents(String clientLogin) {
        return rentRepository.findByClientLogin(clientLogin);
    }

    public List<Rent> getAllRents() {
        return rentRepository.findAll();
    }
}
