package pl.lodz.p.adapter;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.exception.InvalidValueException;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.ResourceAdministrator;
import pl.lodz.p.model.domain.User;
import pl.lodz.p.model.domain.UserAdministrator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepositoryAdapterInterface extends MongoRepository<User, UUID> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
    List<User> findByLoginContainingIgnoreCase(String loginToMatch);
    default User create(
            Class<? extends User> userType,
            String login,
            String firstName,
            String lastName,
            boolean active) {

        if (login == null || login.trim().isEmpty()) {
            throw new InvalidValueException("Login nie może być pusty");
        }

        UUID uuid = UUID.randomUUID();
        User user;

        if (userType == Client.class) {
            user = new Client(uuid, login, firstName, lastName, active);
        } else if (userType == ResourceAdministrator.class) {
            user = new ResourceAdministrator(
                    uuid, login, firstName, lastName, active
            );
        } else if (userType == UserAdministrator.class) {
            user = new UserAdministrator(
                    uuid, login, firstName, lastName, active
            );
        } else {
            throw new IllegalArgumentException("Nieobsługiwany typ użytkownika");
        }

        return insert(user);
    }
}