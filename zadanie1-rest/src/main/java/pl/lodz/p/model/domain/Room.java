package pl.lodz.p.model.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
import java.util.UUID;

@Document
public class Room {
    @Id
    private UUID uuid;

    @NotBlank
    @Indexed(unique = true)
    private String number;

    @NotNull
    private int bedsAmount;

    @NotNull
    private double price;

    @Version
    private Long version;

    @PersistenceCreator
    public Room(UUID uuid, String number, int bedsAmount, double price) {
        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
        this.number = number;
        this.bedsAmount = bedsAmount;
        this.price = price;
    }

    @JsonCreator
    public Room(@JsonProperty("number") String number, @JsonProperty("bedsAmount") int bedsAmount, @JsonProperty("price") double price) {

        this.uuid = UUID.randomUUID();
        this.number = number;
        this.bedsAmount = bedsAmount;
        this.price = price;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getPrice() {
        return price;
    }

    public void setBasePrice(double price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public int getBedsAmount() {
        return bedsAmount;
    }

    public void setBedsAmount(int bedsAmount) { this.bedsAmount = bedsAmount; }

    public double getBasePrice() {
        return price;
    }


    @Override
    public String toString() {
        return "Room: " +
                "number: '" + number + '\'' + '\n' +
                "bedsAmount: " + bedsAmount + '\n' +
                "price: " + price + '\n';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Room other = (Room) object;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
