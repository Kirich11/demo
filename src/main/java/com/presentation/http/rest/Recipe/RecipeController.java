package com.presentation.http.rest.Recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.core.application.Recipe.RecipeFacade;
import com.core.port.Recipe.RecipeId;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeFacade recipeService;

    @Autowired
    public RecipeController(RecipeFacade recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeId create(@RequestBody CreateRecipeRequest createRequest) {
        return recipeService.createRecipe(createRequest.title, createRequest.description);
    }
}