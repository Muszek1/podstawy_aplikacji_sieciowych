package pl.lodz.p.adapter;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pl.lodz.p.model.db.RoomEnt;
import pl.lodz.p.model.domain.Room;

import java.util.List;

public interface RoomRepositoryAdapterInterface extends MongoRepository<RoomEnt, String> {
//    Room saveRoom(Room room);
    void setRoomArchivedStatus(String number, boolean isArchived);

    Room findRoomByNumber(String number);
    List<Room> findAllRooms();
}
