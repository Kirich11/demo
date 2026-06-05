package com.core.application.Recipe.commands.CreateRecipe;

import com.core.port.Recipe.RecipeId;
import com.core.port.messageBus.command.CommandResult;

import lombok.Data;
import lombok.Value;

@Data
@Value 
public class CreateRecipeCommandResult implements CommandResult {
    public final RecipeId id;
    
    public CreateRecipeCommandResult(RecipeId id) {
        this.id = id;
    }
}
