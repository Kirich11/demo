package com.presentation.http.rest.Recipe;

import lombok.Data;
import lombok.Value;

@Data
@Value
public class CreateRecipeRequest {
    public final String title;
    public final String description;
    public CreateRecipeRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
