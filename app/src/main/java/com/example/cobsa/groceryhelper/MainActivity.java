package com.example.cobsa.groceryhelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;

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

        new SyncIngredients().execute();
    }


    private class SyncIngredients extends AsyncTask<Void,Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef;
            Cursor cursor = getContentResolver().query(MyContentProvider.INGREDIENTS_URI,new String[] {
                    MyContentProvider.INGREDIENTS_NAME},null,null,null);
            while(cursor.moveToNext()) {
                myRef = database.getReference("ingredient").
                        child(cursor.getString(cursor.getColumnIndex(MyContentProvider.INGREDIENTS_NAME))).child("TIMESTAMP");
                myRef.setValue(System.currentTimeMillis());

            }
            myRef = database.getReference("ingredient");
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ingredient_name : dataSnapshot.getChildren() ) {
                        ContentValues values = new ContentValues();
                        values.put(MyContentProvider.INGREDIENTS_NAME,ingredient_name.getKey());
                        getContentResolver().insert(MyContentProvider.INGREDIENTS_URI,values);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            return null;
        }
    }
}
