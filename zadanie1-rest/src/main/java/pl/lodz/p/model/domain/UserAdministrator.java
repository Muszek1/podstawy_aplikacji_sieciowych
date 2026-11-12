package pl.lodz.p.model.domain;

public class UserAdministrator extends User{

    public UserAdministrator(String id, String login, String firstName, String lastName, boolean active, Long version) {
        super(id, login, firstName, lastName, active, version);
    }
}


