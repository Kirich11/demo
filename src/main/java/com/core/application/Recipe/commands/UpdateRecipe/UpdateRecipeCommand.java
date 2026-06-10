package com.core.application.Recipe.commands.UpdateRecipe;

import com.core.port.Recipe.RecipeId;
import com.core.port.messageBus.annotation.Command;
import com.core.port.messageBus.command.CommandInterface;

@Command(returnType = UpdateRecipeCommandResult.class)
public final class UpdateRecipeCommand implements CommandInterface {
    public final RecipeId id;
    public final String title;
    public final String description;

    public UpdateRecipeCommand(RecipeId id,String title, String description)
    {
        this.id = id;
        this.title = title;
        this.description = description;
    }
}
