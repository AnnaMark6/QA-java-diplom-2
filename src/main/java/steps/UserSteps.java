package steps;

import static config.UriConstants.*;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import config.UriConstants;
import model.User;

public class UserSteps {

    private static String jsonString;
    public static String message;
    public static boolean success;
    public static String accessToken;
    public static String refreshToken;

    public UserSteps(boolean success, String message, String accessToken, String refreshToken) {
        this.success = success;
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    static ObjectMapper mapper = new ObjectMapper();

    @Step("Создание учетной записи пользователя")
    public static UserSteps getResponseCreateUser(User user, int statusCode) throws JsonProcessingException {
        jsonString = mapper.writeValueAsString(user);
        Response response = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .baseUri(UriConstants.BASE_URL)
                .body(jsonString)
                .when()
                .post(REGISTER)
                .then().log().all()
                .statusCode(statusCode)
                .extract()
                .response();
        success = response.path("success");
        message = response.path("message");
        accessToken = response.path("accessToken");
        refreshToken = response.path("refreshToken");
        return new UserSteps(success, message, accessToken, refreshToken);
    }

    @Step("Логин пользователя")
    public static UserSteps getResponseUserLogin (User user, int statusCode) throws JsonProcessingException {
        jsonString = mapper.writeValueAsString(user);
        Response response = RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .baseUri(UriConstants.BASE_URL)
                .body(jsonString)
                .when()
                .post(LOGIN)
                .then().log().all()
                .statusCode(statusCode).extract()
                .response();
        success = response.path("success");
        message = response.path("message");
        accessToken = response.path("accessToken");
        refreshToken = response.path("refreshToken");
        return new UserSteps(success, message, accessToken, refreshToken);
    }

    @Step("Выход из учетной записи пользователя")
    public static ValidatableResponse getResponseLogoutUser(String userRefreshToken, int statusCode) {
        jsonString = "{\"token\": \"" + userRefreshToken + "\"}";
        return RestAssured.given().log().all()
                .header("Content-Type", "application/json")
                .baseUri(UriConstants.BASE_URL)
                .body(jsonString)
                .when()
                .post(LOGOUT)
                .then().log().all()
                .statusCode(statusCode);
    }

    @Step("Удаление пользователя")
    public static ValidatableResponse getResponseUserDeleted(String userAccessToken, int statusCode) {
        return RestAssured.given().log().all()
                .header("Authorization", userAccessToken)
                .baseUri(UriConstants.BASE_URL)
                .when()
                .delete(UPDATE)
                .then().log().all()
                .statusCode(statusCode);
    }

    @Step("Изменение данных пользователя")
    public static ValidatableResponse getResponseChangeUserData(User user,
                                                                String userAccessToken,
                                                                int statusCode) throws JsonProcessingException {
        jsonString = mapper.writeValueAsString(user);
        return RestAssured.given().log().all()
                .headers("Authorization", userAccessToken, "Content-Type", "application/json")
                .baseUri(UriConstants.BASE_URL)
                .body(jsonString)
                .when()
                .patch(UPDATE)
                .then().log().all()
                .statusCode(statusCode);
    }
}
