package pl.lodz.p.manager;

import com.mongodb.client.MongoDatabase;
import pl.lodz.p.adapter.GuestRepositoryAdapterInterface;
import pl.lodz.p.adapter.MongoGuestRepositoryAdapter;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.InvalidValueException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.*;
import pl.lodz.p.repository.MongoGuestRepository;

import java.util.List;

public class GuestManager {
    private final GuestRepositoryAdapterInterface adapter;

    public GuestManager(MongoDatabase database) {
        MongoGuestRepository repository = new MongoGuestRepository(database);
        this.adapter = new MongoGuestRepositoryAdapter(repository);
    }

    public Client registerGuest(String firstName, String lastName, String personalId, String city, String street, String number, String guestTypeName) {
        try {
            adapter.findGuestByPersonalId(personalId);
        } catch (EntityNotFoundException _) {
            Address address = new Address(city, street, number);
            GuestType guestType = mapNameToGuestType(guestTypeName);
            Client client = new Client(firstName, lastName, personalId, address, guestType);
            return adapter.saveGuest(client);
        }

        throw new UniqueValueException("Gość o personalId = " + personalId + " już istnieje");
    }

    public GuestManager(GuestRepositoryAdapterInterface adapter) {
        this.adapter = adapter;
    }

    public void unregisterGuest(String personalId) {
        adapter.setGuestArchivedStatus(personalId, true);
    }

    public void activateGuest(String personalId) {
        adapter.setGuestArchivedStatus(personalId, false);
    }

    public Client findGuestByPersonalId(String personalId) {
        return adapter.findGuestByPersonalId(personalId);
    }

    public List<Client> findAllGuests() {
        return adapter.findAllGuests();
    }



    private GuestType mapNameToGuestType(String guestTypeName) {
        if (guestTypeName.equalsIgnoreCase("default")) {
            return new GuestTypeDefault();
        } else if (guestTypeName.equalsIgnoreCase("extra")) {
            return new GuestTypeExtra();
        } else if (guestTypeName.equalsIgnoreCase("premium")) {
            return new GuestTypePremium();
        }

        throw new InvalidValueException("Typ gościa " + guestTypeName + " nie jest poprawny");
    }
}
