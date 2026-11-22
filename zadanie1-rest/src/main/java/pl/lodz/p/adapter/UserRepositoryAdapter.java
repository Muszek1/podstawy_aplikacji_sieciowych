package pl.lodz.p.adapter;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.conversions.Bson;
import pl.lodz.p.exception.ConcurrentOperationException;
import pl.lodz.p.exception.InvalidValueException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@ApplicationScoped
public class UserRepositoryAdapter implements UserRepositoryAdapterInterface{

    private final MongoCollection<Document> collection;

    public UserRepositoryAdapter(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("pasdb").getCollection("users");

        this.collection.createIndex(
                new Document("login", 1),
                new IndexOptions().unique(true)
        );
    }

    @Override
    public User create(Class<? extends User> userType, String login, String firstName, String lastName, boolean active) {
        if (login == null || login.trim().isEmpty()) {
            throw new InvalidValueException("Login nie może być pusty");
        }

        Long initialVersion = 0L;
        String clazzName = userType.getSimpleName();

        Document doc = new Document("login", login)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("active", active)
                .append("_clazz", clazzName)
                .append("version", initialVersion);

        try {
            collection.insertOne(doc);
        } catch (MongoWriteException e) {
            if (e.getError().getCode() == 11000) {
                throw new UniqueValueException("Użytkownik o loginie " + login + " już istnieje");
            }
            throw e;
        }

        return mapToUser(doc);
    }

    @Override
    public Optional<User> findById(String id) {
        Document doc = collection.find(Filters.eq("_id", new org.bson.types.ObjectId(id))).first();
        return Optional.ofNullable(mapToUser(doc));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        Document doc = collection.find(Filters.eq("login", login)).first();
        return Optional.ofNullable(mapToUser(doc));
    }

    @Override
    public boolean existsByLogin(String login) {
        return collection.countDocuments(Filters.eq("login", login)) > 0;
    }

    @Override
    public List<User> findByLoginContainingIgnoreCase(String loginToMatch) {
        List<User> users = new ArrayList<>();
        Pattern pattern = Pattern.compile(Pattern.quote(loginToMatch), Pattern.CASE_INSENSITIVE);

        for (Document doc : collection.find(Filters.regex("login", pattern))) {
            users.add(mapToUser(doc));
        }
        return users;
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (Document doc : collection.find()) {
            users.add(mapToUser(doc));
        }
        return users;
    }

    @Override
    public void save(User user) {
        Long currentVersion = user.getVersion();
        Long newVersion = currentVersion + 1;

        Bson query = Filters.and(
                Filters.eq("_id", new org.bson.types.ObjectId(user.getId())),
                Filters.eq("version", currentVersion)
        );

        Bson update = Updates.combine(
                Updates.set("login", user.getLogin()),
                Updates.set("firstName", user.getFirstName()),
                Updates.set("lastName", user.getLastName()),
                Updates.set("active", user.isActive()),
                Updates.set("version", newVersion)
        );

        UpdateResult result = collection.updateOne(query, update);

        if (result.getModifiedCount() == 0) {
            throw new ConcurrentOperationException("Konflikt! Użytkownik został w międzyczasie zmodyfikowany.");
        }

        user.setVersion(newVersion);
    }

    @Override
    public void delete(User user) {
        collection.deleteOne(Filters.eq("_id", new org.bson.types.ObjectId(user.getId())));
    }

    @Override
    public void deleteAll() {
        collection.deleteMany(new org.bson.Document());
    }

    private User mapToUser(Document doc) {
        if (doc == null) return null;


        String id = doc.getObjectId("_id").toHexString();
        String login = doc.getString("login");
        String firstName = doc.getString("firstName");
        String lastName = doc.getString("lastName");
        boolean active = doc.getBoolean("active");
        Long version = doc.getLong("version");
        String clazz = doc.getString("_clazz");

        if ("Client".equals(clazz)) {
            return new Client(id, login, firstName, lastName, active, version);
        } else if ("ResourceAdministrator".equals(clazz)) {
            return new ResourceAdministrator(id, login, firstName, lastName, active, version);
        } else if ("UserAdministrator".equals(clazz)) {
            return new UserAdministrator(id, login, firstName, lastName, active, version);
        }

        throw new IllegalStateException("Nieznany typ użytkownika: " + clazz);
    }
}
