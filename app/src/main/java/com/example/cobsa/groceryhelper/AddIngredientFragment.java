package com.example.cobsa.groceryhelper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Cobsa on 17.8.2017.
 */

public class AddIngredientFragment extends Fragment {

    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_basket_contents,container,false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_show_basket);



        return view;
    }
}
