package usertests;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.parsing.Parser;
import model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class UserCreateTest {

    private String userAccessToken;
    private boolean userCreateSuccess;
    User user = new User(genString() + "@yandex.ru", genString(), "Username");
    public String genString() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    @Before
    public void setUp() {
        RestAssured.defaultParser = Parser.JSON;
        RestAssured.filters(new RequestLoggingFilter());
    }

    @Test
    @DisplayName("Тест: успешное создание уникального пользователя, код 201")
    public void successfulUserCreateTest() throws JsonProcessingException {
        UserSteps response = UserSteps.getResponseCreateUser(user,200);
        userAccessToken = response.accessToken;
        userCreateSuccess = response.success;
        assertThat(userAccessToken, notNullValue());
        assertTrue(userCreateSuccess);
    }

    @Test
    @DisplayName("Тест: создание пользователя, который уже зарегистрирован, код 403 Forbidden")
    public void createUserWithSameDataTest() throws JsonProcessingException {
        UserSteps initResponse = UserSteps.getResponseCreateUser(user,200);
        userAccessToken = initResponse.accessToken;
        userCreateSuccess = initResponse.success;
        UserSteps response = UserSteps.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals("User already exists", response.message);
    }

    @Test
    @DisplayName("Тест: создание пользователя без email, код 403 Forbidden")
    public void createUserWithoutEmailTest() throws JsonProcessingException {
        User user = new User(null, genString(), "Username");
        UserSteps response = UserSteps.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals("Email, password and name are required fields", response.message);
    }
    @Test
    @DisplayName("Тест: создание пользователя без пароля, код 403 Forbidden")
    public void createUserWithoutPasswordTest() throws JsonProcessingException {
        User user = new User(genString() + "@yandex.ru", null, "Username");
        UserSteps response = UserSteps.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals("Email, password and name are required fields", response.message);
    }

    @Test
    @DisplayName("Тест: создание пользователя без имени, код 403 Forbidden")
    public void createUserWithoutNameTest() throws JsonProcessingException {
        User user = new User(genString() + "@yandex.ru", genString(), null);
        UserSteps response = UserSteps.getResponseCreateUser(user, 403);
        assertFalse(response.success);
        assertEquals("Email, password and name are required fields", response.message);
    }

    @After
    public void tearDown() throws Exception {
        if (userAccessToken != null) {
            UserSteps.getResponseUserDeleted(userAccessToken, 202);
        }
    }
}