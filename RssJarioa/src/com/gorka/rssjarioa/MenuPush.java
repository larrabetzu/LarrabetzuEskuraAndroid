package com.gorka.rssjarioa;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.parse.ParsePush;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class MenuPush extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_push);

        final TextView pushnumeroa = (TextView) findViewById(R.id.layout_menupush_pushnumeroa);
        final EditText pushtituloa = (EditText) findViewById(R.id.layout_menupush_tituloa);
        final EditText pushtestua = (EditText) findViewById(R.id.layout_menupush_testua);
        final CheckBox urlrik = (CheckBox) findViewById(R.id.layout_menupush_urlcheckbox);
        final EditText pushurl =(EditText) findViewById(R.id.layout_menupush_url);
        final Button pushlogout = (Button) findViewById(R.id.layout_menupush_logout);
        final Button pushok = (Button) findViewById(R.id.layout_menupush_ok);


        urlrik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(urlrik.isChecked()){
                    pushurl.setVisibility(View.VISIBLE);
                }else {
                    pushurl.setVisibility(View.GONE);
                }
            }
        });

        pushok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pushtituloa.getText().toString().isEmpty() || pushtestua.getText().toString().isEmpty()) {
                    Toast.makeText(MenuPush.this, "Tituloa eta testua beteak egon behar dira", Toast.LENGTH_SHORT).show();
                }else{
                    if (urlrik.isChecked() && pushurl.getText().toString().isEmpty()){
                        Toast.makeText(MenuPush.this, "Url-a beteta egon behar da", Toast.LENGTH_SHORT).show();
                    }else {
                        JSONObject data = null;
                        try {
                            data = new JSONObject("{ \"action\": \"com.gorka.rssjarioa.UPDATE_STATUS\"," +
                                    " \"tit\": \""+pushtituloa.getText().toString()+"\", " +
                                    "\"tex\": \""+pushtestua.getText().toString()+"\", " +
                                    "\"url\": \""+pushurl.getText().toString()+"\" }");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ParsePush push = new ParsePush();
                        push.setChannel(ParseUser.getCurrentUser().getUsername());
                        push.setData(data);
                        push.sendInBackground();
                        pushtituloa.setText("");
                        pushtestua.setText("");
                        pushurl.setText("");
                        finish();
                    }
                }
            }
        });

        pushlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                finish();
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
    }
}