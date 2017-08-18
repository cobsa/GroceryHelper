package com.example.cobsa.groceryhelper;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Cobsa on 17.8.2017.
 */

public class AddIngredientFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String BASKET_ID = "BASKETID";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private AddItemAdapter mAdapter;
    private Context mContext;
    private long mBasketID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_ingredient,container,false);
        mContext = view.getContext();

        // Get BasketId from arguments
        if(getArguments() != null) {
            if(getArguments().containsKey(BASKET_ID)) {
                mBasketID = getArguments().getLong(BASKET_ID);
            }
        }
        else {
            // If no basket id is provider in arguments
            throw new IllegalArgumentException("Provide Basket ID.");
        }

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.add_ingredient_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new AddItemDialog();
                Bundle args = new Bundle();
                args.putInt(AddItemDialog.SHOW_TAG, AddItemDialog.INGREDIENT);
                newFragment.setArguments(args);
                newFragment.setTargetFragment(AddIngredientFragment.this,0);
                newFragment.show(getFragmentManager(),"basket");
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(R.id.add_item_recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mAdapter = new AddItemAdapter(mBasketID);
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(0,null,this);



        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] PROJECTION = new String[] {MyContentProvider.INGREDIENTS_NAME,MyContentProvider.INGREDIENT_ID};

        return new CursorLoader(mContext,MyContentProvider.INGREDIENTS_URI,PROJECTION,null,null,null);
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
