package ordertests;

import model.User;
import org.apache.commons.lang3.RandomStringUtils;
import steps.OrderSteps;
import steps.UserSteps;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class OrderListTest {
    private String userAccessToken;
    private int numberOfOrders;

    User user = new User(genString() + "@yandex.ru", genString(), "Username");
    public String genString() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    @Before
    public void setUp() throws Exception {
        userAccessToken = UserSteps.getResponseCreateUser(user,200).accessToken;
        numberOfOrders = 3;
        OrderSteps.createListOfOrders(user, numberOfOrders);
    }
    @Test
    @DisplayName("Тест: получение списка заказов авторизованного пользователя, код 200")
    public void getOrdersListFromAuthorizedUserTest() throws JsonProcessingException {
        userAccessToken = UserSteps.getResponseUserLogin(user, 200).accessToken;
        ArrayList<Integer> orderNumber =
                new ArrayList<>(OrderSteps.getOrderList(userAccessToken, 200)
                .extract()
                .path("orders.number"));
        assertEquals(numberOfOrders, orderNumber.size());
    }

    @Test
    @DisplayName("Тест: получение списка заказов неавторизованного пользователя, код 401 Unauthorized")
    public void getOrdersListFromUnauthorizedUser() throws JsonProcessingException {
        OrderSteps.getOrderList("", 401)
                .body("message",equalTo("You should be authorised"));
    }

    @After
    public void tearDown() throws Exception {
        if (userAccessToken != null) {
            UserSteps.getResponseUserDeleted(userAccessToken, 202);
        }
    }
}
