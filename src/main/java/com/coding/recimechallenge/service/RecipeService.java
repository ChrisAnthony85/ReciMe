package com.coding.recimechallenge.service;

import com.coding.recimechallenge.model.Recipe;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class RecipeService {

    @Value("${dummy.json}")
    private String filePath;

    @Value("${error.difficulty.required}")
    private String missingDifficultyMessage;

    private ObjectMapper objectMapper;

    @Autowired
    public RecipeService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Recipe> getTrendingRecipes() throws IOException {
        File file = ResourceUtils.getFile(("classpath:" + filePath));
        List<Recipe> recipes = Arrays.asList(objectMapper.readValue(file, Recipe[].class));
        return recipes.stream()
                .filter(Recipe::isTrending)
                .sorted(Comparator.comparing(Recipe::getPosition))
                .toList();
    }

    public List<Recipe> getFilteredTrendingRecipes(String difficulty) {
        Recipe sample = new Recipe();
        sample.setName(difficulty);
        return Collections.singletonList(sample);
    }
}
