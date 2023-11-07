package praktikum;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.api.StellarBurgersApi;
import praktikum.model.Ingredient;
import praktikum.model.OrderRequest;
import praktikum.model.UserRequest;

import java.util.Collections;
import java.util.List;

public class CreateOrderTest {

    private final StellarBurgersApi api = new StellarBurgersApi();
    private final UserRequest userRequest = Utils.createRandomUser();
    private OrderRequest orderRequest;
    private String accessToken;


    @Before
    public void setUp() {
        Response user = api.createUser(userRequest);
        accessToken = user.getBody().jsonPath().getString("accessToken");
        List<Ingredient> ingredients = api.getIngredients();
        orderRequest = Utils.getRandomOrderRequest(ingredients);
    }

    @Test
    @Description("Создание заказа без авторизации")
    public void testCreateOrderUnauthorized() {
        api.createOrder(orderRequest, null)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", Matchers.equalTo(true));

    }

    @Test
    public void testCreateOrderAuthorized() {
        api.createOrder(orderRequest, accessToken)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", Matchers.equalTo(true));
    }

    @Test
    public void testCreateOrderWithoutIngredients() {
        OrderRequest orderRequest = new OrderRequest(Collections.emptyList());
        api.createOrder(orderRequest, accessToken)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", Matchers.equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void testCreateOrderWithBadIngredient() {
        OrderRequest orderRequest = new OrderRequest(
                List.of(RandomStringUtils.randomAlphabetic(10))
        );
        api.createOrder(orderRequest, accessToken)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        //Кажется, должно быть другое поведение,
        //но по заданию нужно проверить этот кейс, а также все тесты должны проходить
    }

    @After
    public void tearDown() {
        api.deleteUser(accessToken);
    }
}
