package io.supportkit.ethanifier.fragments;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import io.supportkit.ethanifier.R;

public class OnboardingFragment extends Fragment implements View.OnClickListener, TextWatcher {
    private static Button loginButton;
    private static EditText firstName;
    private static EditText lastName;
    private static EditText email;

    private OnUserLoginListener loginCallback;

    public interface OnUserLoginListener {
        void onUserLoggedIn(String firstName, String lastName, String email);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_onboarding, container, false);

        loginButton = (Button) view.findViewById(R.id.loginButton);
        firstName = (EditText) view.findViewById(R.id.firstNameText);
        lastName = (EditText) view.findViewById(R.id.lastNameText);
        email = (EditText) view.findViewById(R.id.emailText);

        loginButton.setOnClickListener(this);
        firstName.addTextChangedListener(this);
        lastName.addTextChangedListener(this);
        email.addTextChangedListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            loginCallback = (OnUserLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if (id == R.id.loginButton) {
            loginCallback.onUserLoggedIn(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        final String firstNameText = firstName.getText().toString(),
                lastNameText = lastName.getText().toString(),
                emailText = email.getText().toString();

        if(firstNameText.isEmpty() || lastNameText.isEmpty()) {
            loginButton.setEnabled(false);
        }
        else {
            loginButton.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) { }
}
