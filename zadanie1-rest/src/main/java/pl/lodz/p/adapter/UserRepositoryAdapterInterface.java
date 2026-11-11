package pl.lodz.p.adapter;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
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
    default User create(User user) {
        UUID uuid = UUID.randomUUID();
        User newUser;

        if (user instanceof Client) {
            newUser = new Client(
                    uuid, user.getLogin(), user.getFirstName(),
                    user.getLastName(), user.isActive()
            );
        } else if (user instanceof ResourceAdministrator) {
            newUser = new ResourceAdministrator(
                    uuid, user.getLogin(), user.getFirstName(),
                    user.getLastName(), user.isActive()
            );
        } else if (user instanceof UserAdministrator) {
            newUser = new UserAdministrator(
                    uuid, user.getLogin(), user.getFirstName(),
                    user.getLastName(), user.isActive()
            );
        } else {
            throw new IllegalArgumentException("Nieobsługiwany typ użytkownika");
        }

        return insert(newUser);
    }
}