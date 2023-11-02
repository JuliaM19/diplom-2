package praktikum;


import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
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

    @Test
    public void testUserLogin() {
        api.createUser(userRequest);
        AuthorizationRequest authorizationRequest = Utils.createAuthorizationRequest(userRequest);

        api.loginUser(authorizationRequest)
                .then()
                .statusCode(HttpStatus.SC_OK);

        String accessToken = api.getAccessToken(authorizationRequest);
        api.deleteUser(accessToken);
    }

    @Test
    public void testUserLoginWithWrongPassword() {
        api.createUser(userRequest);

        AuthorizationRequest authorizationRequest = new AuthorizationRequest(
                RandomStringUtils.randomAlphabetic(5),
                userRequest.getPassword()
        );

        api.loginUser(authorizationRequest)
                .then()
                .statusCode(HttpStatus.SC_UNAUTHORIZED)
                .and()
                .body("message", Matchers.equalTo("email or password are incorrect"));

        String accessToken = api.getAccessToken(Utils.createAuthorizationRequest(userRequest));
        api.deleteUser(accessToken);
    }
}
