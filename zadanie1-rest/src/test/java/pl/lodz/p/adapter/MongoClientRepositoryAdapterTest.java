package pl.lodz.p.adapter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lodz.p.AbstractDatabaseTest;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.MongoGuestRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class MongoClientRepositoryAdapterTest extends AbstractDatabaseTest {
    private MongoGuestRepositoryAdapter adapter;
    private Client validClient;
    private Client validClient2;
    private Client invalidClient;
    private Client invalidClient2;

    @BeforeEach
    public void setup() {
        MongoGuestRepository guestRepository = new MongoGuestRepository(database);
        adapter = new MongoGuestRepositoryAdapter(guestRepository);
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
        assertDoesNotThrow(() -> adapter.saveGuest(validClient));
        Client addedClient = adapter.findGuestByLogin("1234");

        assertEquals(validClient, addedClient);
    }

    @Test
    public void saveInvalidGuestTest() {
        // Puste pole nazwiska
        assertThrows(RepositoryException.class, () -> adapter.saveGuest(invalidClient));

        adapter.saveGuest(validClient);

        // Brak unikalności personalId
        assertThrows(RepositoryException.class, () -> adapter.saveGuest(invalidClient2));

    }

    @Test
    public void setGuestArchivedStatusTest() {
        adapter.saveGuest(validClient);
        Client foundClient = adapter.findGuestByLogin("1234");

        assertFalse(foundClient.isArchived());

        assertDoesNotThrow(() -> adapter.setGuestArchivedStatus("1234", true));
        foundClient =adapter.findGuestByLogin("1234");

        assertTrue(foundClient.isArchived());
    }

    @Test
    public void setGuestArchivedStatusRaceTest() {
        adapter.saveGuest(validClient);

        AtomicInteger successCount = new AtomicInteger(0);
        ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();
        CyclicBarrier barrier = new CyclicBarrier(2);

        Runnable raceTask = () -> {
            try {
                barrier.await();
                adapter.setGuestArchivedStatus("1234", true);
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
        adapter.saveGuest(validClient);

        Client foundClient = adapter.findGuestByLogin("1234");
        assertNotNull(foundClient);
        assertEquals(validClient, foundClient);
    }

    @Test
    public void findInvalidGuestByPersonalIdTest() {
        adapter.saveGuest(validClient);
        assertThrows(EntityNotFoundException.class, () -> adapter.findGuestByLogin("1"));
    }

    @Test
    public void findAllGuestsTest() {
        adapter.saveGuest(validClient);
        adapter.saveGuest(validClient2);

        List<Client> allClients = adapter.findAllGuests();

        assertEquals(2, allClients.size());
        assertTrue(allClients.contains(validClient));
        assertTrue(allClients.contains(validClient2));

    }

    @Test
    public void guestAdapterOperationsWhenPrimaryNodeIsDownTest() {
        adapter.saveGuest(validClient);

        String stoppedPrimary = null;
        try {
            stoppedPrimary = stopPrimaryNode();

            assertDoesNotThrow(() -> adapter.saveGuest(validClient2));
            assertThrows(RepositoryException.class, () -> adapter.saveGuest(invalidClient2));

            assertDoesNotThrow(() -> adapter.setGuestArchivedStatus("1234", true));
            assertTrue(adapter.findGuestByLogin("1234").isArchived());
        } finally {
            startNode(stoppedPrimary);
        }

        assertEquals(2, adapter.findAllGuests().size());
        assertTrue(adapter.findGuestByLogin("1234").isArchived());
    }

}