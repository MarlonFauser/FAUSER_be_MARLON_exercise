package com.ecore.roles.api;

import com.ecore.roles.utils.RestAssuredHelper;
import com.ecore.roles.web.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.ecore.roles.utils.MockUtils.*;
import static com.ecore.roles.utils.RestAssuredHelper.*;
import static com.ecore.roles.utils.TestData.*;
import static io.restassured.RestAssured.when;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersApiTest {

    private final RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @LocalServerPort
    private int port;

    @Autowired
    public UsersApiTest(
            RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        RestAssuredHelper.setUp(port);
    }

    @Test
    void shouldFailWhenPathDoesNotExist() {
        sendRequest(when()
                .get("/v1/team")
                .then())
                        .validate(404, "Not Found");
    }

    @Test
    void shouldGetAllUsers() {
        List<UserDto> expectedUsers = List.of(UserDto.fromModel(GIANNI_USER()));
        mockGetUsers(mockServer, expectedUsers);

        UserDto[] users = getUsers().extract().as(UserDto[].class);

        assertThat(users.length).isEqualTo(1);
        assertTrue(users[0].getId().equals(expectedUsers.get(0).getId()));
    }

    @Test
    void shouldGetUser() {
        UserDto expectedUser = UserDto.fromModel(GIANNI_USER());
        mockGetUserById(mockServer, GIANNI_USER_UUID, expectedUser);

        getUser(GIANNI_USER_UUID)
                .statusCode(200)
                .body("firstName", equalTo(expectedUser.getFirstName()));
    }

    @Test
    void shouldFailToGetUserWhenNotExist() {
        mockGetUserById(mockServer, UUID_1, null);
        getUser(UUID_1).validate(404, format("User %s not found", UUID_1));
    }
}
