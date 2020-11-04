/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Recipe implements Parcelable {

    private String name;
    private Ingredient[] ingredients;
    private Step[] steps;
    private String image;

    public Recipe() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Ingredient[] getIngredients() { return ingredients; }
    public void setIngredients(Ingredient[] ingredients) { this.ingredients = ingredients; }

    public Step[] getSteps() { return steps; }
    public void setSteps(Step[] steps) { this.steps = steps; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(image);
        parcel.writeTypedArray(ingredients, i);
        parcel.writeTypedArray(steps, i);
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel parcel) {
            Recipe recipe = new Recipe();
            recipe.name = parcel.readString();
            recipe.image = parcel.readString();
            recipe.ingredients = parcel.createTypedArray(Ingredient.CREATOR);
            recipe.steps = parcel.createTypedArray(Step.CREATOR);

            return recipe;
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
