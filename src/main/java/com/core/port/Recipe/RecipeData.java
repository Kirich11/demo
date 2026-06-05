package com.core.port.Recipe;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;
import lombok.NonNull;
import lombok.Value;

@Value @Data public class RecipeData {
    public final RecipeId id;
    public final String title;
    public final String description;
    public final Instant createdAt;
    public final Instant updatedAt;

    public RecipeData(
        @NonNull UUID id,
        @NonNull String title,
        String description,
        @NonNull Instant createdAt,
        @NonNull Instant updatedAt
    ) {
        this.id = new RecipeId(id);
        this.title = title;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
