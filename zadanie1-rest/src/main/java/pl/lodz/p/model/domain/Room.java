package pl.lodz.p.model.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Room {
    @Id
    private final String number;
    private int bedsAmount;
    private double price;

    public Room(String number, int bedsAmount, double price) {
        this.number = number;
        this.bedsAmount = bedsAmount;
        this.price = price;
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
        return number.equals(other.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }
}
