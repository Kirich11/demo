package com.core.application.Recipe.commands.CreateRecipe;

import com.core.port.messageBus.annotation.Command;
import com.core.port.messageBus.command.CommandInterface;

import lombok.Data;

@Command(returnType = CreateRecipeCommandResult.class)
@Data
public final class CreateRecipeCommand implements CommandInterface {
    public final String title;
    public final String description;

    public CreateRecipeCommand(String title, String description)
    {
        this.title = title;
        this.description = description;
    }
}
