package com.application.Recipe;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.core.application.Recipe.RecipeFacade;

@SpringBootTest
public class RecipeFacadeTest {
    
    @Test
    void itCreatesARecipe(@Autowired RecipeFacade recipeApp) {
        final var result = recipeApp.createRecipe("some title", "some description");
        assertNotNull(result);
    }
}