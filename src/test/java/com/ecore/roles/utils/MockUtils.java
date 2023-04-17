package com.ecore.roles.utils;

import com.ecore.roles.web.dto.TeamDto;
import com.ecore.roles.web.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

public class MockUtils {

    public static void mockGetUserById(MockRestServiceServer mockServer, UUID userId, UserDto user) {
        try {
            mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://test.com/users/" + userId))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(
                            withStatus(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(new ObjectMapper().writeValueAsString(user)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void mockGetUsers(MockRestServiceServer mockServer, List<UserDto> users) {
        try {
            mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://test.com/users"))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(
                            withStatus(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(new ObjectMapper().writeValueAsString(users)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void mockGetTeamById(MockRestServiceServer mockServer, UUID teamId, TeamDto team) {
        try {
            mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://test.com/teams/" + teamId))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(
                            withStatus(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(new ObjectMapper().writeValueAsString(team)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void mockGetTeams(MockRestServiceServer mockServer, List<TeamDto> teams) {
        try {
            mockServer.expect(ExpectedCount.manyTimes(), requestTo("http://test.com/teams"))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(
                            withStatus(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(new ObjectMapper().writeValueAsString(teams)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
