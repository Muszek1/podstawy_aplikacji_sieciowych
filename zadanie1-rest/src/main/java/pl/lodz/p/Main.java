package pl.lodz.p;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import pl.lodz.p.manager.GuestManager;
import pl.lodz.p.manager.RentManager;
import pl.lodz.p.manager.RoomManager;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.MongoDatabaseProvider;
import pl.lodz.p.view.ConsoleInterface;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final String CONNECTION_STRING =
            "mongodb://host.docker.internal:27017,host.docker.internal:27018,host.docker.internal:27019/?replicaSet=rs0";
    private static final String DB_NAME = "nbd_hotel";

    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoDatabaseProvider.createClient(CONNECTION_STRING)) {
            MongoDatabase database = MongoDatabaseProvider.initDatabase(mongoClient, DB_NAME);

            GuestManager guestManager = new GuestManager(database);
            RoomManager roomManager = new RoomManager(database);
            RentManager rentManager = new RentManager(database);

            initializeDataIfEmpty(guestManager, roomManager, rentManager);

            ConsoleInterface consoleInterface = new ConsoleInterface(guestManager, roomManager, rentManager);
            consoleInterface.run();
        }
    }

    private static void initializeDataIfEmpty(GuestManager guestManager, RoomManager roomManager, RentManager rentManager) {
        if (roomManager.findAllRooms().isEmpty()) {
            roomManager.registerRoom("101", 2, 150.0);
            roomManager.registerRoom("102", 3, 200.0);
            roomManager.registerRoom("201", 1, 100.0);
            roomManager.registerRoom("999", 4, 500.0);
        }

        if (guestManager.findAllGuests().isEmpty()) {
            guestManager.registerGuest("Jan", "Kowalski", "1234", "Warszawa", "Marszałkowska", "10", "default");
            guestManager.registerGuest("Anna", "Nowak", "4567", "Łódź", "Piotrkowska", "42/5", "premium");
        }

        if (rentManager.findAllRents().isEmpty()) {
            LocalDateTime beginTime1 = LocalDateTime.parse("2025-06-13 12:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime beginTime2 = LocalDateTime.parse("2026-07-13 12:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime endTime1 = LocalDateTime.parse("2025-06-18 12:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime endTime2 = LocalDateTime.parse("2026-07-15 12:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            Client client = guestManager.findGuestByPersonalId("1234");
            Room room1 = roomManager.findRoomByNumber("101");
            Room room2 = roomManager.findRoomByNumber("102");

            rentManager.rentRoom(beginTime1, endTime1, client, room1);
            rentManager.rentRoom(beginTime2, endTime2, client, room2);
        }
    }
}