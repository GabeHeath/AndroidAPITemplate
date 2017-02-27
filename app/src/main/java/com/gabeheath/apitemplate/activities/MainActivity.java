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
import android.widget.TextView;

import com.gabeheath.apitemplate.R;

/**
 * Created by gabeheath on 2/9/17.
 */

public class MainActivity extends AppCompatActivity {
    Button mLogoutButton;
    Button mEmailUpdateButton;
    Button mPasswordUpdateButton;
    TextView mEmailUpdateTextView;
    TextView mPasswordUpdateTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mLogoutButton = (Button) findViewById( R.id.logoutButton );
        mLogoutButton.setOnClickListener(createLogoutButtonListener());

        mEmailUpdateButton = (Button) findViewById( R.id.emailUpdateButton );
        mEmailUpdateButton.setOnClickListener(createEmailUpdateButtonListener());

        mPasswordUpdateButton = (Button) findViewById( R.id.passwordUpdateButton );
        mPasswordUpdateButton.setOnClickListener(createPasswordUpdateButtonListener());

        mEmailUpdateTextView = (TextView) findViewById( R.id.emailUpdateInstructions );
        mPasswordUpdateTextView = (TextView) findViewById( R.id.passwordUpdateInstructions );
    }

    public View.OnClickListener createLogoutButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    private View.OnClickListener createEmailUpdateButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmailUpdateActivity.class);
                startActivityForResult(intent,1);
            }
        };
    }

    private View.OnClickListener createPasswordUpdateButtonListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PasswordUpdateActivity.class);
                startActivityForResult(intent,2);
            }
        };
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode == RESULT_OK){
                    flashAlert(mEmailUpdateTextView);
                }
                break;
            case 2:
                if(resultCode == RESULT_OK){
                    flashAlert(mPasswordUpdateTextView);
                }
                break;
        }
    }

    public void flashAlert(final TextView alert) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flash_alert_animation);
                alert.startAnimation(animation);
            }
        },1000);

        final Handler hideHandler = new Handler();
        hideHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.flash_alert_hide_animation);
                alert.startAnimation(animation);
                alert.setVisibility(View.GONE);
            }
        },5000);
    }
}
