package praktikum.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum IngredientType {
    @JsonProperty("sauce") SAUCE,
    @JsonProperty("main") FILLING,
    @JsonProperty("bun") BUN
}
