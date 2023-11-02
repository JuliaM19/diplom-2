package praktikum;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
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

    @Test
    @Description("Создание заказа без авторизации")
    public void testCreateOrderUnauthorized() {
        Response user = api.createUser(userRequest);
        String accessToken = user.getBody().jsonPath().getString("accessToken");
        List<Ingredient> ingredients = api.getIngredients();

        OrderRequest orderRequest = new OrderRequest();
        Ingredient randomBun = Utils.getRandomBun(ingredients);
        Ingredient randomSauce = Utils.getRandomSauce(ingredients);
        Ingredient randomFilling = Utils.getRandomFilling(ingredients);
        orderRequest.setIngredients(List.of(
                randomBun.getId(),
                randomSauce.getId(),
                randomFilling.getId()
        ));

        api.createOrder(orderRequest, null)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", Matchers.equalTo(true));

        api.deleteUser(accessToken);
    }

    @Test
    public void testCreateOrderAuthorized() {
        Response user = api.createUser(userRequest);
        String accessToken = user.getBody().jsonPath().getString("accessToken");
        List<Ingredient> ingredients = api.getIngredients();

        OrderRequest orderRequest = new OrderRequest();
        Ingredient randomBun = Utils.getRandomBun(ingredients);
        Ingredient randomSauce = Utils.getRandomSauce(ingredients);
        Ingredient randomFilling = Utils.getRandomFilling(ingredients);
        orderRequest.setIngredients(List.of(
                randomBun.getId(),
                randomSauce.getId(),
                randomFilling.getId()
        ));

        api.createOrder(orderRequest, accessToken)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("success", Matchers.equalTo(true));

        api.deleteUser(accessToken);
    }

    @Test
    public void testCreateOrderWithoutIngredients() {
        Response user = api.createUser(userRequest);
        String accessToken = user.getBody().jsonPath().getString("accessToken");

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setIngredients(Collections.emptyList());
        api.createOrder(orderRequest, accessToken)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", Matchers.equalTo("Ingredient ids must be provided"));


        api.deleteUser(accessToken);
    }

    @Test
    public void testCreateOrderWithBadIngredient() {
        Response user = api.createUser(userRequest);
        String accessToken = user.getBody().jsonPath().getString("accessToken");

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setIngredients(List.of(RandomStringUtils.randomAlphabetic(10)));
        api.createOrder(orderRequest, accessToken)
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        //Кажется, должно быть другое поведение,
        //но по заданию нужно прорить этот кейс, а также все тесты должны проходить

        api.deleteUser(accessToken);
    }
}
