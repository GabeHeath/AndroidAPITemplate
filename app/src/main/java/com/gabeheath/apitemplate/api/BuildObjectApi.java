package com.gabeheath.apitemplate.api;

import com.gabeheath.apitemplate.Constants;
import com.gabeheath.apitemplate.models.Build;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by gabeheath on 2/9/17.
 */
public interface BuildObjectApi {
    String BUILD = Constants.BUILD;
    @GET("builds/" + BUILD)
    Call<Build> getBuildDetails();
}
