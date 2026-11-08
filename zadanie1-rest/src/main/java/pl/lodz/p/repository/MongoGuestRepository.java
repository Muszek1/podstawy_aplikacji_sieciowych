package pl.lodz.p.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.EntityNotFoundException;
import pl.lodz.p.exception.RepositoryException;
import pl.lodz.p.model.db.UserEnt;

import java.util.ArrayList;
import java.util.List;

public class MongoGuestRepository {
    private final MongoCollection<UserEnt> guestsCollection;

    public MongoGuestRepository(MongoDatabase database) {
        this.guestsCollection = database.getCollection("guests", UserEnt.class);
    }

    public UserEnt saveGuest(UserEnt userEnt) {
        try {
            guestsCollection.insertOne(userEnt);
            return userEnt;
        } catch (Exception e) {
            throw new RepositoryException("Wystąpił błąd przy zapisie gościa o personalId = " + userEnt.getPersonalId() + ". " + e.getMessage());
        }
    }

    public void setGuestArchivedStatus(String personalId, boolean isArchived) {
        UpdateResult result;
        try {
            // Atomowa zmiana tylko wtedy, gdy obecny stan archived jest przeciwny do docelowego
            result = guestsCollection.updateOne(
                    Filters.and(
                            Filters.eq("personal_id", personalId),
                            Filters.eq("archived", !isArchived)
                    ),
                    Updates.set("archived", isArchived)
            );
        } catch (Exception e) {
            throw new RepositoryException("Wystąpił błąd przy zmianie statusu archiwizacji gościa o personalId = " + personalId + ", " + e.getMessage());
        }

        if (result.getModifiedCount() == 0) {
            // Sprawdzamy, czy gość istnieje (jeśli nie, poleci EntityNotFoundException)
            findGuestByPersonalId(personalId);
            // Jeśli istnieje, to znaczy, że stan archiwizacji był już wcześniej zmieniony
            throw new ConcurrentOperationException("Stan archiwizacji gościa o personalId = " + personalId + " został już zmieniony");
        }
    }

    public UserEnt findGuestByPersonalId(String personalId) {
        UserEnt userEnt = guestsCollection.find(Filters.eq("personal_id", personalId)).first();
        if (userEnt == null) {
            throw new EntityNotFoundException("Nie znaleziono gościa o personalId = " + personalId);
        }
        return userEnt;
    }

    public List<UserEnt> findAllGuests() {
        return guestsCollection.find().into(new ArrayList<>());
    }
}