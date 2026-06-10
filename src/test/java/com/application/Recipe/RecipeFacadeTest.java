package com.application.Recipe;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.core.application.Recipe.RecipeFacade;
import com.core.application.Recipe.exceptions.RecipeNotFoundException;
import com.core.port.Recipe.RecipeId;

@SpringBootTest
public class RecipeFacadeTest {
    
    @Test
    void happyflowCrudRecipeTest(@Autowired RecipeFacade recipeApp) {
        String title = "some title";
        String description = "some description";
        final var commandResult = recipeApp.createRecipe(title, description);
        assertNotNull(commandResult);

        final var queryResult = recipeApp.getRecipeById(commandResult);
        assertNotNull(queryResult);
        assertEquals(title, queryResult.title);
        assertEquals(description, queryResult.description);

        title += " 1";
        description += " 1";
        
        final var updateResult = recipeApp.updateRecipe(commandResult, title, description);
        
        assertNotNull(updateResult);

        final var queryAfterUpdateResult = recipeApp.getRecipeById(updateResult);
        assertNotNull(queryAfterUpdateResult);
        assertEquals(title, queryAfterUpdateResult.title);
        assertEquals(description, queryAfterUpdateResult.description);
        assertNotEquals(queryResult.updatedAt, queryAfterUpdateResult.updatedAt);

        recipeApp.deleteRecipe(updateResult);

        final var queryAfterDeleteResult = recipeApp.getRecipeById(updateResult);
        assertNull(queryAfterDeleteResult);
    }

    @Test
    void recipeNotFoundTest(@Autowired RecipeFacade recipeFacade) {
        RecipeId id = new RecipeId(UUID.ofEpochMillis(Instant.now().toEpochMilli()));
        assertThrows(RecipeNotFoundException.class, () -> recipeFacade.updateRecipe(id, "title", "description"));
        assertDoesNotThrow(() -> recipeFacade.getRecipeById(id));
        assertDoesNotThrow(() -> recipeFacade.deleteRecipe(id));
    }
}