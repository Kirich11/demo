package com.core.application.Recipe.commands.DeleteRecipe;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.core.domain.Recipe.RecipeRepository;
import com.core.port.messageBus.annotation.CommandHandler;
import com.core.port.messageBus.command.CommandHandlerInterface;

import lombok.extern.slf4j.Slf4j;

@CommandHandler(command = DeleteRecipeCommand.class)
@Component
@Slf4j
public class DeleteRecipeCommandHandler implements CommandHandlerInterface<DeleteRecipeCommand, DeleteRecipeCommandResult> {
    
    private final RecipeRepository repository;

    @Autowired
    public DeleteRecipeCommandHandler(RecipeRepository repository) {
        this.repository = repository;
    }
    
    @Override
    @Transactional
    public DeleteRecipeCommandResult handle(DeleteRecipeCommand command) {
        log.trace("[%s] [%s] [%s]".formatted(Instant.now().toString(), Thread.currentThread().getName(), this.getClass()));
        
        repository.deleteById(command.id.value);

        return new DeleteRecipeCommandResult();
    }
}
