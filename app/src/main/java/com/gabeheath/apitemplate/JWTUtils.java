package com.gabeheath.apitemplate;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by gabeheath on 2/9/17.
 */

public class JWTUtils {

    public static String decoded(String JWTEncoded) throws UnsupportedEncodingException {
        String[] split = JWTEncoded.split("\\.");
        //Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
        //Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
        return getJson(split[1]);
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
