package com.gabeheath.apitemplate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gabeheath.apitemplate.Constants;
import com.gabeheath.apitemplate.R;
import com.gabeheath.apitemplate.api.ForgotPasswordAPI;
import com.gabeheath.apitemplate.models.APIError;
import com.gabeheath.apitemplate.models.ForgotPassword;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gabeheath on 2/22/17.
 */

public class ForgotPasswordActivity extends AppCompatActivity {
    Toolbar mToolbar;
    Button mSubmitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);

        mToolbar = (Toolbar) findViewById(R.id.forgotPasswordToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSubmitButton = (Button) findViewById( R.id.forgotPasswordSubmitButton );
        mSubmitButton.setOnClickListener(createEmailUpdateButtonSubmitListener());


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener createEmailUpdateButtonSubmitListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText emailField = (EditText) findViewById( R.id.forgotPasswordField );
                CharSequence email = emailField.getText();
                submitForgotPassword(email);
            }
        };
    }

    private void submitForgotPassword(CharSequence email) {
        // DEBUG - Logging for response
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); //NONE, BASIC, HEADERS, BODY.
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        OkHttpClient client = httpClient.build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ForgotPasswordAPI service = retrofit.create(ForgotPasswordAPI.class);
        ForgotPassword forgotPassword = new ForgotPassword(email.toString());
        Call<ForgotPassword> call = service.submitForgotPassword(forgotPassword);

        call.enqueue(new Callback<ForgotPassword>() {
            @Override
            public void onResponse(Call<ForgotPassword> call, Response<ForgotPassword> response) {
                if(response.isSuccessful()) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if (response.code() == 401 || response.code() == 500) {
                        //TODO if status code is 401 then try to refresh auth token or force user to re log in
                    } else {
                        try {
                            Converter<ResponseBody, APIError> converter = retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
                            APIError errorResponse = null;
                            errorResponse = converter.convert(response.errorBody());
                            for (String u : errorResponse.getErrors()) {
                                Toast.makeText(getApplicationContext(), u, Toast.LENGTH_LONG).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ForgotPassword> call, Throwable t) {
                Log.d("-------fail------","Failed");
            }
        });
    }
}
