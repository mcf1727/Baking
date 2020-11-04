/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.ingredientsPage;

import android.example.com.baking.R;
import android.example.com.baking.data.Ingredient;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IngredientsFragment extends Fragment {

    private Ingredient[] mIngredients;

    public void setIngredients(Ingredient[] ingredients) {
        mIngredients = ingredients;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_ingredients, container, false);

        RecyclerView mIngredientsRecyclerView = rootView.findViewById(R.id.recyclerview_ingredients);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mIngredientsRecyclerView.setLayoutManager(layoutManager);
        mIngredientsRecyclerView.setHasFixedSize(true);
        IngredientAdapter mIngredientAdapter = new IngredientAdapter();
        mIngredientsRecyclerView.setAdapter(mIngredientAdapter);

        mIngredientAdapter.setIngredientData(mIngredients);

        return rootView;
    }

}
