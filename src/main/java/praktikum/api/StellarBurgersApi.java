package praktikum.api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import praktikum.model.AuthorizationRequest;
import praktikum.model.Ingredient;
import praktikum.model.OrderRequest;
import praktikum.model.UserRequest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

public class StellarBurgersApi {

    private final Header contentTypeHeader = new Header(CONTENT_TYPE, APPLICATION_JSON.getMimeType());

    public StellarBurgersApi() {
        RestAssured.baseURI = Paths.BASE_URL;
    }

    @Step("Создание пользователя")
    public Response createUser(UserRequest userRequest) {
        return given()
                .header(contentTypeHeader)
                .body(userRequest)
                .when()
                .post(Paths.CREATE_USER_PATH);
    }

    @Step("Авторизация пользователя")
    public Response loginUser(AuthorizationRequest authorizationRequest) {
        return given()
                .header(contentTypeHeader)
                .body(authorizationRequest)
                .when()
                .post(Paths.LOGIN_USER_PATH);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .header(AUTHORIZATION, accessToken)
                .when()
                .delete(Paths.AUTH_USER_PATH);
    }

    public String getAccessToken(AuthorizationRequest authorizationRequest) {
        Response user = loginUser(authorizationRequest);
        return user.getBody().jsonPath().getString("accessToken");
    }

    @Step("Изменение данных пользователя")
    public Response modifyUser(UserRequest userRequest, String accessToken) {
        Header authHeader = new Header(AUTHORIZATION, accessToken);
        return given()
                .headers(new Headers(contentTypeHeader, authHeader))
                .body(userRequest)
                .when()
                .patch(Paths.AUTH_USER_PATH);
    }

    public List<Ingredient> getIngredients() {
        Response ingredients = given()
                .get("/ingredients");
        return ingredients
                .getBody()
                .jsonPath()
                .getList("data", Ingredient.class);
    }

    @Step("Создание заказа")
    public Response createOrder(OrderRequest orderRequest, String accessToken) {
        Header authHeader = new Header(AUTHORIZATION, accessToken);
        return given()
                .headers(new Headers(contentTypeHeader, authHeader))
                .body(orderRequest)
                .when()
                .post(Paths.ORDER_PATH);
    }

    @Step("Получение списка заказов")
    public Response getOrders(String accessToken) {
        Header authHeader = new Header(AUTHORIZATION, accessToken);
        return given()
                .header(authHeader)
                .when()
                .get(Paths.ORDER_PATH);
    }
}
