/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.ingredientsPage;

import android.content.Context;
import android.example.com.baking.R;
import android.example.com.baking.data.Ingredient;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientAdapterViewHolder>{

    private static final String colonIngredient = ": ";
    private Ingredient[] mIngredients;

    public IngredientAdapter(){
    }

    @Override
    public IngredientAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.ingredient_list_item;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new IngredientAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientAdapterViewHolder ingredientAdapterViewHolder, int position) {
        Ingredient ingredient = mIngredients[position];
        Double quantity = ingredient.getQuantity();
        String measure = ingredient.getMeasure();
        String ingredientName = ingredient.getIngredient();
        String ingredientToAdd = ingredientName + colonIngredient + quantity + measure;
        ingredientAdapterViewHolder.mIngredientTextView.setText(ingredientToAdd);
    }

    @Override
    public int getItemCount() {
        if ( mIngredients== null) return 0;
        return mIngredients.length;    }

    public static class IngredientAdapterViewHolder extends RecyclerView.ViewHolder {

        public final TextView mIngredientTextView;

        public IngredientAdapterViewHolder(View itemView) {
            super(itemView);
            mIngredientTextView = itemView.findViewById(R.id.tv_ingredient);
        }
    }

    public void setIngredientData(Ingredient[] ingredients) {
        mIngredients = ingredients;
        notifyDataSetChanged();
    }

}
