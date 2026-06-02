package com.infrastructure.Recipe.Persistance;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.domain.Recipe.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    
    @Query("select r from Recipe r where title like '%?1%'")
    List<Recipe> findByTitle(String title);
}

