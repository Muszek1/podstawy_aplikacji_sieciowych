package pl.lodz.p.model.domain;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Objects;


public class Rent {

    @NotNull
    private final LocalDateTime beginTime;
    private LocalDateTime endTime;

    @NotNull
    private final Client client;
    @NotNull
    private final Room room;
    private final String id;
    private Long version;

    public Rent(String id, LocalDateTime beginTime, LocalDateTime endTime, Client client, Room room, Long version) {
        this.id = Objects.requireNonNull(id);
        this.beginTime = Objects.requireNonNull(beginTime);
        this.endTime = endTime;
        this.client = Objects.requireNonNull(client);
        this.room = Objects.requireNonNull(room);
        this.version = version != null ? version : 0L;
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

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Rent: " + "UUID: " + id + '\n' +
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
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long newVersion) {
        this.version = newVersion;
    }
}
