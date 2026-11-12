package pl.lodz.p.model.domain;

import java.util.Objects;
import java.util.UUID;


public class Room {

    private String id;
    private String number;
    private int bedsAmount;
    private double price;
    private boolean rented = false;
    private Long version;

    public Room(String id, String number, int bedsAmount, double price, boolean rented, Long version) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.number = number;
        this.bedsAmount = bedsAmount;
        this.price = price;
        this.rented = rented;
        this.version = version != null ? version : 0L;
    }

    public String getId() {
        return id;
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
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
