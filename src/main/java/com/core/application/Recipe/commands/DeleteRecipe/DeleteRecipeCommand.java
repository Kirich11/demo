package com.core.application.Recipe.commands.DeleteRecipe;

import com.core.port.Recipe.RecipeId;
import com.core.port.messageBus.annotation.Command;
import com.core.port.messageBus.command.CommandInterface;

@Command(returnType = DeleteRecipeCommandResult.class)
public final class DeleteRecipeCommand implements CommandInterface {
    public final RecipeId id;

    public DeleteRecipeCommand(RecipeId id)
    {
        this.id = id;
    }
}
