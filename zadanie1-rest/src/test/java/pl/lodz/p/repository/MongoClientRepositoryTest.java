package pl.lodz.p.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lodz.p.AbstractDatabaseTest;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.UserEnt;
import pl.lodz.p.model.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class MongoClientRepositoryTest extends AbstractDatabaseTest {
    private MongoGuestRepository guestRepository;
    private Client validClient;
    private Client validClient2;
    private Client invalidClient;
    private Client invalidClient2;

    @BeforeEach
    public void setup() {
        guestRepository = new MongoGuestRepository(database);
        clearDatabase();

        Address address = new Address("Warszawa", "Lesna", "6A");
        GuestType guestType = new GuestTypePremium();
        validClient = new Client("Jan", "Kowalski", "1234", address, guestType);
        validClient2 = new Client("Jakub", "Pawlak", "4567", address, new GuestTypeExtra());
        invalidClient = new Client("Jan", "", "1234", address, guestType);
        invalidClient2 = new Client("Michał", "Kowal", "1234", address, guestType);
    }

    @Test
    public void saveGuestTest() {
        assertDoesNotThrow(() -> guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validClient)));
        Client addedClient = GuestMapper.mapGuestFromDbToDomain(guestRepository.findGuestByPersonalId("1234"));

        assertEquals(validClient, addedClient);
    }

    @Test
    public void saveInvalidGuestTest() {
        // Puste pole nazwiska - odrzucone przez $jsonSchema w MongoDB
        assertThrows(RepositoryException.class, () -> guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(invalidClient)));

        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validClient));

        // Brak unikalności personalId - odrzucone przez unikalny indeks w MongoDB
        assertThrows(RepositoryException.class, () -> guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(invalidClient2)));
    }

    @Test
    public void setGuestArchivedStatusTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validClient));
        Client foundClient = GuestMapper.mapGuestFromDbToDomain(guestRepository.findGuestByPersonalId("1234"));

        assertFalse(foundClient.isArchived());

        assertDoesNotThrow(() -> guestRepository.setGuestArchivedStatus("1234", true));
        foundClient = GuestMapper.mapGuestFromDbToDomain(guestRepository.findGuestByPersonalId("1234"));

        assertTrue(foundClient.isArchived());
    }

    @Test
    public void setGuestArchivedStatusRaceTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validClient));

        AtomicInteger successCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
        CyclicBarrier barrier = new CyclicBarrier(2);

        Runnable raceTask = () -> {
            try {
                barrier.await();
                guestRepository.setGuestArchivedStatus("1234", true);
                successCount.incrementAndGet();
            } catch (Exception e) {
                exceptions.add(e);
            }
        };

        CompletableFuture<Void> task1 = CompletableFuture.runAsync(raceTask);
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(raceTask);

        CompletableFuture.allOf(task1, task2).join();

        assertEquals(1, successCount.get());
        assertEquals(1, exceptions.size());
        assertInstanceOf(ConcurrentOperationException.class, exceptions.peek());
    }

    @Test
    public void findGuestByPersonalIdTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validClient));
        Client foundClient = GuestMapper.mapGuestFromDbToDomain(guestRepository.findGuestByPersonalId("1234"));
        assertNotNull(foundClient);
        assertEquals(validClient, foundClient);
    }

    @Test
    public void findInvalidGuestByPersonalIdTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validClient));
        assertThrows(EntityNotFoundException.class, () -> guestRepository.findGuestByPersonalId("1"));
    }

    @Test
    public void findAllGuestsTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validClient));
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validClient2));

        List<Client> allClients = new ArrayList<>();

        for (UserEnt userEnt : guestRepository.findAllGuests()) {
            allClients.add(GuestMapper.mapGuestFromDbToDomain(userEnt));
        }

        assertEquals(2, allClients.size());
        assertTrue(allClients.contains(validClient));
        assertTrue(allClients.contains(validClient2));
    }

    @Test
    public void guestRepositoryOperationsWhenPrimaryNodeIsDownTest() {
        guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validClient));

        String stoppedPrimary = null;
        try {
            stoppedPrimary = stopPrimaryNode();

            // 1. Zapis nowego gościa po awarii węzła Primary
            assertDoesNotThrow(() -> guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(validClient2)));

            // 2. Walidacja duplikatu na zdegradowanym klastrze
            assertThrows(RepositoryException.class, () -> guestRepository.saveGuest(GuestMapper.mapGuestFromDomainToDb(invalidClient2)));

            // 3. Wyścig o archiwizację gościa na zdegradowanym klastrze
            AtomicInteger successCount = new AtomicInteger(0);
            ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
            CyclicBarrier barrier = new CyclicBarrier(2);

            Runnable raceTask = () -> {
                try {
                    barrier.await();
                    guestRepository.setGuestArchivedStatus("1234", true);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptions.add(e);
                }
            };

            CompletableFuture.allOf(
                    CompletableFuture.runAsync(raceTask),
                    CompletableFuture.runAsync(raceTask)
            ).join();

            assertEquals(1, successCount.get());
            assertEquals(1, exceptions.size());
            assertInstanceOf(ConcurrentOperationException.class, exceptions.peek());
        } finally {
            startNode(stoppedPrimary);
        }

        // Weryfikacja spójności danych po powrocie węzła do klastra
        assertEquals(2, guestRepository.findAllGuests().size());
        assertTrue(guestRepository.findGuestByPersonalId("1234").isArchived());
    }
}