/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking;

import android.content.Context;
import android.example.com.baking.data.Recipe;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeAdapterViewHolder>{

    private String[] mRecipeNames;
    private Recipe[] mRecipes;

    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(Recipe recipeToCheck);
    }

    public RecipeAdapter(ListItemClickListener clickListener) {
        mOnClickListener = clickListener;
    }

    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.recipe_list_item;
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        RecipeAdapterViewHolder recipeAdapterViewHolder = new RecipeAdapterViewHolder(view);

        return recipeAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder recipeAdapterViewHolder, int position) {
        String recipeName = mRecipeNames[position];
        recipeAdapterViewHolder.mRecipeTextView.setText(recipeName);
    }

    @Override
    public int getItemCount() {
        if (mRecipes == null) return 0;
        return mRecipes.length;
    }

    public void setRecipeData(Recipe[] recipes) {
        mRecipeNames = new String[recipes.length];
        for(int i=0;i<recipes.length;i++){
            mRecipeNames[i] = recipes[i].getName();
        }
        mRecipes = recipes;
        notifyDataSetChanged();
    }

    class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mRecipeTextView;

        public RecipeAdapterViewHolder(View itemView) {
            super(itemView);
            mRecipeTextView = itemView.findViewById(R.id.tv_recipe);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Recipe recipeToCheck = mRecipes[adapterPosition];
            mOnClickListener.onListItemClick(recipeToCheck);
        }
    }
}
