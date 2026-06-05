package com.core.application.Recipe.commands.CreateRecipe;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.core.domain.Recipe.Recipe;
import com.core.domain.Recipe.RecipeRepository;
import com.core.port.Recipe.RecipeId;
import com.core.port.messageBus.annotation.CommandHandler;
import com.core.port.messageBus.command.CommandHandlerInterface;

import lombok.extern.slf4j.Slf4j;

@CommandHandler(command = CreateRecipeCommand.class)
@Component
@Slf4j
public class CreateRecipeCommandHandler implements CommandHandlerInterface<CreateRecipeCommand, CreateRecipeCommandResult> {
    
    private final RecipeRepository repository;

    @Autowired
    public CreateRecipeCommandHandler(RecipeRepository repository) {
        this.repository = repository;
    }
    
    @Override
    @Transactional
    public CreateRecipeCommandResult handle(CreateRecipeCommand command) {
        log.trace("[%s] [%s]".formatted(Instant.now().toString(), this.getClass()));
        
        final var recipe = repository.saveAndFlush(Recipe.create(command.title, command.description));

        return new CreateRecipeCommandResult(new RecipeId(recipe.getId()));
    }
}
