package com.example.c026_01_0319_2018_authentication_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.c026_01_0319_2018_authentication_app.ui.DatePickerFragment;
import com.example.c026_01_0319_2018_authentication_app.utils.Validation;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";

    private static final String FIRST_NAME = "com.example.c026_01_0319_2018_authentication_app.sign_up_activity.first_name";
    private static final String LAST_NAME = "com.example.c026_01_0319_2018_authentication_app.sign_up_activity.last_name";
    private static final String DATE_OF_BIRTH = "com.example.c026_01_0319_2018_authentication_app.sign_up_activity.date_of_birth";
    private static final String EMAIL = "com.example.c026_01_0319_2018_authentication_app.sign_up_activity.email";
    private static final String PASSWORD = "com.example.c026_01_0319_2018_authentication_app.sign_up_activity.password";


    private TextInputLayout firstNameInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout dateOfBirthInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private Button signUpButton;
    private Button signInButton;
    private ProgressBar progressBar;


    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Objects.requireNonNull(getSupportActionBar()).hide();

        // Instantiate Views
        firstNameInputLayout = findViewById(R.id.sign_up_first_name);
        lastNameInputLayout = findViewById(R.id.sign_up_last_name);
        dateOfBirthInputLayout = findViewById(R.id.sign_up_dob);
        emailInputLayout = findViewById(R.id.sign_up_email);
        passwordInputLayout = findViewById(R.id.sign_up_password);
        signUpButton = findViewById(R.id.sign_up_sign_up_button);
        signInButton = findViewById(R.id.sign_up_sign_in_button);
        progressBar = findViewById(R.id.sign_up_progress_bar);

        // Set on click listener for the date picker icon
        dateOfBirthInputLayout.setEndIconOnClickListener(this::showDatePickerDialog);

        // Restore the previously saved state
        restoreInstanceStateValues(savedInstanceState);

        // Initialize the Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize the FirebaseFirestore
        db = FirebaseFirestore.getInstance();
    }

    private void restoreInstanceStateValues(@NonNull Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore values that were saved in the Bundle by the onSaveInstanceState method
            firstNameInputLayout.getEditText().setText(savedInstanceState.getString(FIRST_NAME));
            lastNameInputLayout.getEditText().setText(savedInstanceState.getString(LAST_NAME));
            dateOfBirthInputLayout.getEditText().setText(savedInstanceState.getString(DATE_OF_BIRTH));
            emailInputLayout.getEditText().setText(savedInstanceState.getString(EMAIL));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save values that can be reused in case the user comes back to the activity
        outState.putString(FIRST_NAME, getStringFromInputLayout(firstNameInputLayout));
        outState.putString(LAST_NAME, getStringFromInputLayout(lastNameInputLayout));
        outState.putString(DATE_OF_BIRTH, getStringFromInputLayout(dateOfBirthInputLayout));
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

    public void showDatePickerDialog(View view) {
        // Focus on the DateOfBirth input
        dateOfBirthInputLayout.requestFocus();

        // Display the Date Picker
        DialogFragment newFragment = new DatePickerFragment(dateOfBirthInputLayout.getEditText());
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void signUpUser(View view) {
        // Read the data from inputs
        HashMap<String, String> userData = readUserData();
        // Validate the user data and set errors
        HashMap<String, String> userDataErrors = validateUserData(userData);
        // Update UI to show the errors;
        updateUIWithErrors(userDataErrors);

        // If user data meets our requirements add user to db
        if (Validation.isUserDataValid(userDataErrors)) {
            makeAuthRequest(userData);
        }

    }

    private HashMap<String, String> readUserData() {
        HashMap<String, String> userData = new HashMap<>();

        userData.put(FIRST_NAME, getStringFromInputLayout(firstNameInputLayout));
        userData.put(LAST_NAME, getStringFromInputLayout(lastNameInputLayout));
        userData.put(EMAIL, getStringFromInputLayout(emailInputLayout));
        userData.put(DATE_OF_BIRTH, getStringFromInputLayout(dateOfBirthInputLayout));
        userData.put(PASSWORD, getStringFromInputLayout(passwordInputLayout));

        return userData;
    }

    private HashMap<String, String> validateUserData(HashMap<String, String> userData) {

        HashMap<String, String> errors = new HashMap<>();

        for (HashMap.Entry userDataEntry : userData.entrySet()) {
            String key = (String) userDataEntry.getKey();
            String value = (String) userDataEntry.getValue();

            // Holds the current error message
            String errorMsg;

            if (DATE_OF_BIRTH.equals(key)) {
                errorMsg = Validation.validateDate(value);
            } else if (EMAIL.equals(key)) {
                errorMsg = Validation.validateEmail(value);
            } else if (PASSWORD.equals(key)) {
                errorMsg = Validation.validatePassword(value);
            } else {
                errorMsg = Validation.validateName(value);
            }

            errors.put(key, errorMsg);
        }

        return errors;
    }

    private void updateUIWithErrors(HashMap<String, String> userDataErrors) {
        for (HashMap.Entry userDataErrorsEntry : userDataErrors.entrySet()) {
            String key = (String) userDataErrorsEntry.getKey();
            String value = (String) userDataErrorsEntry.getValue();

            if (FIRST_NAME.equals(key)) {
                firstNameInputLayout.setError(value);
            } else if (LAST_NAME.equals(key)) {
                lastNameInputLayout.setError(value);
            } else if (DATE_OF_BIRTH.equals(key)) {
                dateOfBirthInputLayout.setError(value);
            } else if (EMAIL.equals(key)) {
                emailInputLayout.setError(value);
            } else if (PASSWORD.equals(key)) {
                passwordInputLayout.setError(value);
            }

        }
    }

    private void makeAuthRequest(HashMap<String, String> userData) {
        // Disable inputs and show progress bar
        updateUILoadingState(true);

        // Get user data
        String email = userData.get(EMAIL);
        String password = userData.get(PASSWORD);

        // Attempt to create a user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, add the User to FireStore
                        Log.d(TAG, "registerUser:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        addUserToDB(user, userData);
                    } else {
                        // Display an error message to the user
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication failed! " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();

                        // Enable inputs and disable progress bar
                        updateUILoadingState(false);
                    }
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
        firstNameInputLayout.getEditText().setEnabled(!isLoading);
        lastNameInputLayout.getEditText().setEnabled(!isLoading);
        dateOfBirthInputLayout.getEditText().setEnabled(!isLoading);
        emailInputLayout.getEditText().setEnabled(!isLoading);
        passwordInputLayout.getEditText().setEnabled(!isLoading);
        signUpButton.setEnabled(!isLoading);
        signInButton.setEnabled(!isLoading);
    }

    private void addUserToDB(FirebaseUser user, HashMap<String, String> userData) {
        HashMap<String, Object> userDocument = new HashMap<>();
        userDocument.put("first_name", userData.get(FIRST_NAME));
        userDocument.put("last_name", userData.get(LAST_NAME));
        userDocument.put("date_of_birth", userData.get(DATE_OF_BIRTH));

        // Add a new user with the user id from auth
        db.collection("users").document(user.getUid())
                .set(userDocument)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Authentication successful!", Toast.LENGTH_SHORT).show();
                    // Enable inputs and hide progress bar
                    updateUILoadingState(false);
                    redirectToHome();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Authentication failure!", Toast.LENGTH_SHORT).show();
                    // Enable inputs and hide progress bar
                    updateUILoadingState(false);
                });
    }

    private void redirectToHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
    }

    public void redirectToSignIn(View view) {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }
}