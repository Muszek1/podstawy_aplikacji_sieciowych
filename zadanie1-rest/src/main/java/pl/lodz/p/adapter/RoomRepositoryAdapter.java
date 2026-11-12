package pl.lodz.p.adapter;


import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import pl.lodz.p.exception.InvalidValueException;
import pl.lodz.p.exception.UniqueValueException;
import pl.lodz.p.model.domain.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RoomRepositoryAdapter implements RoomRepositoryAdapterInterface {

    private final MongoCollection<Document> collection;

    public RoomRepositoryAdapter(MongoClient mongoClient) {
        this.collection = mongoClient.getDatabase("rental_db").getCollection("rooms");

        this.collection.createIndex(
                new Document("number", 1),
                new IndexOptions().unique(true)
        );
    }

    @Override
    public Room create(String number, int bedsAmount, double price) {
        if (number == null || number.trim().isEmpty()) {
            throw new InvalidValueException("Numer pokoju nie może być pusty");
        }

        Long initialVersion = 0L;

        Document doc = new Document("number", number)
                .append("bedsAmount", bedsAmount)
                .append("price", price)
                .append("rented", false)
                .append("version", initialVersion);

        try {
            collection.insertOne(doc);

            String generatedId = doc.getObjectId("_id").toHexString();

            return new Room(generatedId, number, bedsAmount, price, false, initialVersion);

        } catch (MongoWriteException e) {
            if (e.getError().getCode() == 11000) {
                throw new UniqueValueException("Pokój o numerze " + number + " już istnieje");
            }
            throw e;
        }
    }

    @Override
    public Optional<Room> findById(String id) {
        Document doc = collection.find(Filters.eq("_id", new org.bson.types.ObjectId(id))).first();
        return Optional.ofNullable(mapToRoom(doc));
    }

    @Override
    public Optional<Room> findByNumber(String number) {
        Document doc = collection.find(Filters.eq("number", number)).first();
        return Optional.ofNullable(mapToRoom(doc));
    }

    @Override
    public boolean existsByNumber(String number) {
        return collection.countDocuments(Filters.eq("number", number)) > 0;
    }

    @Override
    public List<Room> findAll() {
        List<Room> rooms = new ArrayList<>();
        for (Document doc : collection.find()) {
            rooms.add(mapToRoom(doc));
        }
        return rooms;
    }

    @Override
    public void save(Room room) {
        Long currentVersion = room.getVersion();
        Long newVersion = currentVersion + 1;

        Bson query = Filters.and(
                Filters.eq("_id", new org.bson.types.ObjectId(room.getId())),
                Filters.eq("version", currentVersion)
        );

        Bson update = Updates.combine(
                Updates.set("number", room.getNumber()),
                Updates.set("bedsAmount", room.getBedsAmount()),
                Updates.set("price", room.getPrice()),
                Updates.set("rented", room.isRented()),
                Updates.set("version", newVersion)
        );

        UpdateResult result = collection.updateOne(query, update);

        if (result.getModifiedCount() == 0) {
            throw new OptimisticLockingFailureException("Konflikt! Obiekt został zmodyfikowany przez kogoś innego.");
        }

        room.setVersion(newVersion);
    }

    @Override
    public void delete(Room room) {
        collection.deleteOne(Filters.eq("_id", new org.bson.types.ObjectId(room.getId())));
    }

    @Override
    public void deleteAll() {
        collection.deleteMany(new org.bson.Document());
    }

    private Room mapToRoom(Document doc) {
        if (doc == null) return null;

        return new Room(
                doc.getObjectId("_id").toHexString(),
                doc.getString("number"),
                doc.getInteger("bedsAmount"),
                doc.getDouble("price"),
                doc.getBoolean("rented"),
                doc.getLong("version")
        );
    }
}
