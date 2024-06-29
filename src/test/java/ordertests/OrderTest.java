package ordertests;

import model.Order;
import model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import usertests.UserSteps;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderTest {

    private String userAccessToken;
    private ArrayList<String> ingredientsHash;
    private String[] ingredients;

    Order order = new Order(ingredients);
    User user = new User(genString() + "@yandex.ru", genString(), "Username");
    public String genString() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    @Before
    public void setUp() throws Exception {
        userAccessToken = UserSteps.getResponseCreateUser(user,200).accessToken;
        ingredientsHash = OrderSteps.сreateListOfIngredients();
    }

    @Test
    @DisplayName("Тест: создание заказа с авторизацией с ингредиентами, код 200")
    public void successfulCreateOrderWithAuthAndIngredientsTest() throws JsonProcessingException {
        userAccessToken = UserSteps.getResponseUserLogin(user, 200).accessToken;
        ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};
        Order order = new Order(ingredients);
        OrderSteps.getCreateOrder(order, userAccessToken, 200)
                .assertThat()
                .body("order.number",notNullValue());
    }

    @Test
    @DisplayName("Тест: создание заказа с авторизацией без ингредиентов, код 400 Bad Request")
    public void сreateOrderWithAuthAndNoIngredientTest() throws JsonProcessingException {
        userAccessToken = UserSteps.getResponseUserLogin(user, 200).accessToken;
        OrderSteps.getCreateOrder(order, userAccessToken, 400)
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Тест: создание заказа без авторизации с ингредиентами, код 400 Bad Request")
    public void createOrderWithoutAuthAndTwoIngredientTest() throws JsonProcessingException {
        ingredients = new String[]{ingredientsHash.get(0), ingredientsHash.get(ingredientsHash.size() - 1)};
        OrderSteps.getCreateOrder(order, "", 400)
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Тест: создание заказа без авторизации без ингредиентов, код 400 Bad Request")
    public void createOrderWithoutAuthAndNoIngredientTest() throws JsonProcessingException {
        OrderSteps.getCreateOrder(order, "", 400)
                .body("message",equalTo("Ingredient ids must be provided"));
    }

    @Test // тест падает с кодом 400 Bad Request
    @DisplayName("Тест: создания заказа с авторизацией и неверным хешем ингредиентов")
    public void createOrderWithAuthAndIncorrectHashIngredientTest() throws JsonProcessingException {
        userAccessToken = UserSteps.getResponseUserLogin(user, 200).accessToken;
        ingredients = new String[]{"1000"};
        OrderSteps.getCreateOrder(order, userAccessToken, 500)
                .body("message",equalTo("Internal Server Error"));
    }

    @After
    public void tearDown() throws Exception {
        if (userAccessToken != null) {
            UserSteps.getResponseUserDeleted(userAccessToken, 202);
        }
    }
}