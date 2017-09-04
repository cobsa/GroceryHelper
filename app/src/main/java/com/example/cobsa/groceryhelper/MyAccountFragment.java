package com.example.cobsa.groceryhelper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Cobsa on 23.8.2017.
 */

public class MyAccountFragment extends Fragment {

    private TextView mEmailTextView;
    private Button mloginButton;
    private Button mSignUpButton;
    private Button mLogoutButton;
    private FirebaseUser mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_my_account, container, false);

        mloginButton = (Button) mView.findViewById(R.id.login_button);
        mLogoutButton = (Button) mView.findViewById(R.id.logout_button);
        mSignUpButton = (Button) mView.findViewById(R.id.sign_up_button);
        mEmailTextView = (TextView) mView.findViewById(R.id.my_account_email);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                setLayout();
            }
        });
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setLayout();
    }

    private void setLayout() {

        if(mUser != null) {
            // Hide login and sign up buttons
            mloginButton.setVisibility(View.INVISIBLE);
            mSignUpButton.setVisibility(View.INVISIBLE);
            // Show log out button
            mLogoutButton.setVisibility(View.VISIBLE);
            // Show personal info
            mEmailTextView.setText(mUser.getEmail());

        } else {
            // Show login and sign up buttons
            mloginButton.setVisibility(View.VISIBLE);
            mSignUpButton.setVisibility(View.VISIBLE);
            // Hide log out button
            mLogoutButton.setVisibility(View.INVISIBLE);
            //Setup click listeners
            mloginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new LoginFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_activity_fragment_container, fragment).addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                }
            });

            mSignUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new SignupFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_activity_fragment_container, fragment).addToBackStack(null)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

                }
            });
        }
    }
}
