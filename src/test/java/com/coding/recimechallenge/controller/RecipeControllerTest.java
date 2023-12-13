package com.coding.recimechallenge.controller;

import com.coding.recimechallenge.exception.FilterNotFoundException;
import com.coding.recimechallenge.exception.InvalidFilterValueException;
import com.coding.recimechallenge.model.Difficulty;
import com.coding.recimechallenge.model.Recipe;
import com.coding.recimechallenge.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecipeService service;

    @Value("${error.difficulty.required}")
    private String missingDifficultyMessage;

    @Value("${error.difficulty.invalid}")
    private String invalidDifficultyMessage;

    @Test
    void whenGetTrendingRecipes_thenSuccess() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setName("Sample Recipe");
        List<Recipe> recipes = Collections.singletonList(recipe);

        BDDMockito.given(service.getTrendingRecipes()).willReturn(recipes);

        mockMvc.perform(get("/recipes/trending").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(recipe.getName())));
    }

    @Test
    void whenGetFilteredRecipes_thenSuccess() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setName("Sample Recipe");
        recipe.setDifficulty(Difficulty.EASY);
        List<Recipe> recipes = Collections.singletonList(recipe);

        BDDMockito.given(service.getFilteredTrendingRecipes("easy")).willReturn(recipes);

        mockMvc.perform(get("/recipes/trending?difficulty=easy").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(recipe.getName())));
    }

    @Test
    void whenGetFilteredRecipes_missing_thenErrorMessage() throws Exception {
        BDDMockito.given(service.getFilteredTrendingRecipes(""))
                .willThrow(new FilterNotFoundException("sample"));

        mockMvc.perform(get("/recipes/trending?difficulty=").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(missingDifficultyMessage)));
    }

    @Test
    void whenGetFilteredRecipes_invalid_thenErrorMessage() throws Exception {
        BDDMockito.given(service.getFilteredTrendingRecipes("a,b"))
                .willThrow(new InvalidFilterValueException("sample"));

        mockMvc.perform(get("/recipes/trending?difficulty=a,b").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(invalidDifficultyMessage)));
    }
}