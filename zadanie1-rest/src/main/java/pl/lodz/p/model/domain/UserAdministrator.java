package pl.lodz.p.model.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.TypeAlias;

import java.util.UUID;

@TypeAlias("userAdmin")
public class UserAdministrator extends User{

    @JsonCreator
    @PersistenceCreator
    public UserAdministrator(
            @JsonProperty("uuid") UUID uuid,
            @JsonProperty("login") String login,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("active") boolean active) {

        super(uuid, login, firstName, lastName, active);
    }
}
