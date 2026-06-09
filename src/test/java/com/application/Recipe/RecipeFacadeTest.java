package com.application.Recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.core.application.Recipe.RecipeFacade;

@SpringBootTest
public class RecipeFacadeTest {
    
    @Test
    void itCreatesAndReturnsRecipe(@Autowired RecipeFacade recipeApp) {
        String title = "some title";
        String description = "some description";
        final var commandResult = recipeApp.createRecipe(title, description);
        assertNotNull(commandResult);

        final var queryResult = recipeApp.getRecipeById(commandResult);
        assertNotNull(commandResult);
        assertEquals(title, queryResult.title);
        assertEquals(description, queryResult.description);
    }
}