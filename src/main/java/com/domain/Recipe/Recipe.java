package com.domain.Recipe;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="recipes")
public class Recipe {
    
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false, unique = true, length = 200)
    private String title;

    @Column(name="descr", nullable = false, unique = true, length = 500)
    private String description;
    
    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("now()")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

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
            UUID.ofEpochMillis(Instant.now().toEpochMilli()),
            title,
            description,
            Instant.now(),
            Instant.now()
        );

        return recipe;
    }

    public UUID getId() {
        return this.id;
    }
}
