package com.core.application.Recipe.queries.FindRecipeById;

import com.core.port.Recipe.RecipeData;
import com.core.port.messageBus.query.QueryResult;

public class FindRecipeByIdQueryResult implements QueryResult {
    public final RecipeData data;
    public FindRecipeByIdQueryResult (RecipeData data) {
        this.data = data;
    }
}
