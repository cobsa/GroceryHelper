package com.example.cobsa.groceryhelper;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment for basket list.
 */

public class BasketListingFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private String SELECTION;
    private String[] PROJECTION;

    BasketRecyclerViewAdapter mAdapter;
    LinearLayoutManager mLayoutManager;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_basket_listing,container,false);

        mContext = view.getContext();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_basket);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddItemDialog();
                Bundle args = new Bundle();
                args.putInt(AddItemDialog.SHOW_TAG, AddItemDialog.BASKET);
                newFragment.setArguments(args);
                newFragment.setTargetFragment(BasketListingFragment.this,0);
                newFragment.show(getFragmentManager(),"basket");
            }
        });


        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_show_basket);
        mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new BasketRecyclerViewAdapter();
        recyclerView.setAdapter(mAdapter);

        // Set cursor loader arguments

        SELECTION = null;
        PROJECTION = new String[] {MyContentProvider.BASKET_NAME,
                MyContentProvider.BASKET_ID_WITH_TABLE,
                "count("+ MyContentProvider.INGREDIENT_ID + ")",
                "sum(" + MyContentProvider.BASKET_ITEM_CHECKED + ")"};

        getLoaderManager().initLoader(0,new Bundle(), this);
        return view;
    }

    // Cursor loader functions

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(mContext, MyContentProvider.BASKETS_URI,PROJECTION,SELECTION,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }

}
