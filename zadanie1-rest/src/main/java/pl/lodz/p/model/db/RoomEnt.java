package pl.lodz.p.model.db;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class RoomEnt extends AbstractEntity {

    @BsonProperty("number")
    private String number;

    @BsonProperty("beds")
    private int bedsAmount;

    @BsonProperty("price")
    private double price;

    @BsonProperty("rented")
    private boolean rented;

    public RoomEnt() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getBedsAmount() {
        return bedsAmount;
    }

    public void setBedsAmount(int bedsAmount) {
        this.bedsAmount = bedsAmount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }
}