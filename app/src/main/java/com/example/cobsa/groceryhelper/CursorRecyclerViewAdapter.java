package com.example.cobsa.groceryhelper;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;


/*
* Base implementation of RecyclerViewAdapter that supports cursors.
* TODO: Not implemented all function in RecyclerView.Adapter
*/

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private Cursor mCursor;
    private boolean dataValid;

    public CursorRecyclerViewAdapter() {
        dataValid = false;
        mCursor = null;
    }

    public abstract void onBindViewHolder(VH holder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH holder, int position) {

        if(!dataValid) {
            throw new IllegalStateException("This should only be called when cursor is valid");
        }
        if(!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("Cannot move cursor to " + position);
        }
        onBindViewHolder(holder, mCursor);
    }

    @Override
    public int getItemCount() {
        if (dataValid) {
            return mCursor.getCount();
        }
        return -1;
    }

    public void swapCursor(Cursor newCursor) {
        // Swap cursor if it's different and close unused cursors.

        if(newCursor == mCursor) {
            // Just return if cursor is same as before
            return;
        }

        mCursor = newCursor;

        // Set dataValid tag to false if mCursor is null, otherwise to true
        if (mCursor == null) {
            dataValid = false;
        } else {
            dataValid = true;
        }
        notifyDataSetChanged();
    }
}