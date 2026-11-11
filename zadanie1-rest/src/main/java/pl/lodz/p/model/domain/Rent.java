package pl.lodz.p.model.domain;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Document(collection = "rents")
public class Rent {
    @NotNull
    private final LocalDateTime beginTime;

    private LocalDateTime endTime;

    @NotNull
    @DocumentReference(collection = "user", lazy = true)
    private final Client client;

    @NotNull
    @DocumentReference(lazy = true)
    private final Room room;

    @Id
    private final UUID uuid;

    @Version
    private Long version;


    @PersistenceCreator
    public Rent(UUID uuid, LocalDateTime beginTime, LocalDateTime endTime, Client client, Room room) {
        this.uuid = Objects.requireNonNull(uuid);
        this.beginTime = Objects.requireNonNull(beginTime);
        this.endTime = endTime;
        this.client = Objects.requireNonNull(client);
        this.room = Objects.requireNonNull(room);
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime time) {
        this.endTime = Objects.requireNonNull(time);
    }

    public Client getClient() {
        return client;
    }

    public Room getRoom() {
        return room;
    }

    public boolean isActive() {
        return endTime == null;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Rent: " + "UUID: " + uuid + '\n' +
                "beginTime: " + beginTime + '\n' +
                "endTime: " + endTime + '\n' +
                "client: " + client + '\n' +
                "room: " + room + '\n';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Rent other = (Rent) object;
        return uuid != null && uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
