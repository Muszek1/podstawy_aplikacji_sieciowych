package pl.lodz.p.controller;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import pl.lodz.p.AbstractTest;
import pl.lodz.p.adapter.UserRepositoryAdapterInterface;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.Room;
import pl.lodz.p.model.domain.User;


import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest extends AbstractTest {

    @Autowired
    private UserRepositoryAdapterInterface userRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void addClientTest() {
        Client newClient = given()
                .queryParam("login", "jkowalski")
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when()
                .post("/users/clients")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("uuid", notNullValue())
                .body("login", equalTo("jkowalski"))
                .body("firstName", equalTo("Jan"))
                .body("lastName", equalTo("Kowalski"))
                .body("active", equalTo(true))
                .extract()
                .as(Client.class);

        User foundUser = userRepository
                .findByLogin("jkowalski")
                .orElseThrow();

        assertInstanceOf(Client.class, foundUser);
        assertEquals(newClient, foundUser);
        assertEquals(newClient.getLogin(), foundUser.getLogin());
        assertEquals(newClient.getFirstName(), foundUser.getFirstName());
        assertEquals(newClient.getLastName(), foundUser.getLastName());
        assertEquals(newClient.isActive(), foundUser.isActive());
    }

    @Test
    public void addClientWithBlankFirstNameTest() {
        given()
                .queryParam("login", "invalid-client")
                .queryParam("firstName", " ")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when()
                .post("/users/clients")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);

        assertTrue(userRepository.findByLogin("invalid-client").isEmpty());
    }

    @Test
    public void addClientWithDuplicateLoginTest() {
        given()
                .queryParam("login", "duplicate-login")
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when()
                .post("/users/clients")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

        given()
                .queryParam("login", "duplicate-login")
                .queryParam("firstName", "Anna")
                .queryParam("lastName", "Nowak")
                .queryParam("active", true)
                .when()
                .post("/users/clients")
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);

        User foundUser = userRepository
                .findByLogin("duplicate-login")
                .orElseThrow();

        assertEquals("Jan", foundUser.getFirstName());
        assertEquals("Kowalski", foundUser.getLastName());
    }

    @Test
    public void deactivateUserTest() {
        Client client = given()
                .queryParam("login", "client-to-deactivate-" + UUID.randomUUID())
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when().post("/users/clients").as(Client.class);

        given()
                .when()
                .patch("/users/" + client.getUuid() + "/deactivate")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        User updatedUser = userRepository.findById(client.getUuid()).orElseThrow();
        assertFalse(updatedUser.isActive());
    }

    @Test
    public void getUserTest() {
        Client client = given()
                .queryParam("login", "client-to-deactivate-" + UUID.randomUUID())
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when().post("/users/clients").as(Client.class);

        given()
                .when()
                .get("/users/" + client.getUuid())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("login", equalTo(client.getLogin()));
    }

    @Test
    public void updateUserTest() {
        Client client = given()
                .queryParam("login", "client-to-update-" + UUID.randomUUID())
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when().post("/users/clients").as(Client.class);

        given()
                .header("If-Match", client.getVersion())
                .queryParam("firstName", "Adam")
                .queryParam("lastName", "Nowak")
                .when()
                .put("/users/" + client.getUuid())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        User updatedUser = userRepository.findById(client.getUuid()).orElseThrow();
        assertEquals("Adam", updatedUser.getFirstName());
        assertEquals("Nowak", updatedUser.getLastName());
    }
}