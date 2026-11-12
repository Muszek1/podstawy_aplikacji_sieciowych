package pl.lodz.p.model.domain;

import java.util.UUID;

public class ResourceAdministrator extends User{

    public ResourceAdministrator(UUID uuid, String login, String firstName, String lastName, boolean active, Long version) {
        super(uuid, login, firstName, lastName, active, version);
    }
}
