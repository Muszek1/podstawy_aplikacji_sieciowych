package pl.lodz.p.model.db;

import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDateTime;

public class RentEnt extends AbstractEntity {

    @BsonProperty("begin_time")
    private LocalDateTime beginTime;

    @BsonProperty("end_time")
    private LocalDateTime endTime;

    @BsonProperty("client")
    private UserEnt client;

    @BsonProperty("room")
    private RoomEnt room;

    public RentEnt() {
    }

    public LocalDateTime getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(LocalDateTime beginTime) {
        this.beginTime = beginTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public UserEnt getClient() {
        return client;
    }

    public void setClient(UserEnt client) {
        this.client = client;
    }

    public RoomEnt getRoom() {
        return room;
    }

    public void setRoom(RoomEnt room) {
        this.room = room;
    }

}