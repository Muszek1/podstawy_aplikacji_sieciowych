package pl.lodz.p.manager;

import org.springframework.stereotype.Service;
import pl.lodz.p.adapter.UserRepositoryAdapterInterface;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.InvalidValueException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.*;

import java.util.List;

@Service
public class UserManager {
    private final UserRepositoryAdapterInterface adapter;

    public User createUser(User user) {
        if (adapter.existsByLogin(user.getLogin())) {
            throw new UniqueValueException("Użytkownik o loginie = " + user.getLogin() + " już istnieje");
        }

        return adapter.save(user);
    }

    public void updateUser(User user) {
        User exisitingUser = findUserByLogin(user.getLogin());
        exisitingUser.setFirstName(user.getFirstName());
        exisitingUser.setLastName(user.getLastName());
        adapter.save(exisitingUser);
    }

    public UserManager(UserRepositoryAdapterInterface adapter) {
        this.adapter = adapter;
    }

    public void archiveUser(String login) {
        User user = findUserByLogin(login);
        user.setActive(false);
        adapter.save(user);
    }

    public void activateUser(String login) {
        User user = findUserByLogin(login);
        user.setActive(true);
        adapter.save(user);
    }

    public User findUserByLogin(String login) {
        return adapter.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono użytkownika o loginie: " + login));
    }

    public List<User> findUsersByMatchingLogin(String loginToMatch) {
        return adapter.findByLoginContainingIgnoreCase(loginToMatch);
    }

    public List<User> findAllUsers() {
        return adapter.findAll();
    }

}
