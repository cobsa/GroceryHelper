package com.example.cobsa.groceryhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Cobsa on 17.8.2017.
 */

public class AddItemAdapter extends CursorRecyclerViewAdapter<AddItemAdapter.AddItemViewHolder> {

    public class AddItemViewHolder extends RecyclerView.ViewHolder {

        private TextView mItemName;
        private ImageView mRemoveItem;
        private TextView mItemCount;
        private long mItemID;
        private View mView;

        public AddItemViewHolder(View v) {
            super(v);
            mItemName = (TextView) v.findViewById(R.id.row_add_item_item_name);
            mRemoveItem = (ImageView) v.findViewById(R.id.row_add_item_delete);
            mItemCount = (TextView) v.findViewById(R.id.row_add_item_in_basket_count);
            mView = v;
        }
    }

    private Long mBasketID;

    public AddItemAdapter(long basketID) {
        mBasketID = basketID;
    }

    @Override
    public void onBindViewHolder(final AddItemViewHolder holder, Cursor cursor) {
        int visible = View.INVISIBLE;
        holder.mItemName.setText(cursor.getString(
                cursor.getColumnIndex(MyContentProvider.INGREDIENTS_NAME)));
        holder.mItemID = cursor.getLong(cursor.getColumnIndex(MyContentProvider.INGREDIENT_ID));
        if(mBasketID.compareTo(cursor.getLong(2))==0) {
            visible = View.VISIBLE;
            holder.mItemCount.setText(Long.toString(cursor.getLong(3)));
        }
        holder.mRemoveItem.setVisibility(visible);
        holder.mItemCount.setVisibility(visible);
        // Set click listener to add ingredient to basket
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add item to basket
                ContentValues values = new ContentValues();
                values.put(MyContentProvider.INGREDIENT_ID, holder.mItemID);
                holder.mView.getContext().getContentResolver().
                        insert(Uri.withAppendedPath(MyContentProvider.BASKETS_URI,
                                Long.toString(mBasketID)+"/ingredient"),values);
            }
        });

        // Set click listener to remove ingredient from basket
        holder.mRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mView.getContext().getContentResolver()
                        .delete(Uri.withAppendedPath(MyContentProvider.BASKETS_URI,
                                Long.toString(mBasketID)+"/ingredient/"+holder.mItemID),null,null);
            }
        });

    }

    @Override
    public AddItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View ItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_add_item,parent,false);
        return new AddItemViewHolder(ItemView);
    }
}