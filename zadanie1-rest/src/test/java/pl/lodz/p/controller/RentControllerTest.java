package pl.lodz.p.controller;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.lodz.p.AbstractTest;
import pl.lodz.p.adapter.RentRepositoryAdapterInterface;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;
import pl.lodz.p.model.domain.Rent;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class RentControllerTest extends AbstractTest {

    @Autowired
    private RentRepositoryAdapterInterface rentRepository;

    @Autowired
    private RoomRepositoryAdapterInterface roomRepository;

    @Test
    public void createRentTest() {
        String suffix = UUID.randomUUID().toString();

        String clientUuid = given()
                .queryParam("login", "client-" + suffix)
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when()
                .post("/users/clients")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("uuid");

        String roomUuid = given()
                .queryParam("number", "room-" + suffix)
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when()
                .post("/rooms")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("uuid");

        LocalDateTime beginTime = LocalDateTime.now().withNano(0);

        String rentUuid = given()
                .queryParam("clientUUID", clientUuid)
                .queryParam("roomUUID", roomUuid)
                .queryParam("beginTime", beginTime.toString())
                .when()
                .post("/rents")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("uuid", notNullValue())
                .extract()
                .path("uuid");

        Rent foundRent = rentRepository
                .findById(UUID.fromString(rentUuid))
                .orElseThrow();

        assertEquals(UUID.fromString(rentUuid), foundRent.getUuid());
        assertEquals(
                UUID.fromString(clientUuid),
                foundRent.getClient().getUuid()
        );
        assertEquals(
                UUID.fromString(roomUuid),
                foundRent.getRoom().getUuid()
        );
        assertEquals(beginTime, foundRent.getBeginTime());
        assertNull(foundRent.getEndTime());
        assertTrue(foundRent.isActive());
    }

    @Test
    public void rentAlreadyRentedRoomTest() {
        String suffix = UUID.randomUUID().toString();

        String clientUuid = given()
                .queryParam("login", "client-" + suffix)
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when()
                .post("/users/clients")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("uuid");

        String roomUuid = given()
                .queryParam("number", "room-" + suffix)
                .queryParam("bedsAmount", 2)
                .queryParam("price", 100.0)
                .when()
                .post("/rooms")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .path("uuid");

        LocalDateTime beginTime = LocalDateTime.now().withNano(0);

        given()
                .queryParam("clientUUID", clientUuid)
                .queryParam("roomUUID", roomUuid)
                .queryParam("beginTime", beginTime.toString())
                .when()
                .post("/rents")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        given()
                .queryParam("clientUUID", clientUuid)
                .queryParam("roomUUID", roomUuid)
                .queryParam("beginTime", beginTime.toString())
                .when()
                .post("/rents")
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);

        var rents = rentRepository.findByRoomAndEndTimeIsNull(
                roomRepository.findById(UUID.fromString(roomUuid))
                        .orElseThrow()
        );

        assertEquals(1, rents.size());
    }



}