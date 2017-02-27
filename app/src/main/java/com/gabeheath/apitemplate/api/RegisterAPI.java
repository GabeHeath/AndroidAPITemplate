package com.gabeheath.apitemplate.api;

import com.gabeheath.apitemplate.Constants;
import com.gabeheath.apitemplate.models.Registration;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by gabeheath on 2/11/17.
 */

public interface RegisterAPI {
    @Headers({
            "accept: version=" + Constants.API_VERSION,
            "Content-Type: application/json"
    })
    @POST("users")
    Call<Registration> submitRegistration(@Body Registration registration);
}
