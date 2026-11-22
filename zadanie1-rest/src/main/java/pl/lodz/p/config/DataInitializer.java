package pl.lodz.p.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import pl.lodz.p.adapter.RentRepositoryAdapterInterface;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.adapter.UserRepositoryAdapterInterface;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.ResourceAdministrator;
import pl.lodz.p.model.domain.Room;
import pl.lodz.p.model.domain.UserAdministrator;

import java.time.LocalDateTime;

@ApplicationScoped
public class DataInitializer {

    private final RoomRepositoryAdapterInterface roomRepository;
    private final UserRepositoryAdapterInterface userRepository;
    private final RentRepositoryAdapterInterface rentRepository;

    public DataInitializer(RoomRepositoryAdapterInterface roomRepository,
                           UserRepositoryAdapterInterface userRepository,
                           RentRepositoryAdapterInterface rentRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.rentRepository = rentRepository;
    }

    void onStart(@Observes StartupEvent event) {
        rentRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();

        Room room1 = roomRepository.create("101", 2, 150.0);
        Room room2 = roomRepository.create("102", 3, 220.0);
        roomRepository.create("VIP-1", 1, 500.0);

        Client client1 = (Client) userRepository.create(Client.class, "klient1", "Jan", "Kowalski", true);
        userRepository.create(Client.class, "klient2", "Anna", "Nowak", true);

        userRepository.create(ResourceAdministrator.class, "resAdmin", "Piotr", "Zarządca", true);
        userRepository.create(UserAdministrator.class, "userAdmin", "Tomasz", "Szef", true);

        rentRepository.create(LocalDateTime.now().withNano(0), client1, room1);

        room1.setRented(true);
        roomRepository.save(room1);

        System.out.println("====== BAZA DANYCH ZAINICJOWANA POMYŚLNIE ======");
        System.out.println("Dodano zasoby, użytkowników oraz początkowe alokacje.");
    }
}