package com.domain.port.Recipe;

import java.time.Instant;
import java.util.UUID;

public class RecipeData {
    public final RecipeId id;
    public final String title;
    public final String description;
    public final Instant createdAt;
    public final Instant updatedAt;

    public RecipeData(
        UUID id,
        String title,
        String description,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = new RecipeId(id);
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
