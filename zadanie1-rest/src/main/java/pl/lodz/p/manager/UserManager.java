package pl.lodz.p.manager;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import pl.lodz.p.adapter.UserRepositoryAdapterInterface;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.*;

import java.util.List;
import java.util.UUID;

@Service
public class UserManager {
    private final UserRepositoryAdapterInterface adapter;

    public User createUser(
            Class<? extends User> userType,
            String login,
            String firstName,
            String lastName,
            boolean active) {

        if (adapter.existsByLogin(login)) {
            throw new UniqueValueException(
                    "Użytkownik o loginie " + login + " już istnieje"
            );
        }

        try {
            return adapter.create(
                    userType, login, firstName, lastName, active
            );
        }catch (DuplicateKeyException e) {
            throw new UniqueValueException(
                    "Konflikt unikalności danych użytkownika"
            );
        }
    }

    public void updateUser(UUID uuid, String firstName, String lastName) {
        User user = findUserByUuid(uuid);

        user.setFirstName(firstName);
        user.setLastName(lastName);

        adapter.save(user);
    }

    public UserManager(UserRepositoryAdapterInterface adapter) {
        this.adapter = adapter;
    }

    public void archiveUser(UUID uuid) {
        User user = findUserByUuid(uuid);
        user.setActive(false);
        adapter.save(user);
    }

    public void activateUser(UUID uuid) {
        User user = findUserByUuid(uuid);
        user.setActive(true);
        adapter.save(user);
    }

    public User findUserByLogin(String login) {
        return adapter.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika o loginie: " + login));
    }
    public User findUserByUuid(UUID uuid) {
        return adapter.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika o uuid: " + uuid));
    }



    public List<User> findUsersByMatchingLogin(String loginToMatch) {
        return adapter.findByLoginContainingIgnoreCase(loginToMatch);
    }

    public List<User> findAllUsers() {
        return adapter.findAll();
    }

}
