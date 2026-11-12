package pl.lodz.p.model.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;


public abstract class User {
    private final String id;

    @NotBlank
    private String login;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private boolean active;

    @NotNull
    private Long version;

    public User(String id, String login, String firstName, String lastName, boolean active, Long version) {

        if (login == null || login.isBlank()) {
            throw new IllegalArgumentException("login cannot be null or blank");
        }

        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.active = active;
        this.version = version != null ? version : 0L;
    }

    public String getId() {
        return id;
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
        return id != null && id.equals(other.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(id);
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long newVersion) {
        this.version = newVersion;
    }

}
