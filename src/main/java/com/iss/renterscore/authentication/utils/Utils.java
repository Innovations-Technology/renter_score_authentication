package com.iss.renterscore.authentication.utils;

import java.util.UUID;

public class Utils {

    public static String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
