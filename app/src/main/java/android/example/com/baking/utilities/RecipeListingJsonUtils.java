/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.utilities;

import android.example.com.baking.data.Recipe;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeListingJsonUtils {
    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<Recipe[]> listRecipes();
}
