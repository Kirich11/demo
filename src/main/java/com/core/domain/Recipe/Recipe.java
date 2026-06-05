package com.core.domain.Recipe;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name="recipes")
public final class Recipe {
    
    @Id
    @UuidGenerator(style = Style.VERSION_7)
    @Column(name = "id", nullable = false, unique = true)
    @Getter private UUID id;

    @Column(nullable = false, unique = true, length = 200)
    @Getter private String title;

    @Column(name="descr", nullable = false, unique = true, length = 500)
    @Getter private String description;
    
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    @Getter private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    @Getter private Instant updatedAt;

    public Recipe(
        UUID id,
        String title,
        String description,
        Instant createdAt,
        Instant updatedAt
    ) {
       this.id = id;
       this.title = title; 
       this.description = description; 
       this.createdAt = createdAt;
       this.updatedAt = updatedAt; 
    }

    public final static Recipe create(
        String title,
        String description
    ) {
        Recipe recipe = new Recipe(
            null,
            title,
            description,
            Instant.now(),
            Instant.now()
        );

        return recipe;
    }
}
