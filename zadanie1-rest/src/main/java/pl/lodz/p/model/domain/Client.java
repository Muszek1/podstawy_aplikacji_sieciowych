package pl.lodz.p.model.domain;

import java.util.UUID;

public class Client extends User {
    public Client(UUID uuid, String login, String firstName, String lastName, boolean active, Long version) {
        super(uuid, login, firstName, lastName, active, version);
    }
}