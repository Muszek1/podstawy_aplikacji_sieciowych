package pl.lodz.p.controller;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import pl.lodz.p.AbstractTest;
import pl.lodz.p.adapter.UserRepositoryAdapterInterface;
import pl.lodz.p.model.domain.Client;
import pl.lodz.p.model.domain.User;


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
}