package com.example.cobsa.groceryhelper;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

    private static final String FRAGMENT_TAG = "fragment_tag";
    private int CURRENT_NAV_ID;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String[] mMenuTitles;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private View mHeaderView;
    private TextView mEmailAddress;

    boolean shouldHomeOnBackButton = true; // setup back button behaviour


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        // Setting up navigation drawer

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        mHeaderView = mNavigationView.getHeaderView(0);
        mEmailAddress = (TextView) mHeaderView.findViewById(R.id.nav_header_email);


        if(findViewById(R.id.main_activity_fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            setFragment(new BasketListingFragment());
            CURRENT_NAV_ID = R.id.nav_home;
            setUpNavigation();
        }

        // User auth setup
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // User is signed in
                    mEmailAddress.setText(user.getEmail());
                } else {
                    // User is signed out
                    mEmailAddress.setText(getString(R.string.sign_in_or_register));
                }
            }
        };

        new SyncIngredients().execute();
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

    private void setFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_fragment_container,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

        mDrawerLayout.closeDrawers();
    }

    /*
    * Google firebase auth functions
    * */

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
                    getSupportFragmentManager().beginTransaction().
                            replace(R.id.main_activity_fragment_container,new MyAccountFragment())
                            .commit();
                }
            }
        });
    }

    public void signInButton(View v) {
        EditText emailEditText = (EditText) findViewById(R.id.login_email_edit_text);
        EditText passwordEditText = (EditText) findViewById(R.id.login_password_edit_text);

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

                        getSupportFragmentManager().beginTransaction().
                                replace(R.id.main_activity_fragment_container,new MyAccountFragment())
                                .commit();

                    }
                });
    }

    public void logOutButton(View v) {
        this.logout();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_fragment_container,new MyAccountFragment())
                .commit();


    }

    private void logout() {
        mAuth.signOut();
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

            private void setUpNavigation() {
                mNavigationView.setNavigationItemSelectedListener(
                        new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                setFragment(new BasketListingFragment());
                                CURRENT_NAV_ID = R.id.nav_home;
                                break;
                            case R.id.nav_my_account:
                                setFragment(new MyAccountFragment());
                        CURRENT_NAV_ID = R.id.nav_my_account;
                        break;
                    case R.id.nav_settings:
                        // TODO add settings fragment
                        CURRENT_NAV_ID = R.id.nav_settings;
                        break;
                    case R.id.nav_about:
                        // TODO add about fragment
                        CURRENT_NAV_ID = R.id.nav_about;
                        break;
                    default:
                        setFragment(new BasketListingFragment());
                        CURRENT_NAV_ID = R.id.nav_home;

                }

                item.setChecked(true);
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar,
                        R.string.open_drawer,R.string.close_drawer);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState(); // called as per documentation
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;
        }

        if(shouldHomeOnBackButton && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            if(CURRENT_NAV_ID != R.id.nav_home) {
                setFragment(new BasketListingFragment());
                CURRENT_NAV_ID = R.id.nav_home;
                return;
            }
        }
        super.onBackPressed();
    }
}
