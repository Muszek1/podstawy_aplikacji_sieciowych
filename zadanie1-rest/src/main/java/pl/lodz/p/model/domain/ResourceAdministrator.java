package pl.lodz.p.model.domain;

import org.springframework.data.annotation.TypeAlias;

@TypeAlias("resourceAdmin")
public class ResourceAdministrator extends User{
    public ResourceAdministrator(String login, String firstName, String lastName, boolean active) {
        super(login, firstName, lastName, active);
    }
}
