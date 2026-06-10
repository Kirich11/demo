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
import lombok.Setter;

@Entity
@Table(name="recipes")
@Getter
@Setter
public final class Recipe {
    
    @Id
    @UuidGenerator(style = Style.VERSION_7)
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

    public Recipe(){};

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

    public void update(String title, String description) {
        this.title = title;
        this.description = description;
        this.updatedAt = Instant.now();
    }
}
