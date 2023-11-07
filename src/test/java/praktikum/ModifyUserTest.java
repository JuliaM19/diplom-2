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

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@RunWith(Parameterized.class)
public class ModifyUserTest {

    private final StellarBurgersApi api;
    private final UserRequest userRequest;
    private String accessToken;

    public ModifyUserTest(UserRequest userRequest) {
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
    public void setUp() {
        Response user = api.createUser(userRequest);
        accessToken = user.getBody().jsonPath().getString("accessToken");
    }

    @Test
    public void testModifyUserUnauthorized() {
        UserRequest modifiedUser = new UserRequest(
                randomAlphabetic(5),
                userRequest.getEmail(),
                userRequest.getPassword()
        );
        api.modifyUser(modifiedUser, null)
                .then()
                .body("message", Matchers.equalTo("You should be authorised"))
                .and()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    public void testChangingEmailForExistingOne() {
        UserRequest randomUser = Utils.createRandomUser();
        Response newUser = api.createUser(randomUser);
        String newUserAccessToken = newUser.getBody().jsonPath().getString("accessToken");

        UserRequest userWithExistingEmail = new UserRequest(
                userRequest.getName(),
                randomUser.getEmail(),
                userRequest.getPassword()
        );

        api.modifyUser(userWithExistingEmail, accessToken)
                .then()
                .statusCode(HttpStatus.SC_FORBIDDEN)
                .and()
                .body("message", Matchers.equalTo("User with such email already exists"));

        api.deleteUser(newUserAccessToken);
    }

    @Test
    public void testChangingName() {
        String newName = randomAlphabetic(5);
        UserRequest userWithNewName = new UserRequest(
                newName,
                userRequest.getEmail(),
                userRequest.getPassword()
        );
        api.modifyUser(userWithNewName, accessToken)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("user.name", Matchers.equalTo(newName));
    }

    @Test
    public void testChangingEmail() {
        String newEmail = randomAlphabetic(5) + "@" + randomAlphabetic(5) + ".com";

        UserRequest userWithNewName = new UserRequest(
                userRequest.getName(),
                newEmail,
                userRequest.getPassword()
        );

        api.modifyUser(userWithNewName, accessToken)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("user.email", Matchers.equalToIgnoringCase(newEmail));
    }

    @After
    public void tearDown() {
        api.deleteUser(accessToken);
    }
}
