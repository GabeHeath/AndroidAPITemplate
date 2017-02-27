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
import com.gabeheath.apitemplate.api.RegisterAPI;
import com.gabeheath.apitemplate.models.APIError;
import com.gabeheath.apitemplate.models.Registration;
import com.gabeheath.apitemplate.models.User;

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

public class RegisterActivity extends AppCompatActivity {
    Toolbar mToolbar;
    Button mRegisterButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mToolbar = (Toolbar) findViewById(R.id.registrationToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRegisterButton = (Button) findViewById( R.id.registrationSubmitButton );
        mRegisterButton.setOnClickListener(createRegisterButtonSubmitListener());
    }

    private View.OnClickListener createRegisterButtonSubmitListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firstNameField = (EditText) findViewById( R.id.registrationFirstNameField );
                EditText lastNameField = (EditText) findViewById( R.id.registrationLastNameField );
                EditText usernameField = (EditText) findViewById( R.id.registrationUsernameField );
                EditText emailField = (EditText) findViewById( R.id.registrationEmailField );
                EditText passwordField = (EditText) findViewById( R.id.registrationPasswordField );
                EditText passwordConfirmField = (EditText) findViewById( R.id.registrationPasswordConfirmField );

                CharSequence firstName = firstNameField.getText();
                CharSequence lastName = lastNameField.getText();
                CharSequence username = usernameField.getText();
                CharSequence email = emailField.getText();
                CharSequence password = passwordField.getText();
                CharSequence passwordConfirmation = passwordConfirmField.getText();

                if( com.gabeheath.apitemplate.validators.RegistrationValidator.areRegistrationValuesValid(getApplicationContext(), firstName, lastName, username, email, password, passwordConfirmation) ) {
                    submitRegistration(firstName, lastName, username, email, password, passwordConfirmation);
                }
            }
        };
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

    private void submitRegistration(CharSequence firstName, CharSequence lastName, CharSequence username, CharSequence email, CharSequence password, CharSequence passwordConfirmation) {

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

        RegisterAPI service = retrofit.create(RegisterAPI.class);
        User user = new User(firstName.toString(), lastName.toString(), username.toString(), email.toString(), password.toString(), passwordConfirmation.toString());
        Registration registration = new Registration(user);
        Call<Registration> call = service.submitRegistration(registration);

        call.enqueue(new Callback<Registration>() {
            @Override
            public void onResponse(Call<Registration> call, Response<Registration> response) {
                if(response.isSuccessful()) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
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
            public void onFailure(Call<Registration> call, Throwable t) {
                Log.d("-------fail------","Failed");
            }
        });


    }
    
}
