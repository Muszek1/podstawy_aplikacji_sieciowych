package pl.lodz.p.manager;

import org.springframework.stereotype.Service;
import pl.lodz.p.adapter.RentRepositoryAdapterInterface;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.adapter.UserRepositoryAdapterInterface;
import pl.lodz.p.exception.BusinessLogicException;
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


    public RentManager(RentRepositoryAdapterInterface rentRepository,
                       UserRepositoryAdapterInterface userRepository,
                       RoomRepositoryAdapterInterface roomRepository) {

        this.rentRepository = rentRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    public Rent createRent(UUID clientUuid, UUID roomUuid, LocalDateTime beginTime) {

        User user = userRepository.findById(clientUuid)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta o uuid: " + clientUuid));

        if (!(user instanceof Client client)) {
            throw new BusinessLogicException("Pokój może wynająć tylko klient.");
        }

        if (!client.isActive()) {
            throw new BusinessLogicException(
                    "Nieaktywny klient nie może wynająć pokoju"
            );
        }

        Room room = roomRepository.findById(roomUuid)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono pokoju o UUID: " + roomUuid));

        if (rentRepository.existsByRoomAndEndTimeIsNull(room)) {
            throw new BusinessLogicException(
                    "Pokój " + room.getNumber() + " jest już wynajęty.");
        }

        return rentRepository.create(beginTime, client, room);
    }

    public void endRent(UUID rentUuid) {
        Rent rent = rentRepository.findById(rentUuid)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono wynajmu: " + rentUuid
                ));

        if (rent.getEndTime() != null) {
            return;
        }

        LocalDateTime endTime = LocalDateTime.now();

        if (endTime.isBefore(rent.getBeginTime())) {
            throw new BusinessLogicException(
                    "Nie można zakończyć wynajmu przed jego rozpoczęciem"
            );
        }

        rent.setEndTime(endTime);
        rentRepository.save(rent);
    }

    public List<Rent> getClientRents(UUID clientUuid, boolean current) {
        User user = userRepository.findById(clientUuid)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono klienta: " + clientUuid
                ));

        if (!(user instanceof Client client)) {
            throw new BusinessLogicException(
                    "Wskazany użytkownik nie jest klientem"
            );
        }

        return current
                ? rentRepository.findByClientAndEndTimeIsNull(client)
                : rentRepository.findByClientAndEndTimeIsNotNull(client);
    }

    public List<Rent> getRoomRents(UUID roomUuid, boolean current) {
        Room room = roomRepository.findById(roomUuid)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono pokoju: " + roomUuid
                ));

        return current
                ? rentRepository.findByRoomAndEndTimeIsNull(room)
                : rentRepository.findByRoomAndEndTimeIsNotNull(room);
    }

    public Rent findRentByUuid(UUID uuid) {
        return rentRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono wynajmu: " + uuid
                ));
    }

    public List<Rent> getAllRents() {
        return rentRepository.findAll();
    }

    public void deleteRent(UUID uuid) {
        long deleted = rentRepository.deleteByUuidAndEndTimeIsNull(uuid);

        if (deleted == 0) {
            findRentByUuid(uuid);

            throw new BusinessLogicException(
                    "Nie można usunąć zakończonego wynajmu"
            );
        }
    }

    public boolean hasRoomActiveRents(Room room) {
        return rentRepository.existsByRoomAndEndTimeIsNull(room);
    }
}
