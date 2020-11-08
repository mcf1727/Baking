/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.detailPage;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.com.baking.R;
import android.example.com.baking.data.Ingredient;
import android.example.com.baking.data.Recipe;
import android.example.com.baking.data.Step;
import android.example.com.baking.ingredientsPage.IngredientsActivity;
import android.example.com.baking.ingredientsPage.IngredientsFragment;
import android.example.com.baking.stepPage.StepActivity;
import android.example.com.baking.stepPage.StepFragment;
import android.example.com.baking.widget.RecipeWidgetProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class DetailActivity extends AppCompatActivity implements DetailFragment.OnDetailClickListener {

    public static final String EXTRA_RECIPE = "android.example.com.baking.extra.RECIPE";

    FrameLayout ingredientsFrameLayout;
    FrameLayout stepFrameLayout;

    Recipe mRecipe;
    Step[] mSteps;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(EXTRA_RECIPE)) {
                mRecipe = intentThatStartedThisActivity.getParcelableExtra(EXTRA_RECIPE);

                if (mRecipe != null) {
                    mSteps = mRecipe.getSteps();

                    DetailFragment detailFragment = new DetailFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.detail_container, detailFragment)
                            .commit();
                    detailFragment.setSteps(mSteps);

                    setTitle(mRecipe.getName());
                }
            }
        }

        // For tablets step fragment is displayed in detailActivity
        if (findViewById(R.id.step_container) != null) {
            mTwoPane = true;

            ingredientsFrameLayout = findViewById(R.id.ingredients_container);
            stepFrameLayout = findViewById(R.id.step_container);

            if (savedInstanceState == null) {

                StepFragment stepFragment = new StepFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.step_container, stepFragment)
                        .commit();

                Step mStep = mSteps[0];
                String stepInstruction;
                String videoURL;

                if (mStep != null) {
                    stepInstruction = mStep.getDescription();
                    videoURL = mStep.getVideoURL();
                    stepFragment.setVideoURL(videoURL);

                    if (stepInstruction != null) {
                        stepFragment.setStepInstruction(stepInstruction);
                    }
                }

                stepFragment.setTwoPane(mTwoPane);
            }

        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onStepSelected(Step clickedStepData) {

        if (mTwoPane) {

            ingredientsFrameLayout.setVisibility(View.GONE);
            stepFrameLayout.setVisibility(View.VISIBLE);

            StepFragment stepFragment = new StepFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.step_container, stepFragment)
                    .commit();

            String stepInstruction;
            String videoURL;


            if (clickedStepData != null) {
                stepInstruction = clickedStepData.getDescription();
                videoURL = clickedStepData.getVideoURL();
                stepFragment.setVideoURL(videoURL);

                if (stepInstruction != null) {
                    stepFragment.setStepInstruction(stepInstruction);
                }
            }

            stepFragment.setTwoPane(mTwoPane);

        } else {
            Intent intentToStartStepActivity = new Intent(this, StepActivity.class);
            intentToStartStepActivity.putExtra(StepActivity.EXTRA_STEP, clickedStepData)
                    .putExtra(StepActivity.EXTRA_RECIPE_NAME, mRecipe.getName());

            startActivity(intentToStartStepActivity);
        }
    }

    @Override
    public void onIngredientsSelected() {
        if (mTwoPane) {

            ingredientsFrameLayout.setVisibility(View.VISIBLE);
            stepFrameLayout.setVisibility(View.GONE);

            Ingredient[] mIngredients;

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
            } else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                Gson gson = new Gson();
                String ingredients = sharedPreferences.getString(getString(R.string.INGREDIENTS_WIDGET), null);
                Type type = new TypeToken<Ingredient[]>() {}.getType();
                mIngredients = gson.fromJson(ingredients, type);

                String recipeName = sharedPreferences.getString(getString(R.string.RECIPE_NAME_WIDGET), getString(R.string.RECIPE_DEFAULT_NAME_WIDGET));
                setTitle(recipeName);
            }

            IngredientsFragment ingredientsFragment = new IngredientsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.ingredients_container, ingredientsFragment)
                    .commit();
            ingredientsFragment.setIngredients(mIngredients);

        } else {
            Intent intentToStartIngredientsActivity = new Intent(this, IngredientsActivity.class);
            intentToStartIngredientsActivity.putExtra(IngredientsActivity.EXTRA_RECIPE, mRecipe);

            startActivity(intentToStartIngredientsActivity);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
