package pl.lodz.p.model.domain;

public class ResourceAdministrator extends User{

    public ResourceAdministrator(String id, String login, String firstName, String lastName, boolean active, Long version) {
        super(id, login, firstName, lastName, active, version);
    }
}
