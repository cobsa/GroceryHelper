package com.example.cobsa.groceryhelper;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Cobsa on 16.8.2017.
 */

public class BasketRecyclerViewAdapter extends CursorRecyclerViewAdapter<BasketRecyclerViewAdapter.BasketViewHolder> {


    public class BasketViewHolder extends RecyclerView.ViewHolder {

        private TextView mBasketName;
        private ProgressBar mProgressBar;
        private TextView mProgressStatus;
        private TextView mMenuTextView;
        private Long mBasketID;

        public BasketViewHolder(View v) {
            super(v);
            mBasketName = (TextView) v.findViewById(R.id.basket_name_text_view);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            mProgressStatus = (TextView) v.findViewById(R.id.basket_progress_textView);
            mMenuTextView = (TextView) v. findViewById(R.id.basket_item_menu_text_view);
        }
    }

    @Override
    public void onBindViewHolder(final BasketViewHolder holder, Cursor cursor) {
        // Set content on Binding View to data
        holder.mBasketName.setText(cursor.
                getString(cursor.getColumnIndex(MyContentProvider.BASKET_NAME)));
        holder.mBasketID = cursor.getLong(cursor.getColumnIndex(MyContentProvider.BASKET_ID));
        holder.mProgressBar.setProgress(1); // TODO
        holder.mProgressStatus.setText("1/10");

        // Setup menu for each item

        holder.mMenuTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(),holder.mMenuTextView);
                popupMenu.inflate(R.menu.basket_item_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.basket_item_menu_delete:
                                // Delete current basket
                                v.getContext().getContentResolver().
                                        delete(Uri.withAppendedPath(MyContentProvider.BASKETS_URI,
                                                Long.toString(holder.mBasketID)),null,null);
                                break;
                            case R.id.basket_item_menu_modify:
                                // TODO add modifying basket name
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public BasketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*
         * Called when viewHolder is created for first time.
         * Get correct view layout for ViewHolder when ViewHolder is created
         */

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_basket_recycler_viewer,parent,false);
        BasketViewHolder basketViewHolder = new BasketViewHolder(itemView);
        return basketViewHolder;
    }
}
