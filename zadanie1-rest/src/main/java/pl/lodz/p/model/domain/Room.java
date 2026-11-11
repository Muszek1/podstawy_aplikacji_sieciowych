package pl.lodz.p.model.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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

    @Positive
    private int bedsAmount;

    @PositiveOrZero
    private double price;

    private boolean rented = false;

    @Version
    private Long version;

    @JsonCreator
    @PersistenceCreator
    public Room(UUID uuid, String number, int bedsAmount, double price) {
        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
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

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public int getBedsAmount() {
        return bedsAmount;
    }

    public void setBedsAmount(int bedsAmount) { this.bedsAmount = bedsAmount; }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
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
        return uuid != null && uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }

    public Object getVersion() {
        return version;
    }
}
