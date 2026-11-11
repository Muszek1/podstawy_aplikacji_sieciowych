package pl.lodz.p.model.domain;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.TypeAlias;

import java.util.UUID;

@TypeAlias("client")
public class Client extends User {

    @PersistenceCreator
    public Client(UUID uuid, String login, String firstName, String lastName, boolean active) {
        super(uuid, login, firstName, lastName, active);
    }

    public Client(String login, String firstName, String lastName, boolean active) {
        super(login, firstName, lastName, active);
    }
}
