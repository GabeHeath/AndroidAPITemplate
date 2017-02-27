package com.gabeheath.apitemplate.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.gabeheath.apitemplate.Constants;
import com.gabeheath.apitemplate.JWTUtils;
import com.gabeheath.apitemplate.R;
import com.gabeheath.apitemplate.api.BuildObjectApi;
import com.gabeheath.apitemplate.models.Build;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StartLogic extends AppCompatActivity {
    TextView text_id_1, text_name_1, text_marks_1 ;
    TextView text_id_2, text_name_2, text_marks_2 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isConnectedToInternet()) {
            checkBuild();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.no_internet_dialog_title)
                    .setMessage(R.string.no_internet_dialog_message)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    boolean isConnectedToInternet() {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    void checkBuild() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BuildObjectApi service = retrofit.create(BuildObjectApi.class);

        Call<Build> call = service.getBuildDetails();

        call.enqueue(new Callback<Build>() {
            @Override
            public void onResponse(Call<Build> call, Response<Build> response) {
                if (response.isSuccessful()) {
                    String supportLevel;
                    supportLevel = response.body().getSupportLevel();
                    switch (supportLevel) {
                        case "active":
                            try {
                                checkAuthentication();
                            } catch (UnsupportedEncodingException | JSONException e) {
                                e.printStackTrace();
                                gotoLogin();
                            }
                            break;
                        case "deprecated":
                            //TODO
                            break;
                        case "unsupported":
                            //TODO
                            break;
                        case "invalid":
                            //TODO
                            break;
                        case "failed_to_connect":
                            //TODO
                            Log.d("------------------", "FAILED TO CONNECT TO API");
                            break;
                    }
                } else {
                    Log.d("------------------", "FAILED TO CONNECT TO API");
                }
            }

            @Override
            public void onFailure(Call<Build> call, Throwable t) {
                new AlertDialog.Builder(StartLogic.this)
                        .setTitle(R.string.failed_to_connect_dialog_title)
                        .setMessage(R.string.failed_to_connect_dialog_message)
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        });

    }

    void checkAuthentication() throws UnsupportedEncodingException, JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String jwtToken = preferences.getString("authToken", "");

        if ( jwtToken.length() > 0 && !isJWTExpired(jwtToken) ) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // TODO - eventually implement refresh tokens
            //Not Authenticated
            gotoLogin();
        }
    }

    private boolean isJWTExpired (String jwtToken) throws UnsupportedEncodingException, JSONException {
        JSONObject jwtBody = new JSONObject(JWTUtils.decoded(jwtToken));
        long jwtExpiration = jwtBody.getLong("exp");
        long now = System.currentTimeMillis()/1000;
        return now > jwtExpiration;
    }

    private void gotoLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
