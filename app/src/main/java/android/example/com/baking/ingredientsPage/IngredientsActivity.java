/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.ingredientsPage;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.com.baking.R;
import android.example.com.baking.widget.RecipeWidgetProvider;
import android.example.com.baking.data.Ingredient;
import android.example.com.baking.data.Recipe;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class IngredientsActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE = "android.example.com.baking.extra.RECIPE";
    private Ingredient[] mIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(EXTRA_RECIPE)) {
                Recipe mRecipe = intentThatStartedThisActivity.getParcelableExtra(EXTRA_RECIPE);

                if (mRecipe != null) {
                    mIngredients = mRecipe.getIngredients();

                    if (mIngredients != null) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(mIngredients);
                        editor.putString(getString(R.string.INGREDIENTS_WIDGET), json)
                        .putString(getString(R.string.RECIPE_NAME_WIDGET), mRecipe.getName());
                        editor.apply();

                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeWidgetProvider.class));
                        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.lv_widget_ingredients_list);
                        RecipeWidgetProvider.updateRecipeWidgets(this, appWidgetManager,appWidgetIds);
                    }

                    setTitle(mRecipe.getName());
                }
            } else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                Gson gson = new Gson();
                String ingredients = sharedPreferences.getString(getString(R.string.INGREDIENTS_WIDGET), null);
                Type type = new TypeToken<Ingredient[]>() {}.getType();
                mIngredients = gson.fromJson(ingredients, type);

                String recipeName = sharedPreferences.getString(getString(R.string.RECIPE_NAME_WIDGET), getString(R.string.RECIPE_DEFAULT_NAME_WIDGET));
                setTitle(recipeName);
            }
        }

        IngredientsFragment ingredientsFragment = new IngredientsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.ingredients_container, ingredientsFragment)
                .commit();
        ingredientsFragment.setIngredients(mIngredients);
    }
}