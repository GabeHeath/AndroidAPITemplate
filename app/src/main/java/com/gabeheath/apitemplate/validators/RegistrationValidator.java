package com.gabeheath.apitemplate.validators;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.gabeheath.apitemplate.R;

/**
 * Created by gabeheath on 2/9/17.
 */

public class RegistrationValidator {
    boolean result;

    public static boolean areRegistrationValuesValid(Context currentContext, CharSequence firstName, CharSequence lastName,
                                                     CharSequence username, CharSequence email, CharSequence password,
                                                     CharSequence passwordConfirm)
    {
        return LoginValidator.isNameValid( currentContext, firstName )
                && LoginValidator.isNameValid( currentContext, lastName )
                && isUsernameValid( currentContext, username )
                && LoginValidator.isEmailValid( currentContext, email )
                && LoginValidator.isPasswordValid( currentContext, password )
                && doPasswordsMatch( currentContext, password, passwordConfirm );
    }

    private static boolean isUsernameValid(final Context currentContext, final CharSequence username ) {
        if( username.toString().isEmpty() ) {
            Toast.makeText( currentContext, currentContext.getString( R.string.warning_username_empty ), Toast.LENGTH_LONG ).show();
            return false;
        }

        return true;
    }


    private static boolean doPasswordsMatch(Context currentContext, CharSequence password, CharSequence passwordConfirm ) {
        if( !TextUtils.equals( password, passwordConfirm ) ) {
            Toast.makeText( currentContext, currentContext.getString( R.string.warning_passwords_do_not_match ), Toast.LENGTH_LONG ).show();
            return false;
        }

        return true;
    }
}
