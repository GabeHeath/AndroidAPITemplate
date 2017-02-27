package com.gabeheath.apitemplate.api;

import com.gabeheath.apitemplate.Constants;
import com.gabeheath.apitemplate.models.EmailUpdate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;

/**
 * Created by gabeheath on 2/21/17.
 */

public interface EmailUpdateAPI {
    @Headers({
            "accept: version=" + Constants.API_VERSION,
            "Content-Type: application/json"
    })
    @PATCH("users/email_update")
    Call<EmailUpdate> submitEmailUpdate(@Body EmailUpdate emailUpdate);
}
