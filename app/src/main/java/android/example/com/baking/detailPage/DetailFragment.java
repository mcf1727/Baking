/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.detailPage;

import android.content.Context;
import android.example.com.baking.R;
import android.example.com.baking.data.Step;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DetailFragment extends Fragment implements StepAdapter.ListItemClickListener, View.OnClickListener{

    private Step[] mSteps;

    public void setSteps(Step[] steps) {
        mSteps = steps;
    }

    public DetailFragment() {
    }

    OnDetailClickListener onDetailClickListener;

    public interface OnDetailClickListener {
        void onStepSelected(Step clickedStepData);
        void onIngredientsSelected();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            onDetailClickListener = (OnDetailClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + getString(R.string.must_implement_onstepclicklistener));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        RecyclerView mStepRecyclerView = rootView.findViewById(R.id.recyclerview_steps);
        TextView mIngredientsTextView = rootView.findViewById(R.id.tv_detail_ingredients);
        mIngredientsTextView.setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mStepRecyclerView.setLayoutManager(layoutManager);
        mStepRecyclerView.setHasFixedSize(true);
        StepAdapter mStepAdapter = new StepAdapter(this);
        mStepRecyclerView.setAdapter(mStepAdapter);
        mStepAdapter.setStepData(mSteps);

        return rootView;
    }

    @Override
    public void onListItemClick(Step clickedStepData) {
        onDetailClickListener.onStepSelected(clickedStepData);
    }

    @Override
    public void onClick(View view) {
        onDetailClickListener.onIngredientsSelected();
    }

}
