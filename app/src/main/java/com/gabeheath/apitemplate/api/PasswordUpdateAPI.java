package com.gabeheath.apitemplate.api;

import com.gabeheath.apitemplate.Constants;
import com.gabeheath.apitemplate.models.PasswordUpdate;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;

/**
 * Created by gabeheath on 2/26/17.
 */

public interface PasswordUpdateAPI {
    @Headers({
            "accept: version=" + Constants.API_VERSION,
            "Content-Type: application/json"
    })
    @PATCH("password/update")
    Call<PasswordUpdate> submitPasswordUpdate(@Body PasswordUpdate passwordUpdate);
}
