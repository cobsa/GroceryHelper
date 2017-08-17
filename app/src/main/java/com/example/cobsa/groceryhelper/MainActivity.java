package com.example.cobsa.groceryhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.main_activity_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            BasketListingFragment fragment = new BasketListingFragment();
            fragment.setArguments(new Bundle());
            getSupportFragmentManager().beginTransaction().
                    add(R.id.main_activity_fragment_container, fragment).commit();
        }


    }




    public void AddBasket(View v) {


    }
}
