package com.core.application.Recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.core.application.Recipe.commands.CreateRecipe.CreateRecipeCommand;
import com.core.application.Recipe.commands.CreateRecipe.CreateRecipeCommandResult;
import com.core.application.Recipe.queries.FindRecipeById.FindRecipeByIdQuery;
import com.core.application.Recipe.queries.FindRecipeById.FindRecipeByIdQueryResult;
import com.core.port.Recipe.RecipeData;
import com.core.port.Recipe.RecipeId;
import com.core.port.messageBus.command.CommandDispatcher;
import com.core.port.messageBus.query.QueryDispatcher;

@Service
public class RecipeFacade {
    
    private final CommandDispatcher commandDispatcher;
    private final QueryDispatcher queryDispatcher;

    @Autowired
    public RecipeFacade(
        @Qualifier("commandDispatcher") CommandDispatcher commandDispatcher,
        @Qualifier("queryDispatcher") QueryDispatcher queryDispatcher
    ) {
        this.commandDispatcher = commandDispatcher;
        this.queryDispatcher = queryDispatcher;
    }

    public RecipeId createRecipe(String title, String description) {
        final CreateRecipeCommandResult result = commandDispatcher.dispatchCommand(new CreateRecipeCommand(title, description));
        return result.id;
    }

    public RecipeData getRecipeById(RecipeId id) {
        final FindRecipeByIdQueryResult result = queryDispatcher.dispatchQuery(new FindRecipeByIdQuery(id));
        return result.data;
    }
}
