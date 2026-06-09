package com.core.application.Recipe.queries.FindRecipeById;

import com.core.port.Recipe.RecipeId;
import com.core.port.messageBus.annotation.Query;
import com.core.port.messageBus.query.QueryInterface;

@Query(returnType = FindRecipeByIdQueryResult.class)
public class FindRecipeByIdQuery implements QueryInterface {
    public final RecipeId id;
    public FindRecipeByIdQuery(RecipeId id) {
        this.id = id;
    }
}
