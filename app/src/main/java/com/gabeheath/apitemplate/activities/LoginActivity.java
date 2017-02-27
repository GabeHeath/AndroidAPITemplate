package com.gabeheath.apitemplate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gabeheath.apitemplate.Constants;
import com.gabeheath.apitemplate.R;

import com.gabeheath.apitemplate.models.APIError;
import com.gabeheath.apitemplate.validators.LoginValidator;
import com.gabeheath.apitemplate.api.LoginServiceAPI;
import com.gabeheath.apitemplate.models.Login;

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
 * Created by gabeheath on 2/9/17.
 */

public class LoginActivity extends AppCompatActivity {
    Button mLoginSubmit;
    TextView mRegisterLink;
    TextView mForgotPasswordLink;
    TextView mLoginAlertInstructionsTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mLoginSubmit = (Button) findViewById( R.id.loginButton );
        mLoginSubmit.setOnClickListener(createLoginButtonListener());

        mRegisterLink = (TextView) findViewById( R.id.registerLink );
        mRegisterLink.setOnClickListener(createRegisterLinkListener());

        mForgotPasswordLink = (TextView) findViewById( R.id.forgotPasswordLink );
        mForgotPasswordLink.setOnClickListener(createForgotPasswordLinkListener());

        mLoginAlertInstructionsTextView = (TextView) findViewById( R.id.loginAlertInstructions );
    }

    public View.OnClickListener createLoginButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                EditText emailField = (EditText) findViewById( R.id.emailField );
                EditText passwordField = (EditText) findViewById( R.id.passwordField );

                CharSequence email = emailField.getText();
                CharSequence password = passwordField.getText();

                if( areLoginValuesValid( email, password ) ) {

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

                    LoginServiceAPI service = retrofit.create(LoginServiceAPI.class);
                    Login login = new Login(email.toString(), password.toString());
                    Call<Login> call = service.submitLogin(login);
                    call.enqueue(new Callback<Login>() {
                        @Override
                        public void onResponse(Call<Login> call, Response<Login> response) {
                            if (response.isSuccessful()) {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("authToken", response.body().getAuthToken());
                                editor.apply();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                try {
                                    Converter<ResponseBody,APIError> converter = retrofit.responseBodyConverter(APIError.class, new Annotation[0]);
                                    APIError errorResponse = null;
                                    errorResponse = converter.convert(response.errorBody());
                                    for( String u : errorResponse.getErrors() ) {
                                        Toast.makeText(getApplicationContext(),u,Toast.LENGTH_LONG).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Login> call, Throwable t) {
                            Toast.makeText( getApplicationContext(), getApplicationContext().getString( R.string.warning_api_call_failed ), Toast.LENGTH_LONG ).show();
                        }
                    });
                }
            }
        };
    }

    private View.OnClickListener createRegisterLinkListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent,1);
            }
        };
    }

    private View.OnClickListener createForgotPasswordLinkListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivityForResult(intent,2);
            }
        };
    }

    public boolean areLoginValuesValid( CharSequence email, CharSequence password ) {
        return LoginValidator.isEmailValid( this, email ) && LoginValidator.isPasswordValid( this, password );
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode ) {
            case 1:
                if(resultCode == RESULT_OK){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLoginAlertInstructionsTextView.setText(getResources().getString(R.string.new_registration_alert_instructions));
                            mLoginAlertInstructionsTextView.setVisibility(View.VISIBLE);
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flash_alert_animation);
                            mLoginAlertInstructionsTextView.startAnimation(animation);
                        }
                    },1000);

                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mLoginAlertInstructionsTextView.setText(getResources().getString(R.string.forgot_password_alert_instructions));
                            mLoginAlertInstructionsTextView.setVisibility(View.VISIBLE);
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flash_alert_animation);
                            mLoginAlertInstructionsTextView.startAnimation(animation);
                        }
                    },1000);

                }
                break;
        }
    }
}
