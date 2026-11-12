package pl.lodz.p.controller;

import org.apache.http.HttpStatus;
import org.bson.types.ObjectId;
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
                .body("id", notNullValue())
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
                .queryParam("login", "client-to-deactivate-" + new ObjectId().toHexString())
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when().post("/users/clients").as(Client.class);

        given()
                .when()
                .patch("/users/" + client.getId() + "/deactivate")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        User updatedUser = userRepository.findById(client.getId()).orElseThrow();
        assertFalse(updatedUser.isActive());
    }

    @Test
    public void getUserTest() {
        Client client = given()
                .queryParam("login", "client-to-get-" + new ObjectId().toHexString())
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when().post("/users/clients").as(Client.class);

        given()
                .when()
                .get("/users/" + client.getId())
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("login", equalTo(client.getLogin()));
    }

    @Test
    public void updateUserTest() {
        Client client = given()
                .queryParam("login", "client-to-update-" + new ObjectId().toHexString())
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when().post("/users/clients").as(Client.class);

        given()
                .header("If-Match", String.valueOf(client.getVersion()))
                .queryParam("firstName", "Adam")
                .queryParam("lastName", "Nowak")
                .when()
                .put("/users/" + client.getId())
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        User updatedUser = userRepository.findById(client.getId()).orElseThrow();
        assertEquals("Adam", updatedUser.getFirstName());
        assertEquals("Nowak", updatedUser.getLastName());
    }

    @Test
    public void addResourceAdministratorTest() {
        String login = "radmin-" + new ObjectId().toHexString();
        given()
                .queryParam("login", login)
                .queryParam("firstName", "Piotr")
                .queryParam("lastName", "Zasobny")
                .queryParam("active", true)
                .when()
                .post("/users/resourceAdministrators")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("login", equalTo(login))
                .body("firstName", equalTo("Piotr"));

        User foundUser = userRepository.findByLogin(login).orElseThrow();
    }

    @Test
    public void addUserAdministratorTest() {
        String login = "uadmin-" + new ObjectId().toHexString();
        given()
                .queryParam("login", login)
                .queryParam("firstName", "Anna")
                .queryParam("lastName", "Kadrowa")
                .queryParam("active", true)
                .when()
                .post("/users/userAdministrators")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("login", equalTo(login))
                .body("lastName", equalTo("Kadrowa"));
    }

    @Test
    public void getAllUsersTest() {
        String suffix = new ObjectId().toHexString();
        given().queryParam("login", "clientA-" + suffix).queryParam("firstName", "A").queryParam("lastName", "A").queryParam("active", true).when().post("/users/clients");
        given().queryParam("login", "clientB-" + suffix).queryParam("firstName", "B").queryParam("lastName", "B").queryParam("active", true).when().post("/users/clients");

        given()
                .when()
                .get("/users")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("size()", org.hamcrest.Matchers.greaterThanOrEqualTo(2));
    }

    @Test
    public void getByLoginTest() {
        String login = "search-client-" + new ObjectId().toHexString();
        given()
                .queryParam("login", login)
                .queryParam("firstName", "Adam")
                .queryParam("lastName", "Nowak")
                .queryParam("active", true)
                .when().post("/users/clients");

        given()
                .queryParam("login", login)
                .when()
                .get("/users/by-login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("login", equalTo(login))
                .body("firstName", equalTo("Adam"));
    }

    @Test
    public void searchUsersTest() {
        String sharedPrefix = "super" + new ObjectId().toHexString();
        given().queryParam("login", sharedPrefix + "man").queryParam("firstName", "Clark").queryParam("lastName", "Kent").queryParam("active", true).when().post("/users/clients");
        given().queryParam("login", sharedPrefix + "woman").queryParam("firstName", "Diana").queryParam("lastName", "Prince").queryParam("active", true).when().post("/users/clients");
        given().queryParam("login", "batman-" + new ObjectId().toHexString()).queryParam("firstName", "Bruce").queryParam("lastName", "Wayne").queryParam("active", true).when().post("/users/clients");

        given()
                .queryParam("login", sharedPrefix)
                .when()
                .get("/users/search")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("size()", equalTo(2));
    }

    @Test
    public void activateUserTest() {
        Client client = given()
                .queryParam("login", "client-to-activate-" + new ObjectId().toHexString())
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", false)
                .when().post("/users/clients").as(Client.class);

        given()
                .when()
                .patch("/users/" + client.getId() + "/activate")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        User updatedUser = userRepository.findById(client.getId()).orElseThrow();
        assertTrue(updatedUser.isActive());
    }

    @Test
    public void updateUserWithWrongVersionTest() {
        Client client = given()
                .queryParam("login", "client-version-" + new ObjectId().toHexString())
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "Kowalski")
                .queryParam("active", true)
                .when().post("/users/clients").as(Client.class);

        given()
                .header("If-Match", "9999")
                .queryParam("firstName", "Adam")
                .queryParam("lastName", "Nowak")
                .when()
                .put("/users/" + client.getId())
                .then()
                .statusCode(HttpStatus.SC_CONFLICT);
    }

    @Test
    public void addClientWithBlankLastNameTest() {
        given()
                .queryParam("login", "blank-last-name-" + new ObjectId().toHexString())
                .queryParam("firstName", "Jan")
                .queryParam("lastName", "   ")
                .queryParam("active", true)
                .when()
                .post("/users/clients")
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    public void getNonExistentUserTest() {
        given()
                .when()
                .get("/users/" + new ObjectId().toHexString())
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}