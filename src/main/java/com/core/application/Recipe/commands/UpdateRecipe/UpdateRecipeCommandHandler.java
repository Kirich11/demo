package com.core.application.Recipe.commands.UpdateRecipe;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.core.domain.Recipe.RecipeRepository;
import com.core.port.Recipe.RecipeId;
import com.core.port.messageBus.annotation.CommandHandler;
import com.core.port.messageBus.command.CommandHandlerInterface;
import com.core.application.Recipe.exceptions.RecipeNotFoundException;

import lombok.extern.slf4j.Slf4j;

@CommandHandler(command = UpdateRecipeCommand.class)
@Component
@Slf4j
public class UpdateRecipeCommandHandler implements CommandHandlerInterface<UpdateRecipeCommand, UpdateRecipeCommandResult> {
    
    private final RecipeRepository repository;

    @Autowired
    public UpdateRecipeCommandHandler(RecipeRepository repository) {
        this.repository = repository;
    }
    
    @Override
    @Transactional
    public UpdateRecipeCommandResult handle(UpdateRecipeCommand command) {
        log.trace("[%s] [%s] [%s]".formatted(Instant.now().toString(), Thread.currentThread().getName(), this.getClass()));
        
        var queryResult = repository.findById(command.id.value);
        if (queryResult.isEmpty()) {
            throw new RecipeNotFoundException(command.id);
        }
        var recipe = queryResult.get();
        
        recipe.update(command.title, command.description);

        recipe = repository.saveAndFlush(recipe);

        return new UpdateRecipeCommandResult(new RecipeId(recipe.getId()));
    }
}
