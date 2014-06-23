package com.gorka.rssjarioa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginPush extends Activity {
    int saiakerak = 0;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_push);

        final Button ok = (Button) findViewById(R.id.layout_loginpush_ok);
        final Button resetButton = (Button) findViewById(R.id.layout_reset_pass);
        final EditText erabiltzaileaEditText = (EditText) findViewById(R.id.layout_loginpush_erabiltzailea);
        final EditText pasahitza= (EditText) findViewById(R.id.layout_loginpush_pasahitza);



        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saiakerak<6) {
                    if (erabiltzaileaEditText.getText().toString().isEmpty() || pasahitza.getText().toString().isEmpty()) {
                        Toast.makeText(LoginPush.this, "Pasa hitza eta erabiltzailea beteak egon behar dira", Toast.LENGTH_LONG).show();
                        saiakerak+=1;
                    } else {
                        ParseUser.logInInBackground(erabiltzaileaEditText.getText().toString(), pasahitza.getText().toString(), new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    startActivity(new Intent("menupush"));
                                } else {
                                    Toast.makeText(LoginPush.this, "Pasa hitza edo erabiltzailea ez da egokia", Toast.LENGTH_SHORT).show();
                                    erabiltzaileaEditText.setText("");
                                    pasahitza.setText("");
                                    saiakerak+=1;
                                }
                            }
                        });
                    }
                }else {
                    Toast.makeText(LoginPush.this, "Saiakera guztiak amaitu dozuz", Toast.LENGTH_LONG).show();
                    ok.setVisibility(View.GONE);
                    resetButton.setVisibility(View.VISIBLE);
                }
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginPush.this);

                alert.setTitle("Emaila");
                alert.setIcon(R.drawable.warning);
                alert.setMessage("Pasahitza berrezartzeko zure emaila jarri");
                final EditText input = new EditText(LoginPush.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        saiakerak = 3;
                        String value = input.getText().toString();
                        try {
                            ParseUser.requestPasswordReset(value);
                            Toast.makeText(LoginPush.this, "Pasahitza berrerazteko email bat bidali da", Toast.LENGTH_LONG).show();
                            ok.setVisibility(View.VISIBLE);
                            resetButton.setVisibility(View.GONE);
                        } catch (ParseException e) {
                            Log.e("login", e.toString());
                            Toast.makeText(LoginPush.this, "Ezin izen da emaila bidali", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }
                });
                alert.show();
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