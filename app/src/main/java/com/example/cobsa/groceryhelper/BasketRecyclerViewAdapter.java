package com.example.cobsa.groceryhelper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
        private View mView;

        public BasketViewHolder(View v) {
            super(v);
            mBasketName = (TextView) v.findViewById(R.id.basket_name_text_view);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            mProgressStatus = (TextView) v.findViewById(R.id.basket_progress_textView);
            mMenuTextView = (TextView) v. findViewById(R.id.basket_item_menu_text_view);
            mView = v;
        }
    }

    @Override
    public void onBindViewHolder(final BasketViewHolder holder, Cursor cursor) {
        // Set content on Binding View to data
        holder.mBasketName.setText(cursor.
                getString(cursor.getColumnIndex(MyContentProvider.BASKET_NAME)));
        holder.mBasketID = cursor.getLong(cursor.getColumnIndex(MyContentProvider.BASKET_ID));
        holder.mProgressStatus.setText(Long.toString(cursor.getLong(3)) +"/"
                + Long.toString(cursor.getLong(2)));
        holder.mProgressBar.setMax(cursor.getInt(2));
        holder.mProgressBar.setProgress(cursor.getInt(3));

        // Setup menu on click listener

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

        // Setup click listener for item click

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change Fragment to Basket contents
                Context mContext = v.getContext();
                FragmentTransaction fragmentTransaction =
                        ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction();
                Fragment newFragment = new BasketContentsFragment();
                Bundle fragmentArgs = new Bundle();
                fragmentArgs.putLong(BasketContentsFragment.BASKET_ID,holder.mBasketID);
                newFragment.setArguments(fragmentArgs);

                fragmentTransaction.replace(R.id.main_activity_fragment_container, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

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
