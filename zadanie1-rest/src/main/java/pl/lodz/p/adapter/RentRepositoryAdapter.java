package pl.lodz.p.adapter;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;
import pl.lodz.p.model.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RentRepositoryAdapter implements RentRepositoryAdapterInterface {

    private final MongoCollection<Document> collection;
    private final UserRepositoryAdapterInterface userRepository;
    private final RoomRepositoryAdapterInterface roomRepository;

    public RentRepositoryAdapter(MongoClient mongoClient,
                                 UserRepositoryAdapterInterface userRepository,
                                 RoomRepositoryAdapterInterface roomRepository) {
        this.collection = mongoClient.getDatabase("rental_db").getCollection("rents");
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Rent create(LocalDateTime beginTime, Client client, Room room) {
        if (beginTime == null) {
            throw new IllegalArgumentException("Czas rozpoczęcia wynajmu nie może być pusty");
        }
        if (client == null) {
            throw new IllegalArgumentException("Klient nie może być pusty");
        }
        if (room == null) {
            throw new IllegalArgumentException("Pokój nie może być pusty");
        }

        UUID uuid = UUID.randomUUID();
        Long initialVersion = 0L;

        Document doc = new Document("_id", uuid.toString())
                .append("beginTime", beginTime.toString())
                .append("endTime", null)
                .append("clientId", client.getUuid().toString())
                .append("roomId", room.getUuid().toString())
                .append("version", initialVersion);

        collection.insertOne(doc);

        return new Rent(uuid, beginTime, null, client, room, initialVersion);
    }

    @Override
    public Optional<Rent> findById(UUID uuid) {
        Document doc = collection.find(Filters.eq("_id", uuid.toString())).first();
        return Optional.ofNullable(mapToRent(doc));
    }

    @Override
    public List<Rent> findByClientAndEndTimeIsNull(Client client) {
        return findByFilters(Filters.and(
                Filters.eq("clientId", client.getUuid().toString()),
                Filters.eq("endTime", null)
        ));
    }

    @Override
    public List<Rent> findByClientAndEndTimeIsNotNull(Client client) {
        return findByFilters(Filters.and(
                Filters.eq("clientId", client.getUuid().toString()),
                Filters.ne("endTime", null) // Nie jest nulem
        ));
    }

    @Override
    public List<Rent> findByRoomAndEndTimeIsNull(Room room) {
        return findByFilters(Filters.and(
                Filters.eq("roomId", room.getUuid().toString()),
                Filters.eq("endTime", null)
        ));
    }

    @Override
    public List<Rent> findByRoomAndEndTimeIsNotNull(Room room) {
        return findByFilters(Filters.and(
                Filters.eq("roomId", room.getUuid().toString()),
                Filters.ne("endTime", null)
        ));
    }

    @Override
    public List<Rent> findAll() {
        return findByFilters(new Document());
    }

    @Override
    public void save(Rent rent) {
        Long currentVersion = rent.getVersion();
        Long newVersion = currentVersion + 1;

        Bson query = Filters.and(
                Filters.eq("_id", rent.getUuid().toString()),
                Filters.eq("version", currentVersion)
        );

        String endTimeStr = rent.getEndTime() != null ? rent.getEndTime().toString() : null;

        Bson update = Updates.combine(
                Updates.set("endTime", endTimeStr),
                Updates.set("version", newVersion)
        );

        UpdateResult result = collection.updateOne(query, update);

        if (result.getModifiedCount() == 0) {
            throw new OptimisticLockingFailureException("Konflikt! Wynajem został w międzyczasie zmodyfikowany.");
        }

        rent.setVersion(newVersion);
    }

    @Override
    public void delete(Rent rent) {
        collection.deleteOne(Filters.eq("_id", rent.getUuid().toString()));
    }

    @Override
    public void deleteAll() {
        collection.deleteMany(new org.bson.Document());
    }

    private List<Rent> findByFilters(Bson filters) {
        List<Rent> rents = new ArrayList<>();
        for (Document doc : collection.find(filters)) {
            rents.add(mapToRent(doc));
        }
        return rents;
    }

    private Rent mapToRent(Document doc) {
        if (doc == null) return null;

        UUID uuid = UUID.fromString(doc.getString("_id"));
        LocalDateTime beginTime = LocalDateTime.parse(doc.getString("beginTime"));

        String endTimeStr = doc.getString("endTime");
        LocalDateTime endTime = endTimeStr != null ? LocalDateTime.parse(endTimeStr) : null;

        Long version = doc.getLong("version");



        UUID clientId = UUID.fromString(doc.getString("clientId"));
        UUID roomId = UUID.fromString(doc.getString("roomId"));

        User user = userRepository.findById(clientId)
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono klienta przypisanego do wynajmu"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono pokoju przypisanego do wynajmu"));

        return new Rent(uuid, beginTime, endTime, (Client) user, room, version);
    }
}
