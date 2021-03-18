package com.example.c026_01_0319_2018_authentication_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    // Bundle keys
    private static final String FULL_NAME = "com.example.c026_01_0319_2018_authentication_app.home_activity.full_name";
    private static final String DATE_OF_BIRTH = "com.example.c026_01_0319_2018_authentication_app.home_activity.date_of_birth";
    private static final String EMAIL = "com.example.c026_01_0319_2018_authentication_app.home_activity.email";

    // URL to place holder image
    private static final String PLACE_HOLDER_IMAGE_URL = "https://source.unsplash.com/random/800x600";

    // Logging tag
    private static final String TAG = "HOME_ACTIVITY";


    private MaterialCardView userDetailsCard;
    private ImageView placeHolderView;
    private TextView fullNameView;
    private TextView dateOfBirthView;
    private ProgressBar progressBar;

    private String fullName;
    private String dateOfBirth;
    private String emailAddress;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Instantiate the views
        userDetailsCard = findViewById(R.id.home_user_details_card);
        placeHolderView = findViewById(R.id.home_place_holder_img);
        fullNameView = findViewById(R.id.home_full_name);
        dateOfBirthView = findViewById(R.id.home_date_of_birth);
        progressBar = findViewById(R.id.home_progress_bar);


        // Get the authenticated user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Instantiate the Firestore instance
        db = FirebaseFirestore.getInstance();

        if (savedInstanceState != null) {
            restoreInstanceStateValues(savedInstanceState);
            // Hide the progress bar and display user details
            updateUILoadingState(false);
            // Fetch placeholder image from the A
            fetchPlaceHolderImg();
        } else {
            if (currentUser != null) {
                // Fetch user details from the FirebaseFirestore db
                fetchUserDetailsFromDB();
                // Fetch placeholder image from the API
                fetchPlaceHolderImg();
            } else {
                // Redirect to the SignIn activity
                redirectToSignIn();
            }
        }
    }

    private void restoreInstanceStateValues(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Restore values that were saved  in the Bundle by the onSaveInstanceState method
            fullName = savedInstanceState.getString(FULL_NAME);
            fullNameView.setText(fullName);

            dateOfBirth = savedInstanceState.getString(DATE_OF_BIRTH);
            dateOfBirthView.setText(dateOfBirth);

            emailAddress = savedInstanceState.getString(EMAIL);
        }
    }

    private void fetchUserDetailsFromDB() {
        // Hide user details card and display the progress bar
        updateUILoadingState(true);

        // Get user id from the currently authenticated user
        String userId = currentUser.getUid();

        // The users document is reference by the user Id
        DocumentReference userDocumentRef = db.collection("users").document(userId);
        // Fetch the user details
        userDocumentRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot userDocument = task.getResult();

                if (userDocument.exists()) {
                    Log.d(TAG, "DocumentSnapshot data: " + userDocument.getData());
                    updateUserDetails(userDocument);
                    updateUIWithUserDetails();
                } else {
                    Log.d(TAG, "No such document!");
                }
            } else {
                Log.d(TAG, "get failed with", task.getException());
            }

            // Show the user details card and hide the progress bar
            updateUILoadingState(false);
        });

    }

    private void updateUILoadingState(boolean isLoading) {
        // Hide or display the ProgressBar based on the loading state
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            userDetailsCard.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            userDetailsCard.setVisibility(View.VISIBLE);
        }
    }

    private void updateUserDetails(DocumentSnapshot userDocument) {
        emailAddress = currentUser.getEmail();
        fullName = userDocument.get("first_name") + " " + userDocument.get("last_name");
        dateOfBirth = (String) userDocument.get("date_of_birth");

        String msg = "FullName: " + fullName + "DOB: " + dateOfBirth;
        Log.d(TAG, msg);
    }

    private void updateUIWithUserDetails() {
        fullNameView.setText(fullName);
        dateOfBirthView.setText(dateOfBirth);
    }

    private void redirectToSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
    }

    private void fetchPlaceHolderImg() {
        Picasso
                .get()
                .load(PLACE_HOLDER_IMAGE_URL)
                .into(placeHolderView);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save values that can be reused in case the user comes back to the activity
        outState.putString(FULL_NAME, fullName);
        outState.putString(DATE_OF_BIRTH, dateOfBirth);
        outState.putString(EMAIL, emailAddress);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        restoreInstanceStateValues(savedInstanceState);
    }

    public void sendEmailTo(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);

        // Set subject as well as email address to deliver email to
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Welcome " + fullName);

        // Start email intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}