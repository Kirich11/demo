package com.core.application.Recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.core.application.Recipe.commands.CreateRecipe.CreateRecipeCommand;
import com.core.application.Recipe.commands.CreateRecipe.CreateRecipeCommandResult;
import com.core.port.Recipe.RecipeId;
import com.core.port.messageBus.command.CommandDispatcher;

@Service
public class RecipeFacade {
    
    private final CommandDispatcher commandDispatcher;

    @Autowired
    public RecipeFacade(@Qualifier("commandDispatcher") CommandDispatcher commandDispatcher) {
        this.commandDispatcher = commandDispatcher;
    }

    public RecipeId createRecipe(String title, String description) {
        final CreateRecipeCommandResult result = commandDispatcher.dispatchCommand(new CreateRecipeCommand(title, description));
        return result.id;
    }
}
