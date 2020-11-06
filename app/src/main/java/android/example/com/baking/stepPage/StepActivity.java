/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.stepPage;

import android.content.Intent;
import android.example.com.baking.R;
import android.example.com.baking.data.Step;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class StepActivity extends AppCompatActivity {

    public static final String EXTRA_STEP = "android.example.com.baking.extra.STEP";
    public static final String EXTRA_RECIPE_NAME = "android.example.com.baking.extra.recipe_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        StepFragment stepFragment = new StepFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.step_container, stepFragment)
                .commit();

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(EXTRA_STEP)) {
                Step mStep = intentThatStartedThisActivity.getParcelableExtra(EXTRA_STEP);

                if (mStep != null) {
                    String stepInstruction = mStep.getDescription();
                    String videoURL = mStep.getVideoURL();
                    stepFragment.setVideoURL(videoURL);

                    if (stepInstruction != null) {
                        stepFragment.setStepInstruction(stepInstruction);
                    }
                }
            }

            if (intentThatStartedThisActivity.hasExtra(EXTRA_RECIPE_NAME)) {
                String mRecipeName = intentThatStartedThisActivity.getStringExtra(EXTRA_RECIPE_NAME);

                if (mRecipeName != null) {
                    setTitle(mRecipeName);
                }
            }
        }
    }
}
