package pl.lodz.p.model.domain;

import java.util.Objects;
import java.util.UUID;

public class Room {
    private final String number;
    private int bedsAmount;
    private double price;
    private boolean rented;
    private final UUID uuid;

    public Room(UUID uuid, String number, int bedsAmount, double price) {
        this.number = number;
        this.bedsAmount = bedsAmount;
        this.price = price;
        this.uuid = Objects.requireNonNull(uuid);
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

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "Room: " +
                "UUID: '" + uuid + '\'' + '\n' +
                "number: '" + number + '\'' + '\n' +
                "bedsAmount: " + bedsAmount + '\n' +
                "price: " + price + '\n' +
                "rented: " + rented + '\n';
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
