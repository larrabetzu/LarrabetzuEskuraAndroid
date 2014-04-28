package com.gorka.rssjarioa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.google.analytics.tracking.android.Tracker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class MenuPush extends Activity {

    int zenbatPushNumeroa ;
    int pushNumeroaAktualizatuta;
    ParseObject parseObject;

    @Override
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

        final String user = ParseUser.getCurrentUser().getUsername();
        final Calendar c = Calendar.getInstance();
        final int mWeek = c.get(Calendar.WEEK_OF_YEAR);

        ParseQuery<ParseObject> uery = ParseQuery.getQuery("PushNumeroa");
        uery.whereEqualTo("channel", user);
        uery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    parseObject = scoreList.get(0);
                    parseObject.getObjectId();
                    zenbatPushNumeroa = parseObject.getInt("numeroa");
                    pushNumeroaAktualizatuta = parseObject.getInt("aktualizatua");
                    Log.e("zenbatPushNumeroa", "" + zenbatPushNumeroa);
                    pushnumeroa.setText(""+zenbatPushNumeroa);

                    if (mWeek != pushNumeroaAktualizatuta) {
                        parseObject.put("numeroa", 3);
                        parseObject.put("aktualizatua", mWeek);
                        pushnumeroa.setText(""+3);
                        parseObject.saveInBackground();

                    }
                } else {
                    Log.e("score", "Error: " + e.getMessage());
                    showToast(MenuPush.this, "Beranduago saiatu");
                }
            }
        });


        urlrik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (urlrik.isChecked()) {
                    pushurl.setVisibility(View.VISIBLE);
                } else {
                    pushurl.setVisibility(View.GONE);
                }
            }
        });

        pushok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(zenbatPushNumeroa>=1){
                    if (pushtituloa.getText().toString().isEmpty() || pushtestua.getText().toString().isEmpty()) {
                        showToast(MenuPush.this, "Tituloa eta testua beteak egon behar dira");
                    } else {
                        if (urlrik.isChecked() && pushurl.getText().toString().isEmpty()) {
                            showToast(MenuPush.this, "Url-a beteta egon behar da");
                        } else {
                            JSONObject data = null;
                            try {
                                data = new JSONObject("{ \"action\": \"com.gorka.rssjarioa.UPDATE_STATUS\"," +
                                        " \"tit\": \"" + pushtituloa.getText().toString() + "\", " +
                                        "\"tex\": \"" + pushtestua.getText().toString() + "\", " +
                                        "\"url\": \"" + pushurl.getText().toString() + "\" }");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Tracker myTracker = EasyTracker.getInstance(MenuPush.this);
                                myTracker.send(MapBuilder.createException(new StandardExceptionParser(MenuPush.this, null)
                                        .getDescription(Thread.currentThread().getName(), e), false).build());
                            }
                            ParsePush push = new ParsePush();
                            push.setChannel(user);
                            push.setData(data);
                            push.sendInBackground();

                            pushtituloa.setText("");
                            pushtestua.setText("");
                            pushurl.setText("");
                            parseObject.increment("numeroa", -1);
                            parseObject.saveInBackground();
                            finish();
                        }
                    }
                }else {
                    showToast(MenuPush.this, "Abisu gustiak amaitu doduz");
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
    public void showToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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