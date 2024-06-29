package usertests;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserLoginTest {

    private boolean userLoginSuccess;
    private String userAccessToken;
    User user = new User(genString() + "@yandex.ru", genString(), "Username");
    public String genString() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    @Before
    public void setUp() throws Exception {
        UserSteps.getResponseCreateUser(user, 200);
        userAccessToken = UserSteps.accessToken;
    }

    @Test
    @DisplayName("Тест: логин под существующим пользователем, код 200")
    public void successfulUserLoginTest() throws JsonProcessingException {
        User createdUser = new User(user.getEmail(), user.getPassword(), user.getName());
        UserSteps response = UserSteps.getResponseUserLogin(createdUser, 200);
        userAccessToken = UserSteps.accessToken;
        userLoginSuccess = UserSteps.success;
        assertTrue(userLoginSuccess);
    }

    @Test
    @DisplayName("Тест: логин с неверным email, код 401 Unauthorized")
    public void loginUserWithIncorrectEmailTest() throws JsonProcessingException {
        User createdUser = new User("incorrect@yandex.ru", user.getPassword(), user.getName());
        UserSteps.getResponseUserLogin(createdUser, 401);
        userLoginSuccess = UserSteps.success;
        assertFalse(userLoginSuccess);
    }

    @Test
    @DisplayName("Тест: логин с неверным паролем, код 401 Unauthorized")
    public void loginUserWithIncorrectPasswordTest() throws JsonProcessingException {
        User createdUser = new User(user.getEmail(), "incorrect", user.getName());
        UserSteps.getResponseUserLogin(createdUser, 401);
        userLoginSuccess = UserSteps.success;
        assertFalse(userLoginSuccess);
    }

    @After
    public void tearDown() throws Exception {
        if (userAccessToken != null) {
            UserSteps.getResponseUserDeleted(userAccessToken, 202);
        }
    }
}
