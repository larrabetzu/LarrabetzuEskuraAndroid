package com.gorka.rssjarioa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginPush extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_push);

        final Button ok = (Button) findViewById(R.id.layout_loginpush_ok);
        final EditText erabiltzailea= (EditText) findViewById(R.id.layout_loginpush_erabiltzailea);
        final EditText pasahitza= (EditText) findViewById(R.id.layout_loginpush_pasahitza);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( erabiltzailea.getText().toString().isEmpty() || pasahitza.getText().toString().isEmpty()) {
                    Toast.makeText(LoginPush.this, "Pasa hitza eta erabiltzailea beteak egon behar dira",Toast.LENGTH_LONG).show();
                }else{
                    ParseUser.logInInBackground(erabiltzailea.getText().toString(), pasahitza.getText().toString(), new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                startActivity(new Intent("menupush"));
                            } else {
                                Toast.makeText(LoginPush.this, "Pasa hitza edo erabiltzailea ez da egokia",Toast.LENGTH_SHORT).show();
                                erabiltzailea.setText("");
                                pasahitza.setText("");
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
        finish();
    }
}