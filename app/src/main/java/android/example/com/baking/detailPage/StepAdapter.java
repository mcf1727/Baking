/*
 * Copyright (C) 2020 The Android Open Source Project
 */
package android.example.com.baking.detailPage;

import android.content.Context;
import android.example.com.baking.R;
import android.example.com.baking.data.Step;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder>{

    private Step[] mSteps;
    private String[] mStepTitles;

    private final ListItemClickListener mOnClickListener;

    public interface ListItemClickListener {
        void onListItemClick(Step clickedStepData);
    }

    public StepAdapter(ListItemClickListener clickListener) {
        mOnClickListener = clickListener;
    }

    @Override
    public StepAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        int layoutIdForListItem = R.layout.step_list_item;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new StepAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepAdapterViewHolder stepAdapterViewHolder, int position) {
        String stepTitle = mStepTitles[position];
        stepAdapterViewHolder.mStepTextView.setText(stepTitle);
    }

    @Override
    public int getItemCount() {
        if ( mSteps== null) return 0;
        return mSteps.length;
    }

    public class StepAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final TextView mStepTextView;

        public StepAdapterViewHolder(View itemView) {
            super(itemView);
            mStepTextView = itemView.findViewById(R.id.tv_step);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Step clickedStepData = mSteps[adapterPosition];

            mOnClickListener.onListItemClick(clickedStepData);
        }
    }

    public void setStepData(Step[] steps) {
        mStepTitles = new String[steps.length];
        for(int i=0;i<steps.length;i++){
            mStepTitles[i] = steps[i].getShortDescription();
        }
        mSteps = steps;
        notifyDataSetChanged();
    }
}
