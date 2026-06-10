package com.core.application.Recipe.commands.UpdateRecipe;

import com.core.port.Recipe.RecipeId;
import com.core.port.messageBus.command.CommandResult;

public class UpdateRecipeCommandResult implements CommandResult {
    public final RecipeId id;
    
    public UpdateRecipeCommandResult(RecipeId id) {
        this.id = id;
    }
}
