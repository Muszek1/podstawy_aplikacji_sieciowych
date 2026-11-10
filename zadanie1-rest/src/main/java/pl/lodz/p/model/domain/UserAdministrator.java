package pl.lodz.p.model.domain;

import org.springframework.data.annotation.TypeAlias;

@TypeAlias("userAdmin")
public class UserAdministrator extends User{
    public UserAdministrator(String login, String firstName, String lastName, boolean active) {
        super(login, firstName, lastName, active);
    }
}
