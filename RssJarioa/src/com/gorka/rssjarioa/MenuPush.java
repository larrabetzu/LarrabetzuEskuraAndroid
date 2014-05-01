package com.gorka.rssjarioa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class MenuPush extends Activity {

    int zenbatPushNumeroa ;
    int pushNumeroaAktualizatuta;
    long denboraTot;
    ParseObject parseObject;
    boolean ping;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_push);

        final TextView pushNumeroa = (TextView) findViewById(R.id.layout_menupush_pushnumeroa);
        final EditText pushTituloa = (EditText) findViewById(R.id.layout_menupush_tituloa);
        final EditText pushTestua = (EditText) findViewById(R.id.layout_menupush_testua);
        final CheckBox url_rik = (CheckBox) findViewById(R.id.layout_menupush_urlcheckbox);
        final EditText pushUrl =(EditText) findViewById(R.id.layout_menupush_url);
        final ImageView imageVerified = (ImageView) findViewById(R.id.layout_menupush_imageview);
        final Spinner spinnerDataTartea =(Spinner) findViewById(R.id.layout_menupush_data);
        final Button pushLogout = (Button) findViewById(R.id.layout_menupush_logout);
        final Button pushOk = (Button) findViewById(R.id.layout_menupush_ok);

        final String user = ParseUser.getCurrentUser().getUsername();
        final Calendar c = Calendar.getInstance();
        final int mWeek = c.get(Calendar.WEEK_OF_YEAR);

        final String orduak[] ={"ordu 1", "2 ordu", "4 ordu", "6 ordu", "10 ordu", "Egun bat", "2 Egun"};
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,orduak);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDataTartea.setAdapter(dataAdapter);
        spinnerDataTartea.setSelection(6);

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
                    pushNumeroa.setText("" + zenbatPushNumeroa);

                    if (mWeek != pushNumeroaAktualizatuta) {
                        parseObject.put("numeroa", 3);
                        parseObject.put("aktualizatua", mWeek);
                        pushNumeroa.setText("" + 3);
                        parseObject.saveInBackground();

                    }
                } else {
                    Log.e("score", "Error: " + e.getMessage());
                    showToast(MenuPush.this, "Beranduago saiatu");
                }
            }
        });


        url_rik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url_rik.isChecked()) {
                    pushUrl.setVisibility(View.VISIBLE);
                } else {
                    pushUrl.setVisibility(View.GONE);
                }
            }
        });

        spinnerDataTartea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, android.view.View v, int position, long id) {
                long currentTime = System.currentTimeMillis();
                switch (position) {//"ordu 1", "2 ordu", "4 ordu", "6 ordu", "10 ordu", "Egun bat", "2 Egun"
                    case 0:
                        denboraTot = (currentTime / 1000) + (60 * 60 * 1);
                        break;
                    case 1:
                        denboraTot = (currentTime / 1000) + (60 * 60 * 2);
                        break;
                    case 2:
                        denboraTot = (currentTime / 1000) + (60 * 60 * 4);
                        break;
                    case 3:
                        denboraTot = (currentTime / 1000) + (60 * 60 * 6);
                        break;
                    case 4:
                        denboraTot = (currentTime / 1000) + (60 * 60 * 10);
                        break;
                    case 5:
                        denboraTot = (currentTime / 1000) + (60 * 60 * 24);
                        break;
                    case 6:
                        denboraTot = (currentTime / 1000) + (60 * 60 * 48);
                        break;
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        pushOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zenbatPushNumeroa >= 1) {
                    if (pushTituloa.getText().toString().isEmpty() || pushTestua.getText().toString().isEmpty()) {
                        showToast(MenuPush.this, "Tituloa eta testua beteak egon behar dira");
                    } else {
                        String urlText = pushUrl.getText().toString();
                        ping = ping(urlText);
                        Log.e("ping", " "+ping);
                        if(ping){
                            imageVerified.setImageDrawable(getResources().getDrawable(R.drawable.accept));
                        }else {
                            imageVerified.setImageDrawable(getResources().getDrawable(R.drawable.cancel));
                        }
                        imageVerified.setVisibility(View.VISIBLE);

                        if (url_rik.isChecked() && (urlText.isEmpty() || !ping)) {
                            showToast(MenuPush.this, "URL-a konprobatu");
                        } else {
                            if(!url_rik.isChecked()){
                                pushUrl.setText("");
                                urlText="";
                            }
                            JSONObject data = null;
                            try {
                                data = new JSONObject("{ \"action\": \"com.gorka.rssjarioa.UPDATE_STATUS\"," +
                                        " \"tit\": \"" + pushTituloa.getText().toString() + "\", " +
                                        "\"tex\": \"" + pushTestua.getText().toString() + "\", " +
                                        "\"url\": \"" + urlText + "\" }");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Tracker myTracker = EasyTracker.getInstance(MenuPush.this);
                                myTracker.send(MapBuilder.createException(new StandardExceptionParser(MenuPush.this, null)
                                        .getDescription(Thread.currentThread().getName(), e), false).build());
                            }
                            ParsePush push = new ParsePush();
                            push.setChannel(user);
                            push.setExpirationTime(denboraTot);
                            push.setData(data);
                            push.sendInBackground();
                            pushTituloa.setText("");
                            pushTestua.setText("");
                            pushUrl.setText("");
                            parseObject.increment("numeroa", -1);
                            parseObject.saveInBackground();
                            finish();
                        }
                    }
                } else {
                    showToast(MenuPush.this, "Abisu gustiak amaitu doduz");
                }
            }
        });

        pushLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                finish();
            }
        });
    }
    public static void showToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static boolean ping(String url) {
        url = url.replaceFirst("https", "http");
        try {
            URL urll = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urll.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException exception) {
            Log.e("menu", exception.toString());
            return false;
        }
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