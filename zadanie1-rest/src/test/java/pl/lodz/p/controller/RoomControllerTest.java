package pl.lodz.p.controller;

import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import pl.lodz.p.AbstractTest;
import pl.lodz.p.adapter.RoomRepositoryAdapterInterface;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
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
        String requestBody = """
                {
                    "number": "10",
                    "bedsAmount": 2,
                    "price": 100.0
                }
                """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/rooms")
        .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("number", equalTo("10"))
                .body("bedsAmount", equalTo(2))
                .body("price", equalTo(100.0f));
    }
}