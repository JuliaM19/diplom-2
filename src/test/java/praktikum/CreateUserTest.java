package praktikum;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.api.StellarBurgersApi;
import praktikum.model.UserRequest;

import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateUserTest {

    private final StellarBurgersApi api = new StellarBurgersApi();
    private final UserRequest userRequest;
    private String accessToken;
    private Response user;

    public CreateUserTest(UserRequest userRequest) {
        this.userRequest = userRequest;
    }

    @Parameterized.Parameters(name = "Тестовые данные: {0}")
    public static Object[][] getTestData() {
        return new Object[][]{{Utils.createRandomUser()}};
    }

    @Before
    public void setUp() {
        user = api.createUser(userRequest);
        accessToken = user.getBody().jsonPath().getString("accessToken");
    }

    @Test
    public void testFieldsValidation() {
        UserRequest userWithoutPass = new UserRequest();
        userWithoutPass.setName(userRequest.getName());
        userWithoutPass.setEmail(userRequest.getEmail());

        api.createUser(userWithoutPass).then().statusCode(HttpStatus.SC_FORBIDDEN);

        UserRequest userWithoutEmail = new UserRequest();
        userWithoutEmail.setName(userRequest.getName());
        userWithoutEmail.setPassword(userRequest.getPassword());

        api.createUser(userWithoutEmail).then().statusCode(HttpStatus.SC_FORBIDDEN);

        UserRequest userWithoutName = new UserRequest();
        userWithoutName.setPassword(userRequest.getPassword());
        userWithoutName.setEmail(userRequest.getEmail());

        api.createUser(userWithoutName).then().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void testCreateUserWithoutErrors() {
        user.then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("refreshToken", notNullValue())
                .and()
                .body("accessToken", Matchers.containsString("Bearer"));
    }

    @Test
    public void testCreateUserWithAlreadyCreatedOne() {
        api.createUser(userRequest)
                .then()
                .assertThat().statusCode(HttpStatus.SC_FORBIDDEN);
    }

    @Test
    public void deleteUserTest() {
        api.deleteUser(accessToken)
                .then()
                .assertThat().statusCode(HttpStatus.SC_ACCEPTED);
    }

    @After
    public void tearDown() {
        api.deleteUser(accessToken);
    }
}
