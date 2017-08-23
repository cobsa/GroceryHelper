package com.example.cobsa.groceryhelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String[] mMenuTitles;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        // Setting up navigation drawer

        mMenuTitles = new String[] {"HOME", "SETTINGS", "SIGNUP", "Login"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        if(findViewById(R.id.main_activity_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            BasketListingFragment fragment = new BasketListingFragment();
            fragment.setArguments(new Bundle());
            getSupportFragmentManager().beginTransaction().
                    add(R.id.main_activity_fragment_container, fragment).commit();
            mDrawerLayout.closeDrawers();
        }




        // User auth setup
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // User is signed in
                    Log.d("Firebase", "User logged in: " + user.getUid());
                } else {
                    // User is signed out
                    Log.d("Firebase", "User logged out");
                }
            }
        };

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

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void signUpButton(View v) {
        EditText emailEditText = (EditText) findViewById(R.id.email_edit_text);
        EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        this.CreateAccount(emailEditText.getText().toString(), passwordEditText.getText().toString());
    }

    private void CreateAccount(String email, String password) {
        // Validate input

        // Add user
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("Firebase", "createUserWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, task.getException().toString(),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            getString(R.string.logged_in_as)+ " " + mAuth.getCurrentUser().getEmail(),
                            Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().popBackStack();
                }
            }
        });
    }

    public void signInButton(View v) {
        EditText emailEditText = (EditText) findViewById(R.id.email_edit_text);
        EditText passwordEditText = (EditText) findViewById(R.id.password_edit_text);

        this.SignIn(emailEditText.getText().toString(),passwordEditText.getText().toString());

    }

    private void SignIn(String email, String password) {
        // Validate input


        // Login user

        mAuth.signInWithEmailAndPassword(email,password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Firebase", "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("Firebase", "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

        private void selectItem(int position) {
            Fragment fragment = new SignupFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container,fragment).addToBackStack(null).commit();


        }
    }
}
