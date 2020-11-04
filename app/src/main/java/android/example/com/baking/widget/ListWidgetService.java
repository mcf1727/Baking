/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.com.baking.R;
import android.example.com.baking.data.Ingredient;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String colonIngredient = ": ";
    Context mContext;
    String[] mIngredients;

    ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Ingredient[] ingredientsObject;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(mContext.getString(R.string.INGREDIENTS_WIDGET), null);
        Type type = new TypeToken<Ingredient[]>() {}.getType();
        ingredientsObject = gson.fromJson(json, type);

        mIngredients = new String[ingredientsObject.length];
        for(int i=0;i<ingredientsObject.length;i++){

            Ingredient ingredient = ingredientsObject[i];
            Double quantity = ingredient.getQuantity();
            String measure = ingredient.getMeasure();
            String ingredientName = ingredient.getIngredient();
            mIngredients[i] = ingredientName + colonIngredient + quantity + measure;
        }
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mIngredients == null) return 0;
        return mIngredients.length;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_widget_provider);
        views.setTextViewText(R.id.tv_widget_ingredient, mIngredients[position]);

        Bundle extras = new Bundle();
        extras.putStringArray(mContext.getString(R.string.INGREDIENTS_WIDGET), mIngredients);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        views.setOnClickFillInIntent(R.id.tv_widget_ingredient, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
