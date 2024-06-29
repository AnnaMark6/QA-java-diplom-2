package ordertests;

import static config.UriConstants.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import config.UriConstants;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import model.Order;
import model.User;
import usertests.UserSteps;

import java.util.ArrayList;

public class OrderSteps {

    private static String jsonString;
    static ObjectMapper mapper = new ObjectMapper();

    @Step("Получение ингредиентов")
    public static ValidatableResponse getIngredients() throws JsonProcessingException {
        return RestAssured.given().log().all()
                .baseUri(UriConstants.BASE_URL)
                .get(INGREDIENTS)
                .then().log().all()
                .statusCode(200);
    }

    @Step("Создание заказа")
    public static ValidatableResponse getCreateOrder(Order order, String userAccessToken,
                                                     int statusCode) throws JsonProcessingException {
        jsonString = mapper.writeValueAsString(order);
        return RestAssured.given().log().all()
                .headers("Authorization", userAccessToken, "Content-Type", "application/json")
                .baseUri(UriConstants.BASE_URL)
                .body(jsonString)
                .when()
                .post(ORDERS)
                .then().log().all()
                .statusCode(statusCode);
    }

    @Step("Создание списка валидных хешей ингредиентов")
    public static ArrayList<String> сreateListOfIngredients() throws JsonProcessingException {
        return new ArrayList<>(OrderSteps.getIngredients()
                .extract()
                .path("data._id"));
    }

    @Step("Создание списка заказов пользователя")
    public static void createListOfOrders(User user, int numberOfOrders) throws JsonProcessingException {
        ArrayList<String> ingredientsHash = сreateListOfIngredients();
        String[] ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};
        Order order = new Order(ingredients);
        UserSteps response = UserSteps.getResponseUserLogin(user, 200);
        for (int i = 0; i < numberOfOrders; i++){
            OrderSteps.getCreateOrder(order, response.accessToken, 200)
                    .assertThat()
                    .body("order.number",notNullValue());
        }
        UserSteps.getResponseLogoutUser(response.refreshToken, 200);
    }

    @Step("Получение списка заказов")
    public static ValidatableResponse getOrderList(String userAccessToken, int statusCode) {
        return RestAssured.given().log().all()
                .header("Authorization", userAccessToken)
                .baseUri(UriConstants.BASE_URL)
                .when()
                .get(ORDERS)
                .then().log().all()
                .statusCode(statusCode);
    }
}
