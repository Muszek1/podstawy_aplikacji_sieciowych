package pl.lodz.p.model.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private final String initialFirstName = "Jan";
    private final String initialLastName = "Kowalski";
    private final String initialPersonalID = "1234";
    private final String newFirstName = "Krzysztof";
    private final String newLastName = "Nowak";

    private Address initialAddress;
    private Address newAddress;
    private GuestType initialGuestType;
    private GuestType newGuestType;

    @BeforeEach
    void setUp() {
        initialAddress = new Address("Radomsko", "Ładna", "45");
        newAddress = new Address("Warszawa", "Leśna", "12");
        initialGuestType = new GuestTypeDefault();
        newGuestType = new GuestTypePremium();
    }

    @Test
    void constructorTests() {
        Client client = new Client(initialFirstName, initialLastName, initialPersonalID, initialAddress, initialGuestType);

        assertEquals(initialFirstName, client.getFirstName());
        assertEquals(initialLastName, client.getLastName());
        assertEquals(initialPersonalID, client.getPersonalId());
        assertEquals(initialAddress, client.getAddress());
        assertFalse(client.isArchived());
        assertEquals(initialGuestType, client.getGuestType());
    }

    @Test
    void settersTests() {
        Client client = new Client(initialFirstName, initialLastName, initialPersonalID, initialAddress, initialGuestType);

        client.setFirstName(newFirstName);
        assertEquals(newFirstName, client.getFirstName());

        client.setLastName(newLastName);
        assertEquals(newLastName, client.getLastName());

        client.setAddress(newAddress);
        assertEquals(newAddress, client.getAddress());

//        client.setArchivedStatus(true);
//        assertTrue(client.isArchived());

        client.setGuestType(newGuestType);
        assertEquals(newGuestType, client.getGuestType());
    }
}