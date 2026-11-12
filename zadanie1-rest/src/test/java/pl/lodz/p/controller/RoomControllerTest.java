package pl.lodz.p.controller;


import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import pl.lodz.p.AbstractTest;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.model.domain.Room;


import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.jupiter.api.Assertions.*;

class RoomControllerTest extends AbstractTest {
    @Autowired
    private RoomRepositoryAdapterInterface roomRepository;

    @BeforeEach
    public void setUp() {
        roomRepository.deleteAll();
    }

    @Test
    public void addRoomTest() {
        Room newRoom = given()
                .queryParam("number", "10")
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when()
            .post("/rooms")
        .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("number", equalTo("10"))
                .body("bedsAmount", equalTo(2))
                .body("price", equalTo(100.0f))
                .extract()
                .as(Room.class);

        Room foundRoom = roomRepository.findByNumber("10").orElseThrow();

        assertEquals(newRoom, foundRoom);
    }

    @Test
    public void addRoomWithZeroBedsTest() {
        given()
                .queryParam("number", "invalid-room")
                .queryParam("bedsAmount", 0)
                .queryParam("price", 100.0)
                .when()
                .post("/rooms")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        assertTrue(roomRepository.findByNumber("invalid-room").isEmpty());
    }

    @Test
    public void getRoomTest() {
        Room room = given()
                .queryParam("number", "room-to-get-" + UUID.randomUUID())
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when().post("/rooms").as(Room.class);

        given()
                .when()
                .get("/rooms/" + room.getUuid())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("number", equalTo(room.getNumber()));
    }

    @Test
    public void updateRoomTest() {
        Room room = given()
                .queryParam("number", "room-to-update-" + UUID.randomUUID())
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when().post("/rooms").as(Room.class);

        given()
                .header("If-Match", room.getVersion())
                .queryParam("bedsAmount", 4)
                .queryParam("price", 250.0)
                .when()
                .put("/room/" + room.getUuid())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        Room updatedRoom = roomRepository.findById(room.getUuid()).orElseThrow();
        assertEquals(4, updatedRoom.getBedsAmount());
        assertEquals(250.0, updatedRoom.getPrice());
    }

    @Test
    public void deleteRoomTest() {
        Room room = given()
                .queryParam("number", "room-to-delete-" + UUID.randomUUID())
                .queryParam("bedsAmount", 1)
                .queryParam("price", 50.0)
                .when().post("/rooms").as(Room.class);

        given()
                .when()
                .delete("/room/" + room.getUuid())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        assertTrue(roomRepository.findById(room.getUuid()).isEmpty());
    }
}