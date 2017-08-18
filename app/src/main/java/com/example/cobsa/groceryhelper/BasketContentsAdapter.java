package com.example.cobsa.groceryhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


/**
 * Created by Cobsa on 17.8.2017.
 */

public class BasketContentsAdapter extends CursorRecyclerViewAdapter<BasketContentsAdapter.BasketContentsViewHolder> {

    public class BasketContentsViewHolder extends RecyclerView.ViewHolder {

        private TextView mIngredientName;
        private CheckBox mItemChecked;
        private Context mContext;
        private long mIngredientID;

        public BasketContentsViewHolder(View v) {
            super(v);
            // Assign view to private variables
            mIngredientName = (TextView) v.findViewById(R.id.row_basket_contents_ingredient_name);
            mItemChecked = (CheckBox) v.findViewById(R.id.row_basket_contents_ingredient_checked);

            mContext = v.getContext();

        }

    }

    private long mBasketID;

    BasketContentsAdapter(long basketId) {
        mBasketID = basketId;
    }

    @Override
    public void onBindViewHolder(final BasketContentsViewHolder holder, Cursor cursor) {

        holder.mIngredientName.setText(cursor.getString(cursor.getColumnIndex(MyContentProvider.INGREDIENTS_NAME)));
        holder.mIngredientID = cursor.getLong(cursor.getColumnIndex(MyContentProvider.INGREDIENT_ID));
        holder.mItemChecked.setChecked((cursor.getInt(cursor.getColumnIndex(MyContentProvider.BASKET_ITEM_CHECKED))==1)?true:false);
        holder.mItemChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ContentValues values = new ContentValues();
                values.put(MyContentProvider.BASKET_ITEM_CHECKED, (holder.mItemChecked.isChecked()?1:0));
                holder.mContext.getContentResolver().update(Uri.withAppendedPath(MyContentProvider.BASKETS_URI,
                        Long.toString(mBasketID)+ "/ingredient/" + Long.toString(holder.mIngredientID)),values,null,null);
            }
        });

    }

    @Override
    public BasketContentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout_basket_contents,parent,false);
        return new BasketContentsViewHolder(itemView);
    }
}
