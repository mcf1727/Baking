/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Ingredient implements Parcelable {
    private double quantity;
    private String measure;
    private String ingredient;

    public Ingredient() {}

    public double getQuantity() { return quantity; }
    public String getMeasure() { return measure; }
    public String getIngredient() { return ingredient; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(quantity);
        parcel.writeString(measure);
        parcel.writeString(ingredient);
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel parcel) {
            Ingredient ingredient = new Ingredient();
            ingredient.quantity = parcel.readDouble();
            ingredient.measure = parcel.readString();
            ingredient.ingredient = parcel.readString();

            return ingredient;
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

}
