package com.gabeheath.apitemplate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.gabeheath.apitemplate.api.PasswordUpdateAPI;
import com.gabeheath.apitemplate.models.APIError;
import com.gabeheath.apitemplate.models.PasswordUpdate;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by gabeheath on 2/26/17.
 */
public class PasswordUpdateActivity extends AppCompatActivity {
    Toolbar mToolbar;
    Button mSubmitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_update_activity);

        mToolbar = (Toolbar) findViewById(R.id.passwordUpdateToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSubmitButton = (Button) findViewById( R.id.passwordUpdateSubmitButton );
        mSubmitButton.setOnClickListener(createPasswordUpdateButtonSubmitListener());
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

    private View.OnClickListener createPasswordUpdateButtonSubmitListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordField = (EditText) findViewById( R.id.updatePasswordField );
                CharSequence password = passwordField.getText();

                EditText passwordConfirmationField = (EditText) findViewById( R.id.updatePasswordConfirmationField );
                CharSequence passwordConfirmation = passwordConfirmationField.getText();

                EditText passwordCurrentConfirmationField = (EditText) findViewById( R.id.updateCurrentPasswordField );
                CharSequence currentPassword = passwordCurrentConfirmationField.getText();

                submitUpdatePassword(currentPassword, password, passwordConfirmation);
            }
        };
    }

    private void submitUpdatePassword(CharSequence currentPassword, CharSequence password, CharSequence passwordConfirmation) {
        // DEBUG - Logging for response
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); //NONE, BASIC, HEADERS, BODY.
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String jwtToken = preferences.getString("authToken", "");

                final Request request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " +jwtToken)
                        .build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        PasswordUpdateAPI service = retrofit.create(PasswordUpdateAPI.class);
        PasswordUpdate passwordUpdate = new PasswordUpdate(currentPassword.toString(), password.toString(), passwordConfirmation.toString());
        Call<PasswordUpdate> call = service.submitPasswordUpdate(passwordUpdate);

        call.enqueue(new Callback<PasswordUpdate>() {
            @Override
            public void onResponse(Call<PasswordUpdate> call, Response<PasswordUpdate> response) {
                if(response.isSuccessful()) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if( response.code() == 401 || response.code() == 500 ) {
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
            public void onFailure(Call<PasswordUpdate> call, Throwable t) {
                Log.d("-------fail------","Failed");
            }
        });
    }
}
