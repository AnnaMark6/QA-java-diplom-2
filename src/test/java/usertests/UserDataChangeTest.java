package usertests;

import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class UserDataChangeTest {

    private String userAccessToken;

    User user = new User(genString() + "@yandex.ru", genString(), "Username");
    public String genString() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    @Before
    public void tearUp() throws Exception {
        userAccessToken = UserSteps.getResponseCreateUser(user,200).accessToken;
    }

    @Test
    @DisplayName("Тест: успешное изменение email авторизованного пользователя, код 200")
    public void successfulChangeEmailAuthorizedUserTest() throws JsonProcessingException {
        User createdUser = new User(user.getEmail(), user.getPassword(), user.getName());
        userAccessToken = UserSteps.getResponseUserLogin(createdUser, 200).accessToken;
        String updatedEmail = "New" + user.getEmail();
        User updatedUser = new User(updatedEmail, user.getPassword(), user.getName());
        UserSteps.getResponseChangeUserData(updatedUser, userAccessToken, 200)
                .body("user.email",equalTo(updatedEmail.toLowerCase()));
        assertThat(userAccessToken, notNullValue());

    }
    @Test
    @DisplayName("Тест: успешное изменение пароля авторизованного пользователя, код 200")
    public void successfulChangePasswordAuthorizedUserTest() throws JsonProcessingException {
        User createdUser = new User(user.getEmail(), user.getPassword(), user.getName());
        UserSteps.getResponseUserLogin(createdUser, 200);
        String updatedPassword = "New" + user.getPassword();
        User updatedUser = new User(user.getEmail(), updatedPassword, user.getName());
        UserSteps.getResponseChangeUserData(updatedUser, userAccessToken, 200);
        userAccessToken = UserSteps.getResponseUserLogin(updatedUser, 200).accessToken;
        assertThat(userAccessToken, notNullValue());
    }

    @Test
    @DisplayName("Тест: успешное изменение имени авторизованного пользователя, код 200")
    public void successfulChangeNameOfTheAuthorizedUserTest() throws JsonProcessingException {
        User createdUser = new User(user.getEmail(), user.getPassword(), user.getName());
        userAccessToken = UserSteps.getResponseUserLogin(createdUser, 200).accessToken;
        String updatedName = "New" + user.getName();
        User updatedUser = new User(user.getEmail(), user.getPassword(), updatedName);
        UserSteps.getResponseChangeUserData(updatedUser, userAccessToken, 200)
                .body("user.name",equalTo(updatedName));
        assertThat(userAccessToken, notNullValue());
    }

    @Test
    @DisplayName("Тест: изменение email авторизованного пользователя, код 401 Unauthorized")
    public void changeEmailUnauthorizedUser() throws JsonProcessingException {
        String updatedEmail = "New" + user.getEmail();
        User updatedUser = new User(updatedEmail, user.getPassword(), user.getName());
        UserSteps.getResponseChangeUserData(updatedUser, "", 401)
                .body("message",equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Тест: изменение пароля авторизованного пользователя, код 401 Unauthorized")
    public void changePasswordUnauthorizedUser() throws JsonProcessingException {
        String updatedPassword = "New" + user.getPassword();
        User updatedUser = new User(user.getEmail(), updatedPassword, user.getName());
        UserSteps.getResponseChangeUserData(updatedUser, "", 401)
                .body("message",equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Тест: изменение имени авторизованного пользователя, код 401 Unauthorized")
    public void changeNameUnauthorizedUser() throws JsonProcessingException {
        String updatedName = "New" + user.getName();
        User updatedUser = new User(user.getEmail(), user.getPassword(), updatedName);
        UserSteps.getResponseChangeUserData(updatedUser, "", 401)
                .body("message",equalTo("You should be authorised"));
    }

    @After
    public void tearDown() throws Exception {
        if (userAccessToken != null) {
            UserSteps.getResponseUserDeleted(userAccessToken, 202);
        }
    }
}