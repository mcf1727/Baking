/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.example.com.baking.MainActivity;
import android.example.com.baking.R;
import android.example.com.baking.ingredientsPage.IngredientsActivity;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews remoteViews;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String mRecipeName = sharedPreferences.getString(context.getString(R.string.RECIPE_NAME_WIDGET), context.getString(R.string.RECIPE_DEFAULT_NAME_WIDGET));
        if (mRecipeName.equals(context.getString(R.string.RECIPE_DEFAULT_NAME_WIDGET))) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_recipe_ingredients);
            remoteViews.setOnClickPendingIntent(R.id.ll_recipe_ingredients_widget, pendingIntent);
        } else {
            remoteViews = getRecipeListRemoteView(context);
        }

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateRecipeWidgets(context, appWidgetManager, appWidgetIds);
    }

    public static void updateRecipeWidgets(Context context,AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static RemoteViews getRecipeListRemoteView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_recipe_ingredients);
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.lv_widget_ingredients_list, intent);

        Intent appIntent = new Intent(context, IngredientsActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_widget_ingredients_list, appPendingIntent);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String mRecipeName = sharedPreferences.getString(context.getString(R.string.RECIPE_NAME_WIDGET), context.getString(R.string.RECIPE_DEFAULT_NAME_WIDGET));
        views.setTextViewText(R.id.tv_widget_name, mRecipeName);
        views.setOnClickPendingIntent(R.id.tv_widget_name, appPendingIntent);

        views.setEmptyView(R.id.lv_widget_ingredients_list, R.id.empty_view);
        return views;
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}