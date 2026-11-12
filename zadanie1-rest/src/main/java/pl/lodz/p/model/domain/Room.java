package pl.lodz.p.model.domain;

import java.util.Objects;
import java.util.UUID;


public class Room {

    private UUID uuid;
    private String number;
    private int bedsAmount;
    private double price;
    private boolean rented = false;
    private Long version;

    public Room(UUID uuid, String number, int bedsAmount, double price, boolean rented, Long version) {
        this.uuid = Objects.requireNonNull(uuid, "uuid cannot be null");
        this.number = number;
        this.bedsAmount = bedsAmount;
        this.price = price;
        this.rented = rented;
        this.version = version != null ? version : 0L;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
