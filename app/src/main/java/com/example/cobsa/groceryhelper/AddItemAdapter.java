package com.example.cobsa.groceryhelper;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Cobsa on 17.8.2017.
 */

public class AddItemAdapter extends CursorRecyclerViewAdapter<AddItemAdapter.AddItemViewHolder> {

    public class AddItemViewHolder extends RecyclerView.ViewHolder {

        TextView mItemName;

        public AddItemViewHolder(View v) {
            super(v);
        }
    }

    @Override
    public void onBindViewHolder(AddItemViewHolder holder, Cursor cursor) {

    }

    @Override
    public AddItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }
}