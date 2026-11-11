package pl.lodz.p.model.domain;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.TypeAlias;

import java.util.UUID;

@TypeAlias("userAdmin")
public class UserAdministrator extends User{

    @PersistenceCreator
    public UserAdministrator(UUID uuid, String login, String firstName, String lastName, boolean active) {
        super(uuid, login, firstName, lastName, active);
    }

    public UserAdministrator(String login, String firstName, String lastName, boolean active) {
        super(login, firstName, lastName, active);
    }
}
