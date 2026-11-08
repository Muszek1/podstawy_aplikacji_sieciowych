package pl.lodz.p.adapter.mapper;

import pl.lodz.p.model.db.ClientEnt;
import pl.lodz.p.model.db.RentEnt;
import pl.lodz.p.model.db.RoomEnt;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.Rent;
import pl.lodz.p.model.domain.Room;

public class RentMapper {

    public static Rent mapRentFromDbToDomain(RentEnt rentEnt) {
        Client client = (Client) UserMapper.mapUserFromDbToDomain(rentEnt.getClient());
        Room room = RoomMapper.mapRoomFromDbToDomain(rentEnt.getRoom());

        return new Rent(
                rentEnt.getUuid(),
                rentEnt.getBeginTime(),
                rentEnt.getEndTime(),
                client,
                room
        );
    }

    public static RentEnt mapRentFromDomainToDb(Rent rent) {
        RentEnt rentEnt = new RentEnt();
        RoomEnt room = RoomMapper.mapRoomFromDomainToDb(rent.getRoom());
        ClientEnt client =  (ClientEnt) UserMapper.mapUserFromDomainToDb(rent.getClient());


        rentEnt.setUuid(rent.getUuid());
        rentEnt.setBeginTime(rent.getBeginTime());
        rentEnt.setEndTime(rent.getEndTime());
        rentEnt.setClient(client);
        rentEnt.setRoom(room);

        return rentEnt;
    }
}
