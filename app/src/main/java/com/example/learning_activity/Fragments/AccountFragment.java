package com.example.learning_activity.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learning_activity.Activities.ChangeEmailActivity;
import com.example.learning_activity.Activities.ChangePasswordActivity;
import com.example.learning_activity.Activities.DeleteAccountActivity;
import com.example.learning_activity.Activities.LoginActivity;
import com.example.learning_activity.R;
import com.google.firebase.auth.FirebaseAuth;

public class AccountFragment extends Fragment {

    private CardView changeEmailCV,
                    changePassCV,
                    deleteAccountCV,
                    loggoutCV;


    public AccountFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        changeEmailCV = view.findViewById(R.id.changeEmailCV);
        changePassCV = view.findViewById(R.id.changePassCV);
        deleteAccountCV = view.findViewById(R.id.deleteAccountCV);
        loggoutCV = view.findViewById(R.id.loggoutCV);

        changeEmailCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChangeEmailActivity.class);
                startActivity(intent);
            }
        });

        changePassCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        deleteAccountCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DeleteAccountActivity.class);
                startActivity(intent);
            }
        });

        loggoutCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}