package com.example.timo.hip;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class FilterRecyclerAdapter extends RecyclerView.Adapter<FilterRecyclerAdapter.ViewHolder> {
    private List<String> categories;
    private List<String> activeCategories;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView mName;
        public CheckBox mCheckBox;
        public ViewHolder(View v) {
            super(v);
            this.mView = v;
            this.mName = (TextView) v.findViewById(R.id.txtName);
            this.mCheckBox = (CheckBox) v.findViewById(R.id.checkBox);
        }
    }

    public FilterRecyclerAdapter(List<String> categories, List<String> activeCategories) {
        this.categories = categories;
        this.activeCategories = activeCategories;
    }

    @Override
    public FilterRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mName.setText(this.categories.get(position));
        if(this.activeCategories.contains(this.categories.get(position))) holder.mCheckBox.setChecked(true);
        else holder.mCheckBox.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return this.categories.size();
    }
}