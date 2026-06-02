package com.presentation.http.Recipe;

public class RecipeNotFoundException extends RuntimeException {
        public RecipeNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public RecipeNotFoundException() {}
}
