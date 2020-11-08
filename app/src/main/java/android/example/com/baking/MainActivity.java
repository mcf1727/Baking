/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;

import android.content.Context;
import android.content.Intent;
import android.example.com.baking.data.Recipe;
import android.example.com.baking.detailPage.DetailActivity;
import android.example.com.baking.utilities.RecipeListingJsonUtils;
import android.example.com.baking.widget.SimpleIdlingResource;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.ListItemClickListener{

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String RECIPE_BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    private RecyclerView mRecyclerView;
    private RecipeAdapter mRecipeAdapter;

    public static SimpleIdlingResource mIdlingResource;

    @Nullable
    @VisibleForTesting
    public static IdlingResource getIdlingResource() {
        if (mIdlingResource == null)
            mIdlingResource = new SimpleIdlingResource();
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getIdlingResource();

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main);
        mRecyclerView.setHasFixedSize(true);

        mRecipeAdapter = new RecipeAdapter(this);
        mRecyclerView.setAdapter(mRecipeAdapter);

        if (isOnline()) {
            //Service Retrofit
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(RECIPE_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RecipeListingJsonUtils serviceJson = retrofit.create(RecipeListingJsonUtils.class);
            Call<Recipe[]> callJson = serviceJson.listRecipes();

            // Make test wait until background work finished
            mIdlingResource.setIdleState(false);

            callJson.enqueue(new Callback<Recipe[]>() {
                @Override
                public void onResponse(Call<Recipe[]> call, Response<Recipe[]> response) {
                    Recipe[] listRecipes = response.body();
                    mRecipeAdapter.setRecipeData(listRecipes);

                    // Test continues its work
                    mIdlingResource.setIdleState(true);
                }

                @Override
                public void onFailure(Call<Recipe[]> call, Throwable t) {
                    Log.d(LOG_TAG, t.getMessage());

                    // test continue its work
                    mIdlingResource.setIdleState(true);
                }
            });
        } else {
            Toast.makeText(this,  getString(R.string.NO_INTERNET_TOAST), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onListItemClick(Recipe recipeToCheck) {
        Intent intentToStartDetailActivity = new Intent(this, DetailActivity.class);
        intentToStartDetailActivity.putExtra(DetailActivity.EXTRA_RECIPE, recipeToCheck);

        startActivity(intentToStartDetailActivity);
    }

    /**
     * Check if the internet connection is available.
     * @return Boolean (true when there is internet connection, others return false)
     */
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
