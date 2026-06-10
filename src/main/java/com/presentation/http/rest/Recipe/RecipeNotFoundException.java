package com.presentation.http.rest.Recipe;

import com.core.port.Recipe.RecipeId;

public class RecipeNotFoundException extends RuntimeException {
        public RecipeNotFoundException(RecipeId id) {
            super("Recipe '%s' not found".formatted(id.value));
        }
}
