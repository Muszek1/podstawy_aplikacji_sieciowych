package pl.lodz.p.controller;

import org.apache.http.HttpStatus;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.lodz.p.AbstractTest;
import pl.lodz.p.adapter.RentRepositoryAdapterInterface;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.adapter.UserRepositoryAdapterInterface;
import pl.lodz.p.model.domain.Rent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class RentControllerTest extends AbstractTest {

    @Autowired
    private RentRepositoryAdapterInterface rentRepository;

    @Autowired
    private RoomRepositoryAdapterInterface roomRepository;

    @Autowired
    private UserRepositoryAdapterInterface userRepository;

    @BeforeEach
    public void setUp() {
        rentRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void createRentTest() {
        String suffix = new ObjectId().toHexString();

        String clientId = given()
                .queryParam("login", "client-" + suffix)
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when()
                .post("/users/clients")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("id");

        String roomId = given()
                .queryParam("number", "room-" + suffix)
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when()
                .post("/rooms")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("id");

        LocalDateTime beginTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        String rentId = given()
                .queryParam("clientId", clientId)
                .queryParam("roomId", roomId)
                .queryParam("beginTime", beginTime.toString())
                .when()
                .post("/rents")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("id", notNullValue())
                .extract()
                .path("id");

        Rent foundRent = rentRepository
                .findById(rentId)
                .orElseThrow();

        assertEquals(rentId, foundRent.getId());
        assertEquals(clientId, foundRent.getClient().getId());
        assertEquals(roomId, foundRent.getRoom().getId());
        assertEquals(beginTime, foundRent.getBeginTime().truncatedTo(ChronoUnit.SECONDS));
        assertNull(foundRent.getEndTime());
        assertTrue(foundRent.isActive());
    }

    @Test
    public void rentAlreadyRentedRoomTest() {
        String suffix = new ObjectId().toHexString();

        String clientId = given()
                .queryParam("login", "client-" + suffix)
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when()
                .post("/users/clients")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("id");

        String roomId = given()
                .queryParam("number", "room-" + suffix)
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when()
                .post("/rooms")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("id");

        LocalDateTime beginTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        given()
                .queryParam("clientId", clientId)
                .queryParam("roomId", roomId)
                .queryParam("beginTime", beginTime.toString())
                .when()
                .post("/rents")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        given()
                .queryParam("clientId", clientId)
                .queryParam("roomId", roomId)
                .queryParam("beginTime", beginTime.toString())
                .when()
                .post("/rents")
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);

        var rents = rentRepository.findByRoomAndEndTimeIsNull(
                roomRepository.findById(roomId).orElseThrow()
        );

        assertEquals(1, rents.size());
    }

    @Test
    public void getRentByIdTest() {
        String suffix = new ObjectId().toHexString();
        String clientId = given().queryParam("login", "client-" + suffix).queryParam("firstName", "Jan").queryParam("lastName", "Kowalski").queryParam("active", true).when().post("/users/clients").path("id");
        String roomId = given().queryParam("number", "room-" + suffix).queryParam("bedsAmount", 2).queryParam("price", 100.0).when().post("/rooms").path("id");
        LocalDateTime beginTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        String rentId = given()
                .queryParam("clientId", clientId)
                .queryParam("roomId", roomId)
                .queryParam("beginTime", beginTime.toString())
                .when().post("/rents").path("id");

        given()
                .when()
                .get("/rents/" + rentId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(rentId))
                .body("client.id", equalTo(clientId))
                .body("room.id", equalTo(roomId));
    }

    @Test
    public void endRentTest() {
        String suffix = new ObjectId().toHexString();
        String clientId = given().queryParam("login", "client-" + suffix).queryParam("firstName", "Jan").queryParam("lastName", "Kowalski").queryParam("active", true).when().post("/users/clients").path("id");
        String roomId = given().queryParam("number", "room-" + suffix).queryParam("bedsAmount", 2).queryParam("price", 100.0).when().post("/rooms").path("id");
        LocalDateTime beginTime = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        String rentId = given()
                .queryParam("clientId", clientId)
                .queryParam("roomId", roomId)
                .queryParam("beginTime", beginTime.toString())
                .when().post("/rents").path("id");

        given()
                .when()
                .patch("/rents/" + rentId + "/end")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        Rent endedRent = rentRepository.findById(rentId).orElseThrow();
        assertNotNull(endedRent.getEndTime());
        assertFalse(endedRent.isActive());
    }

    @Test
    public void getClientRentsTest() {
        String suffix = new ObjectId().toHexString();
        String clientId = given().queryParam("login", "client-" + suffix).queryParam("firstName", "Jan").queryParam("lastName", "Kowalski").queryParam("active", true).when().post("/users/clients").path("id");
        String roomId = given().queryParam("number", "room-" + suffix).queryParam("bedsAmount", 2).queryParam("price", 100.0).when().post("/rooms").path("id");

        given()
                .queryParam("clientId", clientId)
                .queryParam("roomId", roomId)
                .queryParam("beginTime", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .when().post("/rents");

        given()
                .queryParam("current", true)
                .when()
                .get("/rents/clients/" + clientId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("size()", equalTo(1))
                .body("[0].client.id", equalTo(clientId));
    }

    @Test
    public void deleteRentTest() {
        String suffix = new ObjectId().toHexString();
        String clientId = given().queryParam("login", "client-" + suffix).queryParam("firstName", "Jan").queryParam("lastName", "Kowalski").queryParam("active", true).when().post("/users/clients").path("id");
        String roomId = given().queryParam("number", "room-" + suffix).queryParam("bedsAmount", 2).queryParam("price", 100.0).when().post("/rooms").path("id");

        String rentId = given()
                .queryParam("clientId", clientId)
                .queryParam("roomId", roomId)
                .queryParam("beginTime", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .when().post("/rents").path("id");

        given()
                .when()
                .delete("/rents/" + rentId)
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        assertTrue(rentRepository.findById(rentId).isEmpty());
    }

    @Test
    public void endAlreadyEndedRentTest() {
        String suffix = new ObjectId().toHexString();
        String clientId = given().queryParam("login", "client-" + suffix).queryParam("firstName", "Jan").queryParam("lastName", "Kowalski").queryParam("active", true).when().post("/users/clients").path("id");
        String roomId = given().queryParam("number", "room-" + suffix).queryParam("bedsAmount", 2).queryParam("price", 100.0).when().post("/rooms").path("id");

        String rentId = given()
                .queryParam("clientId", clientId)
                .queryParam("roomId", roomId)
                .queryParam("beginTime", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString())
                .when().post("/rents").path("id");

        given().patch("/rents/" + rentId + "/end").then().statusCode(HttpStatus.SC_NO_CONTENT);

        given()
                .when()
                .patch("/rents/" + rentId + "/end")
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);
    }

    @Test
    public void getNonExistentRentTest() {
        String fakeId = new ObjectId().toHexString();

        given()
                .when()
                .get("/rents/" + fakeId)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
