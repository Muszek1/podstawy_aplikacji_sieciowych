package pl.lodz.p.model.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

public class Rent {
    private final LocalDateTime beginTime;
    private LocalDateTime endTime;
    private final Client client;
    private final Room room;
    private final UUID uuid;

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
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
