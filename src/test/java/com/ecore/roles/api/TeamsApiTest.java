package com.ecore.roles.api;

import com.ecore.roles.utils.RestAssuredHelper;
import com.ecore.roles.web.dto.TeamDto;
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
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TeamsApiTest {

    private final RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @LocalServerPort
    private int port;

    @Autowired
    public TeamsApiTest(
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
    void shouldGetAllTeams() {
        List<TeamDto> expectedTeams = List.of(TeamDto.fromModel(ORDINARY_CORAL_LYNX_TEAM()));
        mockGetTeams(mockServer, expectedTeams);

        TeamDto[] teams = getTeams().extract().as(TeamDto[].class);

        assertThat(teams.length).isEqualTo(1);
        assertTrue(teams[0].getId().equals(expectedTeams.get(0).getId()));
    }

    @Test
    void shouldGetTeam() {
        TeamDto expectedTeam = TeamDto.fromModel(ORDINARY_CORAL_LYNX_TEAM());
        mockGetTeamById(mockServer, ORDINARY_CORAL_LYNX_TEAM_UUID, expectedTeam);

        getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID)
                .statusCode(200)
                .body("name", equalTo(expectedTeam.getName()));
    }

    @Test
    void shouldFailToGetTeamWhenNotExist() {
        mockGetTeamById(mockServer, UUID_1, null);
        getTeam(UUID_1).validate(404, format("Team %s not found", UUID_1));
    }
}
