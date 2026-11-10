package pl.lodz.p.model.domain;

import org.springframework.data.annotation.TypeAlias;

@TypeAlias("client")
public class Client extends User {
    public Client(String login, String firstName, String lastName, boolean active) {
        super(login, firstName, lastName, active);
    }
}
