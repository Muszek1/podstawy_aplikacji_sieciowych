package pl.lodz.p.adapter;

import pl.lodz.p.model.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserRepositoryAdapterInterface {
    User create(Class<? extends User> userType, String login, String firstName, String lastName, boolean active);

    Optional<User> findById(UUID uuid);

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    List<User> findByLoginContainingIgnoreCase(String loginToMatch);

    List<User> findAll();

    void save(User user);

    void delete(User user);

    void deleteAll();
}