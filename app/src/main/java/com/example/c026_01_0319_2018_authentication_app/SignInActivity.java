package com.example.c026_01_0319_2018_authentication_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.c026_01_0319_2018_authentication_app.utils.Validation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final String EMAIL = "com.example.c026_01_0319_2018_authentication_app.login.email";
    private static final String PASSWORD = "com.example.c026_01_0319_2018_authentication_app.login.password";

    private FirebaseAuth mAuth;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private Button signInButton;
    private Button signUpButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Objects.requireNonNull(getSupportActionBar()).hide();

        // Instantiate views
        emailInputLayout = findViewById(R.id.login_email);
        passwordInputLayout = findViewById(R.id.login_password);
        signInButton = findViewById(R.id.sign_in_sign_in_button);
        signUpButton = findViewById(R.id.sign_in_sign_up_button);
        progressBar = findViewById(R.id.login_progress_bar);

        // Restore the previously saved state
        restoreInstanceStateValues(savedInstanceState);

        // Initialize the Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void restoreInstanceStateValues(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore values that were saved in the Bundle by the onSaveInstanceState method
            emailInputLayout.getEditText().setText(savedInstanceState.getString(EMAIL));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save values that can be reused in case the user comes back to the activity
        outState.putString(EMAIL, getStringFromInputLayout(emailInputLayout));
    }

    private String getStringFromInputLayout(TextInputLayout targetInputLayout) {
        return targetInputLayout.getEditText().getText().toString();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        restoreInstanceStateValues(savedInstanceState);
    }

    public void signInUser(View view) {
        // Read values of inputs
        HashMap<String, String> userData = readUserData();
        // Check the user data for any errors, i.e inputs that do not
        // conform to the predefined format
        HashMap<String, String> userDataErrors = validateUserData(userData);
        // Update the UI to display errors in the entered data. 
        displayUserDataErrors(userDataErrors);

        if (Validation.isUserDataValid(userDataErrors)) {
            makeAuthRequest(userData);
        }
    }

    private HashMap<String, String> readUserData() {
        // Holds values from the inputs
        HashMap<String, String> userData = new HashMap<>();

        // Read values of the inputs and add then to the map of user data.
        userData.put(EMAIL, getStringFromInputLayout(emailInputLayout));
        userData.put(PASSWORD, getStringFromInputLayout(passwordInputLayout));

        return userData;
    }

    private HashMap<String, String> validateUserData(HashMap<String, String> userData) {
        // Holds key, error pairs for each of the inputs e.g EMAIL, "email error"
        HashMap<String, String> userDataErrors = new HashMap<>();

        // Validate each of the inputs
        for (HashMap.Entry userDataEntry : userData.entrySet()) {
            String key = (String) userDataEntry.getKey();
            String value = (String) userDataEntry.getValue();

            if (EMAIL.equals(key)) {
                userDataErrors.put(EMAIL, Validation.validateEmail(value));
            } else if (PASSWORD.equals(key)) {
                userDataErrors.put(PASSWORD, Validation.validatePassword(value));
            }
        }

        return userDataErrors;
    }

    private void displayUserDataErrors(HashMap<String, String> userDataErrors) {
        // Update each of the TextInputs to show errors
        for (HashMap.Entry userDataErrorsEntry : userDataErrors.entrySet()) {
            String key = (String) userDataErrorsEntry.getKey();
            String value = (String) userDataErrorsEntry.getValue();

            if (EMAIL.equals(key)) {
                emailInputLayout.setError(value);
            } else if (PASSWORD.equals(key)) {
                passwordInputLayout.setError(value);
            }
        }
    }

    private void makeAuthRequest(HashMap<String, String> userData) {
        // Disable inputs and show progress bar
        updateUILoadingState(true);

        // Extract values from the map
        String email = userData.get(EMAIL);
        String password = userData.get(PASSWORD);

        String errorMsg = "Authentication failure! ";
        // Make auth request to Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                "Authentication successful!", Toast.LENGTH_SHORT).show();
                        redirectToHome();
                    } else {
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }

                    // Enable inputs and hide progress bar
                    updateUILoadingState(false);
                })
                .addOnFailureListener(this, failure -> {

                    Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void updateUILoadingState(boolean isLoading) {
        // Hide or display the ProgressBar based on the loading state
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }

        // Hide the password input anytime loading changes
        passwordInputLayout.getEditText().setTransformationMethod(new PasswordTransformationMethod());

        // Activate or deactivate inputs based on the loading stated
        emailInputLayout.getEditText().setEnabled(!isLoading);
        passwordInputLayout.getEditText().setEnabled(!isLoading);
        signInButton.setEnabled(!isLoading);
        signUpButton.setEnabled(!isLoading);
    }

    private void redirectToHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    public void redirectToSignUp(View view) {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }
}