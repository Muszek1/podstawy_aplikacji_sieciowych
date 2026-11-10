package pl.lodz.p.adapter;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.model.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepositoryAdapterInterface extends MongoRepository<User, UUID> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
    List<User> findByLoginContainingIgnoreCase(String loginToMatch);
}