package com.presentation.http.Recipe;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.domain.Recipe.Recipe;
import com.infrastructure.Recipe.Persistance.RecipeRepository;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping
    public Iterable<Recipe> findAll() {
        return recipeRepository.findAll();
    }

    @GetMapping("/title/{recipeTitle}")
    public List<Recipe> findByTitle(@PathVariable String recipeTitle) {
        return recipeRepository.findByTitle(recipeTitle);
    }

    @GetMapping("/{id}")
    public Recipe findOne(@PathVariable UUID id) {
        return recipeRepository.findById(id)
          .orElseThrow(RecipeNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Recipe create(@RequestBody Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        recipeRepository.findById(id)
          .orElseThrow(RecipeNotFoundException::new);
        recipeRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Recipe update(@RequestBody Recipe recipe, @PathVariable UUID id) {
        if (recipe.getId() != id) {
          throw new RecipeIdMismatchException();
        }
        recipeRepository.findById(id)
          .orElseThrow(RecipeNotFoundException::new);
        return recipeRepository.save(recipe);
    }
}