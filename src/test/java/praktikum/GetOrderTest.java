package praktikum;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import praktikum.api.StellarBurgersApi;
import praktikum.model.Ingredient;
import praktikum.model.OrderRequest;
import praktikum.model.UserRequest;

import java.util.List;

public class GetOrderTest {

    private final StellarBurgersApi api = new StellarBurgersApi();
    private final UserRequest userRequest = Utils.createRandomUser();


    @Test
    public void testGettingOrdersUnauthorized() {
        Response user = api.createUser(userRequest);
        String accessToken = user.getBody().jsonPath().getString("accessToken");

        api.getOrders(null)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", Matchers.equalTo("You should be authorised"));

        api.deleteUser(accessToken);
    }

    @Test
    public void testGettingOrders() {
        Response user = api.createUser(userRequest);
        String accessToken = user.getBody().jsonPath().getString("accessToken");

        List<Ingredient> ingredients = api.getIngredients();
        OrderRequest randomOrderRequest = Utils.getRandomOrderRequest(ingredients);
        api.createOrder(randomOrderRequest, accessToken);

        api.getOrders(accessToken)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", Matchers.equalTo(true))
                .and()
                .body("orders", Matchers.iterableWithSize(1))
                .and()
                .body("orders[0].ingredients", Matchers.hasItems(randomOrderRequest.getIngredients().toArray()));

        api.deleteUser(accessToken);
    }
}
