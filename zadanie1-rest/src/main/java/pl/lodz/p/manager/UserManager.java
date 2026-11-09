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

public class UserManager {
    private final GuestRepositoryAdapterInterface adapter;

    public UserManager(MongoDatabase database) {
        MongoGuestRepository repository = new MongoGuestRepository(database);
        this.adapter = new MongoGuestRepositoryAdapter(repository);
    }

    public Client createUser(String firstName, String lastName, String personalId, String city, String street, String number, String userType) {
        try {
            adapter.findGuestByLogin(personalId);
        } catch (EntityNotFoundException _) {
            GuestType guestType = mapNameToGuestType(guestTypeName);
            Client client = new Client(firstName, lastName, personalId, address, guestType);
            return adapter.saveGuest(client);
        }

        throw new UniqueValueException("Gość o personalId = " + personalId + " już istnieje");
    }

    public void updateUser(String login, String firstName, String lastName) {

    }

    public UserManager(GuestRepositoryAdapterInterface adapter) {
        this.adapter = adapter;
    }

    public void archiveUser(String personalId) {
        adapter.setGuestArchivedStatus(personalId, true);
    }

    public void activateUser(String personalId) {
        adapter.setGuestArchivedStatus(personalId, false);
    }

    public User findUserByLogin(String login) {
        return adapter.findGuestByLogin(login);
    }
    public List<User> findUsersByMatchingLogin(String loginToMatch) {return null;}

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
