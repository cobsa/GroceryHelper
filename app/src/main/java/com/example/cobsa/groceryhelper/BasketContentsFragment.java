package com.example.cobsa.groceryhelper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shows basket contents and handles checked items ie. items that are already picked.
 */


public class BasketContentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    public static final String BASKET_ID = "BASKETID";
    private BasketContentsAdapter mAdapter;
    private Context mContext;
    private long mBasketID;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(getArguments() != null && getArguments().containsKey(BASKET_ID) ) {
            mBasketID  = getArguments().getLong(BASKET_ID);
        }
        else {
            mBasketID = -1; // No basket id available
            throw new IllegalArgumentException("No Basket ID provided.");
        }


        View view = inflater.inflate(R.layout.fragment_basket_contents,container,false);
        mContext = view.getContext();


        //Set floating Action button
        assignFAB();

        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_basket_contents_recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // set adapter

        mAdapter = new BasketContentsAdapter(mBasketID);
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(1,new Bundle(),this);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // So data is refreshed when coming back to activity
        getLoaderManager().restartLoader(1,new Bundle(),this);
        // Refresh FAB
        assignFAB();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] PROJECTION = new String[] {
                MyContentProvider.INGREDIENTS_NAME,
                MyContentProvider.INGREDIENT_ID_WITH_TABLE,
                "COUNT(" + MyContentProvider.INGREDIENT_ID_WITH_TABLE+ ")",
                MyContentProvider.BASKET_ITEM_CHECKED};

        return new CursorLoader(mContext, Uri.withAppendedPath(MyContentProvider.BASKETS_URI,
                Long.toString(mBasketID)+ "/ingredient"),PROJECTION,null,null,MyContentProvider.BASKET_ITEM_CHECKED + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    private void assignFAB() {
        // Setup Floating action button to be correct for the fragment
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction =
                        ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction();

                // Add new fragment and set arguments

                Fragment newFragment = new AddIngredientFragment();
                Bundle fragmentArgs = new Bundle();
                fragmentArgs.putLong(AddIngredientFragment.BASKET_ID,mBasketID);
                newFragment.setArguments(fragmentArgs);
                transaction.replace(R.id.main_activity_fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
    }

}