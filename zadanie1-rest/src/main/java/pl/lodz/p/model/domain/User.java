package pl.lodz.p.model.domain;

import org.springframework.data.annotation.Id;

import java.util.Objects;
import java.util.UUID;

public abstract class User {
    @Id
    private final UUID uuid;
    private String login;
    private String firstName;
    private String lastName;
    private boolean active;


    public User(UUID uuid, String login, String firstName, String lastName, boolean active) {

        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login cannot be null or blank");
        }

        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {

        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login cannot be null or blank");
        }

        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof User other)) return false;
        return uuid.equals(other.uuid);
    }

    @Override
    public final int hashCode() {
        return uuid.hashCode();
    }

}
