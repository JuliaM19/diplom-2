package praktikum;

import praktikum.model.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

public final class Utils {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    public static UserRequest createRandomUser() {
        int stringLength = 5;
        return new UserRequest(
                randomAlphabetic(stringLength),
                randomAlphabetic(stringLength) + "@" + randomAlphabetic(stringLength) + ".com",
                randomAlphabetic(stringLength)
        );
    }

    public static AuthorizationRequest createAuthorizationRequest(UserRequest userRequest) {
        return new AuthorizationRequest(userRequest.getEmail(), userRequest.getPassword());
    }

    public static Ingredient getRandomBun(List<Ingredient> ingredients) {
        List<Ingredient> buns = ingredients.stream()
                .filter(ingredient -> ingredient.getIngredientType() == IngredientType.BUN)
                .collect(Collectors.toList());

        return buns.get(random.nextInt(buns.size()));
    }

    public static Ingredient getRandomFilling(List<Ingredient> ingredients) {
        List<Ingredient> fillings = ingredients.stream()
                .filter(ingredient -> ingredient.getIngredientType() == IngredientType.FILLING)
                .collect(Collectors.toList());

        return fillings.get(random.nextInt(fillings.size()));
    }

    public static Ingredient getRandomSauce(List<Ingredient> ingredients) {
        List<Ingredient> sauces = ingredients.stream()
                .filter(ingredient -> ingredient.getIngredientType() == IngredientType.SAUCE)
                .collect(Collectors.toList());

        return sauces.get(random.nextInt(sauces.size()));
    }

    public static OrderRequest getRandomOrderRequest(List<Ingredient> ingredients) {
        OrderRequest orderRequest = new OrderRequest();

        List<String> randomIngredients = List.of(
                getRandomBun(ingredients).getId(),
                getRandomSauce(ingredients).getId(),
                getRandomFilling(ingredients).getId()
        );
        orderRequest.setIngredients(randomIngredients);

        return orderRequest;

    }

}
