package com.core.application.Recipe.queries.FindRecipeById;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.core.domain.Recipe.Recipe;
import com.core.domain.Recipe.RecipeRepository;
import com.core.port.Recipe.RecipeData;
import com.core.port.messageBus.annotation.QueryHandler;
import com.core.port.messageBus.query.QueryHandlerInterface;

import lombok.extern.slf4j.Slf4j;

@QueryHandler(query = FindRecipeByIdQuery.class)
@Component
@Slf4j
public class FindRecipeByIdQueryHandler implements QueryHandlerInterface<FindRecipeByIdQuery, FindRecipeByIdQueryResult> {
    private final RecipeRepository repository;

    @Autowired
    public FindRecipeByIdQueryHandler(RecipeRepository repository) {
        this.repository = repository;
    }

    public FindRecipeByIdQueryResult handle(FindRecipeByIdQuery query) {
        Optional<Recipe> queryResult = this.repository.findById(query.id.value);

        if (queryResult.isEmpty()) {
            return null;
        }

        Recipe recipe = queryResult.get();

        return new FindRecipeByIdQueryResult(
            new RecipeData(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getDescription(),
                recipe.getCreatedAt(),
                recipe.getUpdatedAt()
            )
        );
    }
}
