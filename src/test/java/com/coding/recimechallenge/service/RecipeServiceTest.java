package com.coding.recimechallenge.service;

import com.coding.recimechallenge.exception.FilterNotFoundException;
import com.coding.recimechallenge.exception.InvalidFilterValueException;
import com.coding.recimechallenge.model.Difficulty;
import com.coding.recimechallenge.model.Recipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
class RecipeServiceTest {

    @Autowired
    private RecipeService service;

    @Value("${image.url}")
    private String imageUrl;

    private boolean isSorted(List<Recipe> list) {
        boolean isSorted = true;
        for(int i = 1; i<list.size(); i++) {
            if(list.get(i-1).getPosition() > list.get(i).getPosition()){
                return false;
            }
        }
        return isSorted;
    }

    @Test
    void whenGetTrendingRecipes_thenReturnAllTrending() throws IOException {
        List<Recipe> recipes = service.getTrendingRecipes();
        Assertions.assertEquals(10, recipes.size());
        Assertions.assertEquals(1, recipes.get(0).getPosition());
    }

    @Test
    void whenGetTrendingRecipes_thenReturnSorted() throws IOException {
        List<Recipe> recipes = service.getTrendingRecipes();
        Assertions.assertTrue(isSorted(recipes));
    }

    @Test
    void whenGetTrendingRecipes_thenImageUrlIsExpected() throws IOException {
        List<Recipe> recipes = service.getTrendingRecipes();
        Assertions.assertTrue(recipes.stream().allMatch(r -> imageUrl.equals(r.getImageUrl())));
    }


    @Test
    void whenGetFilteredRecipes_easy_thenReturnEasy() throws IOException, InvalidFilterValueException {
        List<Recipe> recipes = service.getFilteredTrendingRecipes("Easy");
        Assertions.assertEquals(4, recipes.size());
        Assertions.assertTrue(recipes.stream().allMatch(r -> r.getDifficulty() == Difficulty.EASY));

        recipes = service.getFilteredTrendingRecipes("easY");
        Assertions.assertEquals(4, recipes.size());
        Assertions.assertTrue(recipes.stream().allMatch(r -> r.getDifficulty() == Difficulty.EASY));
    }

    @Test
    void whenGetFilteredRecipes_medium_thenReturnMedium() throws IOException, InvalidFilterValueException {
        List<Recipe> recipes = service.getFilteredTrendingRecipes("medium");
        Assertions.assertEquals(3, recipes.size());
        Assertions.assertTrue(recipes.stream().allMatch(r -> r.getDifficulty() == Difficulty.MEDIUM));

        recipes = service.getFilteredTrendingRecipes(",MediuM,");
        Assertions.assertEquals(3, recipes.size());
        Assertions.assertTrue(recipes.stream().allMatch(r -> r.getDifficulty() == Difficulty.MEDIUM));
    }

    @Test
    void whenGetFilteredRecipes_hard_thenReturnHard() throws IOException, InvalidFilterValueException {
        List<Recipe> recipes = service.getFilteredTrendingRecipes("Hard,");
        Assertions.assertEquals(3, recipes.size());
        Assertions.assertTrue(recipes.stream().allMatch(r -> r.getDifficulty() == Difficulty.HARD));

        recipes = service.getFilteredTrendingRecipes(",harD");
        Assertions.assertEquals(3, recipes.size());
        Assertions.assertTrue(recipes.stream().allMatch(r -> r.getDifficulty() == Difficulty.HARD));
    }

    @Test
    void whenGetFilteredRecipes_thenReturnSorted() throws IOException, InvalidFilterValueException {
        List<Recipe> recipes = service.getFilteredTrendingRecipes("Hard,easy,mediuM");
        Assertions.assertEquals(10, recipes.size());
        Assertions.assertTrue(isSorted(recipes));
    }

    @Test
    void whenGetFilteredRecipes_mixed_thenReturnSorted() throws IOException, InvalidFilterValueException {
        List<Recipe> recipes = service.getFilteredTrendingRecipes("EASY,mediuM,");
        Assertions.assertEquals(7, recipes.size());
        Assertions.assertTrue(isSorted(recipes));
        Assertions.assertTrue(recipes.stream().allMatch(r -> r.getDifficulty() != Difficulty.HARD));
    }

    @Test
    void whenGetFilteredMissing_thenException() {
        FilterNotFoundException thrown = assertThrows(
                FilterNotFoundException.class, () -> {
                    service.getFilteredTrendingRecipes("");
                }
        );
        Assertions.assertEquals("Required parameter <difficulty> missing.", thrown.getMessage());
    }

    @Test
    void whenGetFilteredInvalid_thenException() {
        InvalidFilterValueException thrown = assertThrows(
                InvalidFilterValueException.class, () -> {
                    service.getFilteredTrendingRecipes("a,xy,z");
                }
        );
        Assertions.assertEquals("Invalid input for difficulty parameter.", thrown.getMessage());
    }
}