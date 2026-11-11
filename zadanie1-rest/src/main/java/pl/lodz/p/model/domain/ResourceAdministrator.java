package pl.lodz.p.model.domain;

import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.TypeAlias;

import java.util.UUID;

@TypeAlias("resourceAdmin")
public class ResourceAdministrator extends User{

    @PersistenceCreator
    public ResourceAdministrator(UUID uuid, String login, String firstName, String lastName, boolean active) {
        super(uuid, login, firstName, lastName, active);
    }

    public ResourceAdministrator(String login, String firstName, String lastName, boolean active) {
        super(login, firstName, lastName, active);
    }
}
