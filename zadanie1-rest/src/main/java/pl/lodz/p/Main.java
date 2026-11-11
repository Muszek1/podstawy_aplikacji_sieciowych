package pl.lodz.p;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.manager.RoomManager;
import pl.lodz.p.model.domain.Room;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

//    @Bean
//    CommandLineRunner initData(RoomManager roomManager) {
//        return args -> {
//            String roomNumber = "999";
//
//            // Warto dodać taki warunek!
//            // Inaczej przy drugim uruchomieniu aplikacji dostaniesz DuplicateKeyException
//            // przez adnotację @Indexed(unique = true) nad polem number.
//            try {
//                Room room = new Room(roomNumber, 2, 199.99);
//                roomManager.createRoom(room);
//                System.out.println("✅ Dodano pokój testowy do bazy danych!");
//            } catch (IllegalStateException e) { // lub inny wyjątek, który rzucasz w menedżerze
//                System.out.println("⚠️ Pokój " + roomNumber + " już istnieje w bazie.");
//            }
//        };
//    }
}