package pl.lodz.p.manager;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import pl.lodz.p.adapter.RentRepositoryAdapterInterface;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.adapter.UserRepositoryAdapterInterface;
import pl.lodz.p.exception.BusinessLogicException;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;
import pl.lodz.p.model.domain.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequestScope
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

    public Rent createRent(String clientId, String roomId, LocalDateTime beginTime) {

        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono klienta o id: " + clientId));

        if (!(user instanceof Client client)) {
            throw new BusinessLogicException("Pokój może wynająć tylko klient.");
        }

        if (!client.isActive()) {
            throw new BusinessLogicException(
                    "Nieaktywny klient nie może wynająć pokoju"
            );
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono pokoju o id: " + roomId));

        if (room.isRented()){
            throw new BusinessLogicException("Pokój " + room.getNumber() + "jest już wynajęty.");
        }

        room.setRented(true);

        try {
            roomRepository.save(room);
        } catch (org.springframework.dao.OptimisticLockingFailureException e) {
            throw new ConcurrentOperationException("Konflikt współbieżności: Pokój został właśnie wynajęty przez kogoś innego.");
        }

        return rentRepository.create(beginTime, client, room);
    }

    public void endRent(String rentId) {
        Rent rent = rentRepository.findById(rentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono wynajmu: " + rentId
                ));

        if (rent.getEndTime() != null) {
            throw new BusinessLogicException("Ten wynajem już został zakończony");
        }

        LocalDateTime endTime = LocalDateTime.now();

        if (endTime.isBefore(rent.getBeginTime())) {
            throw new BusinessLogicException(
                    "Nie można zakończyć wynajmu przed jego rozpoczęciem"
            );
        }

        rent.setEndTime(endTime);

        // Zwalniamy pokój po wynajmie
        Room room = rent.getRoom();
        room.setRented(false);
        roomRepository.save(room);

        rentRepository.save(rent);
    }

    public List<Rent> getClientRents(String clientId, boolean current) {
        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono klienta: " + clientId
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

    public List<Rent> getRoomRents(String roomId, boolean current) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono pokoju: " + roomId
                ));

        return current
                ? rentRepository.findByRoomAndEndTimeIsNull(room)
                : rentRepository.findByRoomAndEndTimeIsNotNull(room);
    }

    public Rent findRentById(String id) {
        return rentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nie znaleziono wynajmu: " + id
                ));
    }

    public List<Rent> getAllRents() {
        return rentRepository.findAll();
    }

    public void deleteRent(String id) {
        Rent rent = findRentById(id);

        if (!rent.isActive()) {
            throw new BusinessLogicException("Nie można usunąć zakończonego wynajmu");
        }

        rentRepository.delete(rent);
    }

    public boolean hasRoomActiveRents(Room room) {
        return room.isRented();
    }
}
