package com.coding.recimechallenge.controller;

import com.coding.recimechallenge.exception.FilterNotFoundException;
import com.coding.recimechallenge.exception.InvalidFilterValueException;
import com.coding.recimechallenge.model.Recipe;
import com.coding.recimechallenge.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value="/recipes")
public class RecipeController {

    private RecipeService service;

    @Autowired
    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @GetMapping(value = "/trending")
    List<Recipe> getTrendingRecipes() throws IOException {
        return service.getTrendingRecipes();
    }

    @GetMapping(value = "/trending", params = "difficulty")
    List<Recipe> getFilteredTrendingRecipes(@RequestParam String difficulty) throws IOException, InvalidFilterValueException {
        return service.getFilteredTrendingRecipes(difficulty);
    }

}
