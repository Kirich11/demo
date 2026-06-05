package com.presentation.http.rest.Recipe;

public class RecipeNotFoundException extends RuntimeException {
        public RecipeNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public RecipeNotFoundException() {}
}
