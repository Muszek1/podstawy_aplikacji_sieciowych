package pl.lodz.p.model.domain;

import org.springframework.data.annotation.TypeAlias;

import java.util.UUID;

@TypeAlias("client")
public class Client extends User {
    public Client(UUID uuid, String login, String firstName, String lastName, boolean active) {
        super(uuid, login, firstName, lastName, active);
    }
}
