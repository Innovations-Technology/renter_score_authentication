package com.iss.renterscore.authentication.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public class Utils {

    private Utils() {
        // empty constructor
    }

    public static final String EMAIL = "email";
    public static final String USER_NAME = "userName";
    public static final String TITLE = "title";
    public static final String MAIL_TITLE = "Renter Score Application";
    public static final String BASE_URL = "baseUrl";
    public static final String ACCOUNT_STATUS = "Account Status Change";
    public static final String EMAIL_VERIFICATION = "User Email Verification";
    public static final String PASSWORD_RESET_LINK = "Password Reset Link";
    public static String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public static LocalDate convertDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
        return LocalDate.parse(date, formatter);
    }
}
