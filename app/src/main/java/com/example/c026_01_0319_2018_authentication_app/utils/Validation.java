package com.example.c026_01_0319_2018_authentication_app.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;

public class Validation {

    public static boolean isUserDataValid(HashMap<String, String> userDataErrors) {
        return Collections.frequency(userDataErrors.values(), null) == userDataErrors.size();
    }

    public static String validateName(String name) {
        if (TextUtils.isEmpty(name) || name.length() < 2) {
            return "Name must be at least 2 characters!";
        }

        return null;
    }

    public static String validateDate(String date) {
        // Formatted date will be like 09/30/2000
        String datePattern = "MM/dd/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        // Ensure that the date strictly matches the specified format
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            return "Date must match the format MM/dd/yyyy e.g 02/20/2000";
        }

        return null;
    }

    public static String validateEmail(String email) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email address!";
        }

        return null;
    }

    public static String validatePassword(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            return "Password should be at least 8 characters long!";
        }

        return null;
    }
}
