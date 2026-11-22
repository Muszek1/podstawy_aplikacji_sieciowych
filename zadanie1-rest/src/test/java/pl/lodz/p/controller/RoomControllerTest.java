package pl.lodz.p.controller;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.lodz.p.AbstractTest;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.model.domain.Room;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class RoomControllerTest extends AbstractTest {

    @Inject
    public RoomRepositoryAdapterInterface roomRepository;

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
                .queryParam("number", "room-to-get-" + new ObjectId().toHexString())
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when().post("/rooms").as(Room.class);

        given()
                .when()
                .get("/rooms/" + room.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("number", equalTo(room.getNumber()));
    }

    @Test
    public void updateRoomTest() {
        Room room = given()
                .queryParam("number", "room-to-update-" + new ObjectId().toHexString())
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when().post("/rooms").as(Room.class);

        given()
                .header("If-Match", String.valueOf(room.getVersion()))
                .queryParam("bedsAmount", 4)
                .queryParam("price", 250.0)
                .when()
                .put("/rooms/" + room.getId())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        Room updatedRoom = roomRepository.findById(room.getId()).orElseThrow();
        assertEquals(4, updatedRoom.getBedsAmount());
        assertEquals(250.0, updatedRoom.getPrice());
    }

    @Test
    public void deleteRoomTest() {
        Room room = given()
                .queryParam("number", "room-to-delete-" + new ObjectId().toHexString())
                .queryParam("bedsAmount", 1)
                .queryParam("price", 50.0)
                .when().post("/rooms").as(Room.class);

        given()
                .when()
                .delete("/rooms/" + room.getId())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        assertTrue(roomRepository.findById(room.getId()).isEmpty());
    }

    @Test
    public void getAllRoomsTest() {
        given().queryParam("number", "101").queryParam("bedsAmount", 2).queryParam("price", 100.0).when().post("/rooms");
        given().queryParam("number", "102").queryParam("bedsAmount", 3).queryParam("price", 150.0).when().post("/rooms");

        given()
                .when()
                .get("/rooms")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("size()", equalTo(2));
    }

    @Test
    public void getRoomByNumberTest() {
        String roomNumber = "201-" + new ObjectId().toHexString();
        given()
                .queryParam("number", roomNumber)
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when().post("/rooms");

        given()
                .when()
                .get("/rooms/number/" + roomNumber)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("number", equalTo(roomNumber));
    }

    @Test
    public void addRoomWithBlankNumberTest() {
        given()
                .queryParam("number", "") // Pusty numer
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when()
                .post("/rooms")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void addRoomWithNegativePriceTest() {
        given()
                .queryParam("number", "301")
                .queryParam("bedsAmount", 2)
                .queryParam("price", -50.0) // Ujemna cena
                .when()
                .post("/rooms")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void updateRoomWithWrongVersionTest() {
        Room room = given()
                .queryParam("number", "room-version-test")
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when().post("/rooms").as(Room.class);

        given()
                .header("If-Match", "999")
                .queryParam("bedsAmount", 4)
                .queryParam("price", 250.0)
                .when()
                .put("/rooms/" + room.getId())
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);
    }

    @Test
    public void getNonExistentRoomTest() {
        String fakeId = new ObjectId().toHexString();

        given()
                .when()
                .get("/rooms/" + fakeId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void addRoomWithDuplicateNumberTest() {
        given()
                .queryParam("number", "duplicate-room")
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when()
                .post("/rooms")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        given()
                .queryParam("number", "duplicate-room")
                .queryParam("bedsAmount", 3)
                .queryParam("price", 150.0)
                .when()
                .post("/rooms")
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);
    }
}