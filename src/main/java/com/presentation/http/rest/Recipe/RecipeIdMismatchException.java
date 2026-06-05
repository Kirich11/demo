package com.presentation.http.rest.Recipe;

public class RecipeIdMismatchException extends RuntimeException {
        public RecipeIdMismatchException(String message, Throwable cause) {
            super(message, cause);
        }

        public RecipeIdMismatchException() {}
}
