package com.coding.recimechallenge.service;

import com.coding.recimechallenge.exception.FilterNotFoundException;
import com.coding.recimechallenge.exception.InvalidFilterValueException;
import com.coding.recimechallenge.model.Difficulty;
import com.coding.recimechallenge.model.Recipe;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@Service
public class RecipeService {

    @Value("${dummy.json}")
    private String filePath;


    private ObjectMapper objectMapper;

    @Autowired
    public RecipeService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Recipe> getTrendingRecipes() throws IOException {
        List<Recipe> recipes = getRecipesDummyData();
        return recipes.stream()
                .filter(Recipe::isTrending)
                .sorted(Comparator.comparing(Recipe::getPosition))
                .toList();
    }

    public List<Recipe> getFilteredTrendingRecipes(String difficulty) throws FilterNotFoundException, IOException, InvalidFilterValueException {
        if(difficulty.isBlank() || difficulty.isEmpty()) {
            throw new FilterNotFoundException("Required parameter difficulty missing.");
        }
        //Store the difficulty enum as a list
        List<String> difficultyTypes = Stream.of(Difficulty.values())
                .map(Difficulty::name).toList();
        //split the request in the url and store in a set
        Set<String> difficultyFilters = new HashSet<>(
                Arrays.stream(difficulty.split(","))
                        .map(String::toUpperCase)
                        .filter(difficultyTypes::contains)
                        .toList()
        );
        //throw exception if filter:required is empty or invalid
        if(difficultyFilters.isEmpty()) {
            //throw exception -> bad request
            throw new InvalidFilterValueException("Required parameter difficulty missing.");
        }

        List<Recipe> recipes = getRecipesDummyData();
        return recipes.stream()
                .filter(recipe -> difficultyFilters.contains(recipe.getDifficulty().toString()))
                .filter(Recipe::isTrending)
                .sorted(Comparator.comparing(Recipe::getPosition))
                .toList();
    }


    private List<Recipe> getRecipesDummyData() throws IOException {
        File file = ResourceUtils.getFile(("classpath:" + filePath));
        return Arrays.asList(objectMapper.readValue(file, Recipe[].class));
    }
}
