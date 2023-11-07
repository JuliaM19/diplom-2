package praktikum;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.api.StellarBurgersApi;
import praktikum.model.Ingredient;
import praktikum.model.OrderRequest;
import praktikum.model.UserRequest;

import java.util.List;

public class GetOrderTest {

    private final StellarBurgersApi api = new StellarBurgersApi();
    private final UserRequest userRequest = Utils.createRandomUser();
    private String accessToken;
    private OrderRequest orderRequest;

    @Before
    public void setUp() {
        Response user = api.createUser(userRequest);
        accessToken = user.getBody().jsonPath().getString("accessToken");
        List<Ingredient> ingredients = api.getIngredients();
        orderRequest = Utils.getRandomOrderRequest(ingredients);
    }

    @Test
    public void testGettingOrdersUnauthorized() {
        api.getOrders(null)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message", Matchers.equalTo("You should be authorised"));
    }

    @Test
    public void testGettingOrders() {
        api.createOrder(orderRequest, accessToken);

        api.getOrders(accessToken)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("success", Matchers.equalTo(true))
                .and()
                .body("orders", Matchers.iterableWithSize(1))
                .and()
                .body("orders[0].ingredients", Matchers.hasItems(orderRequest.getIngredients().toArray()));
    }

    @After
    public void tearDown() {
        api.deleteUser(accessToken);
    }
}
