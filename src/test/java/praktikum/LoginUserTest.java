package praktikum;


import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.api.StellarBurgersApi;
import praktikum.model.AuthorizationRequest;
import praktikum.model.UserRequest;

@RunWith(Parameterized.class)
public class LoginUserTest {

    private final StellarBurgersApi api;
    private final UserRequest userRequest;
    private String accessToken;

    public LoginUserTest(UserRequest userRequest) {
        this.api = new StellarBurgersApi();
        this.userRequest = userRequest;
    }

    @Parameterized.Parameters(name = "Тестовые данные: {0}")
    public static Object[][] getTestData() {

        return new Object[][]{
                {Utils.createRandomUser()}
        };
    }

    @Before
    public void setUp() throws Exception {
        Response user = api.createUser(userRequest);
        accessToken = user.getBody().jsonPath().getString("accessToken");
    }

    @Test
    public void testUserLogin() {
        AuthorizationRequest authorizationRequest = Utils.createAuthorizationRequest(userRequest);

        api.loginUser(authorizationRequest)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testUserLoginWithWrongPassword() {
        AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                RandomStringUtils.randomAlphabetic(5),
                userRequest.getPassword()
        );

        api.loginUser(authorizationRequest)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("message", Matchers.equalTo("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        api.deleteUser(accessToken);
    }
}
