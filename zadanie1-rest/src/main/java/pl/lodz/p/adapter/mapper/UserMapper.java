package pl.lodz.p.adapter.mapper;

import pl.lodz.p.model.db.*;
import pl.lodz.p.model.domain.*;

public class UserMapper {

    public static User mapUserFromDbToDomain(UserEnt entity) {
        if (entity instanceof ClientEnt) {
            return new Client(
                    entity.getUuid(), entity.getLogin(),
                    entity.getFirstName(), entity.getLastName(),
                    entity.isActive()
            );
        }

        if (entity instanceof UserAdministratorEnt) {
            return new UserAdministrator(
                    entity.getUuid(), entity.getLogin(),
                    entity.getFirstName(), entity.getLastName(),
                    entity.isActive()
            );
        }

        if (entity instanceof ResourceAdministratorEnt) {
            return new ResourceAdministrator(
                    entity.getUuid(), entity.getLogin(),
                    entity.getFirstName(), entity.getLastName(),
                    entity.isActive()
            );
        }

        throw new IllegalArgumentException("Unknown user entity type");
    }

    public static UserEnt mapUserFromDomainToDb(User user) {
        UserEnt entity;

        if (user instanceof Client) {
            entity = new ClientEnt();
        } else if (user instanceof UserAdministrator) {
            entity = new UserAdministratorEnt();
        } else if (user instanceof ResourceAdministrator) {
            entity = new ResourceAdministratorEnt();
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }

        entity.setUuid(user.getUuid());
        entity.setLogin(user.getLogin());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setActive(user.isActive());

        return entity;
    }
}