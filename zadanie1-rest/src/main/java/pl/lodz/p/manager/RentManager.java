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

    public Rent createRent(String clientLogin, String roomNumber) {
        if (rentRepository.existsByRoomNumberAndEndTimeIsNull(roomNumber)) {
            throw new IllegalStateException("Pokój " + roomNumber + " jest już wynajęty.");
        }

        User user = userRepository.findById(clientLogin)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta o loginie: " + clientLogin));

        if (!(user instanceof Client client)) {
            throw new IllegalArgumentException("Pokój może wynająć tylko klient.");
        }

        Room room = roomRepository.findById(roomNumber)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono pokoju o numerze: " + roomNumber));

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
