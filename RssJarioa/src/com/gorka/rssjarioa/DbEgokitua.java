package com.gorka.rssjarioa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DbEgokitua {
    
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    /*
	 * TABLE principal_elkartea
	 */
	static final String TAULA_elkartea= "principal_elkartea";
    static final String AUT_ID = "id";
	static final String AUT_NOR = "nor";
	static final String AUT_EMAIL = "email";
	static final String AUT_WEBGUNEA = "webgunea";
    static final String AUT_CREATED = "created_at";
    static final String AUT_UPDATED = "updated_at";
    static final String AUT_DESKRIBAPENA = "deskribapena";
    static final String AUT_IKONOA = "ikonoa";
    static final String AUT_GOIBURUAK = "goiburuak";
	/*
	 * TABLE principal_ekintza
	 */
    static final String TAULA_ekintza= "principal_ekintza";
    static final String KEY_TITULOA = "tituloa";
    static final String KEY_LEKUA = "lekua";
	static final String KEY_CREATED ="created_at";
    static final String KEY_UPDATED ="updated_at";
	static final String KEY_EGUNE="egune";
    static final String KEY_AMAIERA ="amaiera";
    static final String KEY_DESKRIBAPENA="deskribapena";
    static final String KEY_LINK ="link";
    static final String KEY_KARTELA ="kartela";
	static final String KEY_JAKINARAZPENA1="jakinarazpena_1";
	/*
	 * TABLE principal_ekintza_sortzailea
	 */
	static final String TAULA_ekintza_sortzailea= "principal_ekintza_sortzailea";
    static final String SOR_EKINTZA="ekintza_id";
    static final String SOR_elkartea="elkartea_id";
    /*
     * TABLE blog_link
     */
    static final String TAULA_blog_links = "blog_links";
    static final String LINK_BLOG = "blog";
    static final String LINK_TITULOA = "tituloa";
    static final String LINK_LINK = "link";
    static final String LINK_PUB_DATE = "blog_pub_date";


	static final int DB_BERTSIOA = 16;
    static final String DB_IZENA = "NireDB";
    
    
    static final String DB_TAULA_elkartea = "CREATE TABLE principal_elkartea ("+
						    	    "id integer NOT NULL PRIMARY KEY,"+
						    	    "nor varchar(30) NOT NULL,"+
						    	    "email varchar(75) NOT NULL,"+
						    	    "webgunea varchar(200) NOT NULL," +
                                    "created_at datetime NOT NULL," +
                                    "updated_at datetime NOT NULL," +
                                    "deskribapena varchar(600) NOT NULL," +
                                    "ikonoa varchar(100) NOT NULL," +
                                    "goiburuak varchar(100) NOT NULL);";

    static final String DB_TAULA_ekintza ="CREATE TABLE principal_ekintza ("+
									"id integer NOT NULL PRIMARY KEY,"+
									"tituloa varchar(100) NOT NULL,"+
                                    "lekua varchar(100) NOT NULL,"+
									"egune datetime NOT NULL,"+
                                    "amaiera datetime NOT NULL,"+
									"deskribapena varchar(600) NOT NULL,"+
                                    "link varchar(200)," +
                                    "kartela varchar(100),"+
									"created_at datetime NOT NULL,"+
                                    "updated_at datetime NOT NULL,"+
									"jakinarazpena_1 bool NOT NULL);";

    static final String DB_TAULA_ekintza_sortzailea = "CREATE TABLE principal_ekintza_sortzailea ("+
						    	    "id integer NOT NULL PRIMARY KEY,"+
						    	    "ekintza_id integer NOT NULL,"+
						    	    "elkartea_id integer NOT NULL REFERENCES principal_elkartea (id),"+
						    	    "UNIQUE (ekintza_id, elkartea_id)); ";
						    	    
						    	    
	static final String DB_TAULA_blog_links = "CREATE TABLE blog_links (" +
                                    "id integer NOT NULL PRIMARY KEY," +
                                    "blog varchar(100) NOT NULL," +
                                    "tituloa varchar(100) NOT NULL," +
                                    "link varchar(200) NOT NULL," +
                                    "blog_pub_date datetime NOT NULL);";

    public  DbEgokitua(Context ctx)
    {
            this.context = ctx;
            DBHelper = new DatabaseHelper(context);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper
    {
            DatabaseHelper(Context context)
            {
                super(context, DB_IZENA, null, DB_BERTSIOA);
            }
        
            @Override
            public void onCreate(SQLiteDatabase db)
            {
                try {
                    db.execSQL(DB_TAULA_elkartea);
                    db.execSQL(DB_TAULA_ekintza);
                    db.execSQL(DB_TAULA_ekintza_sortzailea);
                    db.execSQL(DB_TAULA_blog_links);
                    try{
                       Cursor cursor = db.rawQuery("PRAGMA journal_mode = OFF;", null);
                        cursor.close();
                    }catch (Exception e){
                        Log.e("databases",e.toString());
                    }
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitNetwork().build();
                    StrictMode.setThreadPolicy(policy);
                    /**
                     * db elkartea internetetik sortu
                     *
                     */
                    JSONArray jarrayElkarteak = elkarteakSortu();
                    for (int i = 0; i < jarrayElkarteak.length(); i++) {
                        try {
                            JSONObject c = jarrayElkarteak.getJSONObject(i);
                            int pk = Integer.parseInt(c.getString("pk"));
                            String info = c.getString("fields");
                            JSONObject fields = new JSONObject(info);
                            String nor = fields.getString(AUT_NOR);
                            String email = fields.getString(AUT_EMAIL);
                            String webgunea = fields.getString(AUT_WEBGUNEA);
                            String created = fields.getString(AUT_CREATED);
                            String updated = fields.getString(AUT_UPDATED);
                            String deskribapena = fields.getString(AUT_DESKRIBAPENA);
                            String ikonoa = fields.getString(AUT_IKONOA);
                            String goiburua = fields.getString(AUT_GOIBURUAK);

                            try {
                                ContentValues initialValues = new ContentValues();
                                initialValues.put(AUT_ID, pk);
                                initialValues.put(AUT_NOR, nor);
                                initialValues.put(AUT_EMAIL, email);
                                initialValues.put(AUT_WEBGUNEA, webgunea);
                                initialValues.put(AUT_CREATED, created);
                                initialValues.put(AUT_UPDATED, updated);
                                initialValues.put(AUT_DESKRIBAPENA, deskribapena);
                                initialValues.put(AUT_IKONOA, ikonoa);
                                initialValues.put(AUT_GOIBURUAK, goiburua);

                                long id = db.insert(TAULA_elkartea, null, initialValues);
                                if (id == -1) {
                                    Log.d(nor, "Ez da gehitu elkartea");
                                } else {
                                    Log.d(nor, "+elkartea");
                                }
                            } catch (Exception e) {
                                Log.e("elkarteasortu",e.toString());
                            }
                        }
                        catch (JSONException e) {
                            Log.e("Elkarteak",e.toString());
                        }
                    }
                    /**
                     * db ekintza internetetik sortu
                     *
                     */
                    JSONArray jarrayEkintzak = ekintzakSortu();
                    for (int i = 0; i < jarrayEkintzak.length(); i++) {
                        try {
                            JSONObject c = jarrayEkintzak.getJSONObject(i);
                            int pk = Integer.parseInt(c.getString("pk"));
                            String info = c.getString("fields");
                            JSONObject fields = new JSONObject(info);
                            String tituloa = fields.getString(KEY_TITULOA);
                            String lekua = fields.getString(KEY_LEKUA);
                            String egune = fields.getString(KEY_EGUNE);
                            String amaiera = fields.getString(KEY_AMAIERA);
                            String deskribapena = fields.getString(KEY_DESKRIBAPENA);
                            String link = fields.getString(KEY_LINK);
                            String kartela = fields.getString(KEY_KARTELA);
                            String created_at = fields.getString(KEY_CREATED);
                            String updated_at = fields.getString(KEY_UPDATED);

                            Log.e("elkartea =>", pk+" "+tituloa);

                            try {
                                ContentValues initialValues = new ContentValues();
                                initialValues.put("id", pk);
                                initialValues.put(KEY_EGUNE, egune);
                                initialValues.put(KEY_AMAIERA, amaiera);
                                initialValues.put(KEY_LEKUA,lekua);
                                initialValues.put(KEY_TITULOA, tituloa);
                                initialValues.put(KEY_LINK,link);
                                initialValues.put(KEY_KARTELA,"http://larrabetzu.net/media/"+kartela);
                                initialValues.put(KEY_CREATED, created_at);
                                initialValues.put(KEY_UPDATED, updated_at);
                                initialValues.put(KEY_DESKRIBAPENA, deskribapena);
                                initialValues.put(KEY_JAKINARAZPENA1, false);
                                long id = db.insert(TAULA_ekintza, null, initialValues);
                                if (id == -1){
                                    Log.d(tituloa, "Ez da ekintzarik gehitu");
                                }else{
                                    JSONArray sortzaileak = fields.getJSONArray("sortzailea");
                                    Log.e("sortzaileak", sortzaileak+" ");
                                    for (int s = 0; s < sortzaileak.length(); s++) {
                                        int sortzaileaID = sortzaileak.getInt(s);
                                        Log.e("sortzailea ID", sortzaileaID+"");
                                        Log.d(tituloa, "+ ekintza");
                                        ContentValues initialValuesSortzailea = new ContentValues();
                                        initialValuesSortzailea.put(SOR_elkartea, sortzaileaID);
                                        initialValuesSortzailea.put(SOR_EKINTZA, id);
                                        long idsor = db.insert(TAULA_ekintza_sortzailea,null,initialValuesSortzailea);
                                        if (idsor == -1) {
                                            Log.e(""+sortzaileaID, "Ez da sortzailea gehitu");
                                        }else {
                                            Log.i(""+sortzaileaID,"+ sortzailea");
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("elkarteasortu",e.toString());
                            }
                        }catch (JSONException e) {
                            Log.e("Elkarteak",e.toString());
                        }
                    }
                } catch (SQLException e) {
                    Log.e("sql", "Taula ez da sortu ");
                    Log.e("taula",e.toString());
                }
            }
        
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
            {
                Log.w("DbEgokitua", "Datu-basearen bertsioa eguneratzen " + oldVersion + " tik " + newVersion +"-ra" +", datu zahar guztiak kenduko dira");
                db.execSQL("DROP TABLE IF EXISTS principal_elkartea");
                db.execSQL("DROP TABLE IF EXISTS principal_ekintza_sortzailea");
                db.execSQL("DROP TABLE IF EXISTS principal_ekintza");
                db.execSQL("DROP TABLE IF EXISTS blog_links");
                onCreate(db);

            }
    }

    public  DbEgokitua zabaldu() throws SQLException
    {
        try{
            db = DBHelper.getWritableDatabase();
            if (db != null) {
                Cursor cursor = db.rawQuery("PRAGMA journal_mode = OFF;", null);
                cursor.close();
            }
        }catch (Exception ex){
            Log.e("zabaldu",ex.toString());
        }
        return this;
    }

    public void zarratu()
    {
        try{
            DBHelper.close();
        }catch (Exception e){
            Log.e("DbEgokitua",e.toString());
        }
    }

    public void eguneratuEkintzak() {
        JSONArray jarrayEkintzak = ekintzakSortu();
        eguneratuEkintzak(jarrayEkintzak);
        ekintzaBarriakSartu(jarrayEkintzak);

    }

    public void eguneratuElkarteak(){
        JSONArray jarrayElkarteak = elkarteakSortu();
        eguneratuElkarteak(jarrayElkarteak);
        elkarteBarriakSartu(jarrayElkarteak);
    }

    private static JSONArray elkarteakSortu(){

        String json = getJson("http://larrabetzu.net/wsElkarteak/");
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return null;
    }

    private static JSONArray ekintzakSortu(){

        String json = getJson("http://larrabetzu.net/wsEkintza/");
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        return null;

    }

    private static String getJson(String url) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e("Elkarteak", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private void ekintzaBarriakSartu(JSONArray jarrayEkintzak){
        int idMax = maxId(TAULA_ekintza);
        for (int i = 0; i < jarrayEkintzak.length(); i++) {
            try {
                JSONObject c = jarrayEkintzak.getJSONObject(i);
                int pk = Integer.parseInt(c.getString("pk"));
                String info = c.getString("fields");
                JSONObject fields = new JSONObject(info);

                String tituloa = fields.getString(KEY_TITULOA);
                String lekua = fields.getString(KEY_LEKUA);
                String egune = fields.getString(KEY_EGUNE);
                String amaiera = fields.getString(KEY_AMAIERA);
                String deskribapena = fields.getString(KEY_DESKRIBAPENA);
                String link = fields.getString(KEY_LINK);
                String kartela = fields.getString(KEY_KARTELA);
                String created_at = fields.getString(KEY_CREATED);
                String updated_at = fields.getString(KEY_UPDATED);

                if (idMax < pk) {
                    try {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put("id", pk);
                        initialValues.put(KEY_EGUNE, egune);
                        initialValues.put(KEY_AMAIERA, amaiera);
                        initialValues.put(KEY_LEKUA, lekua);
                        initialValues.put(KEY_TITULOA, tituloa);
                        initialValues.put(KEY_LINK, link);
                        initialValues.put(KEY_KARTELA, "http://larrabetzu.net/media/" + kartela);
                        initialValues.put(KEY_CREATED, created_at);
                        initialValues.put(KEY_UPDATED, updated_at);
                        initialValues.put(KEY_DESKRIBAPENA, deskribapena);
                        initialValues.put(KEY_JAKINARAZPENA1, false);
                        long id = db.insert(TAULA_ekintza, null, initialValues);
                        if (id == -1) {
                            Log.d(tituloa, "Ez da ekintzarik gehitu");
                        } else {
                            JSONArray sortzaileak = fields.getJSONArray("sortzailea");
                            Log.e("sortzaileak", sortzaileak + " ");
                            for (int s = 0; s < sortzaileak.length(); s++) {
                                int sortzaileaID = sortzaileak.getInt(s);
                                Log.e("sortzailea ID", sortzaileaID + "");
                                Log.d(tituloa, "+ ekintza");
                                ContentValues initialValuesSortzailea = new ContentValues();
                                initialValuesSortzailea.put(SOR_elkartea, sortzaileaID);
                                initialValuesSortzailea.put(SOR_EKINTZA, id);
                                long idsor = db.insert(TAULA_ekintza_sortzailea, null, initialValuesSortzailea);
                                if (idsor == -1) {
                                    Log.e("" + sortzaileaID, "Ez da sortzailea gehitu");
                                } else {
                                    Log.i("" + sortzaileaID, "+ sortzailea");
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("elkarteasortu", e.toString());
                    }
                }
            } catch (JSONException e) {
                Log.e("Elkarteak", e.toString());
            } catch (Exception e) {
                Log.e("Eguneratu", e.toString());
            }
        }
    }

    private void eguneratuEkintzak(JSONArray jarrayEkintzak){
        for (int i = 0; i < jarrayEkintzak.length(); i++) {
            try {
                JSONObject c = jarrayEkintzak.getJSONObject(i);
                int pk = Integer.parseInt(c.getString("pk"));
                String info = c.getString("fields");
                JSONObject fields = new JSONObject(info);

                String tituloa = fields.getString(KEY_TITULOA);
                String lekua = fields.getString(KEY_LEKUA);
                String egune = fields.getString(KEY_EGUNE);
                String amaiera = fields.getString(KEY_AMAIERA);
                String deskribapena = fields.getString(KEY_DESKRIBAPENA);
                String link = fields.getString(KEY_LINK);
                String kartela = fields.getString(KEY_KARTELA);
                String created_at = fields.getString(KEY_CREATED);
                String updated_at = fields.getString(KEY_UPDATED);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                java.util.Date dbAzkenUpdated = null;
                java.util.Date ekitaldiaUpdated = null;

                String query = "SELECT updated_at FROM " + TAULA_ekintza +" WHERE id ="+pk;
                Cursor cursor = db.rawQuery(query, null);
                String updated_at_db = null;
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            updated_at_db = cursor.getString(0);
                            Log.i("update", updated_at_db);

                        } catch (Exception e) {
                            Log.e("DbE-azken_pub_date", e.toString());
                            updated_at_db = "2015-02-25T16:44:56.441";
                        }
                    } while (cursor.moveToNext());
                }
                Log.e("azken_pub_date-DbE", updated_at);

                try {
                    dbAzkenUpdated = sdf.parse(updated_at_db, new ParsePosition(0));
                } catch (Exception e) {
                    Log.e("String to date-DbE", e.toString());
                }

                try {
                    ekitaldiaUpdated = sdf.parse(updated_at, new ParsePosition(0));
                } catch (Exception e) {
                    Log.e("Parse date", e.toString());
                }
                if (dbAzkenUpdated.before(ekitaldiaUpdated)) {
                    try {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put(KEY_EGUNE, egune);
                        initialValues.put(KEY_AMAIERA, amaiera);
                        initialValues.put(KEY_LEKUA, lekua);
                        initialValues.put(KEY_TITULOA, tituloa);
                        initialValues.put(KEY_LINK, link);
                        initialValues.put(KEY_KARTELA, "http://larrabetzu.net/media/" + kartela);
                        initialValues.put(KEY_CREATED, created_at);
                        initialValues.put(KEY_UPDATED, updated_at);
                        initialValues.put(KEY_DESKRIBAPENA, deskribapena);
                        initialValues.put(KEY_JAKINARAZPENA1, false);

                        String[] whereArgs = {pk+""};
                        String selection = "id = ?";
                        int id = db.update(TAULA_ekintza,initialValues, selection, whereArgs);
                        if (id > 0) {
                            Log.d(tituloa, "ekintzaEguneratuDa");
                        }
                    } catch (Exception e) {
                        Log.e("elkarteasortu", e.toString());
                    }
                }
            } catch (JSONException e) {
                Log.e("Elkarteak", e.toString());
            } catch (Exception e) {
                Log.e("Eguneratu", e.toString());
            }
        }
    }


    private void elkarteBarriakSartu(JSONArray jarrayElkarteak){
        int idMax = maxId(TAULA_elkartea);
        for (int i = 0; i < jarrayElkarteak.length(); i++) {
            try {
                JSONObject c = jarrayElkarteak.getJSONObject(i);
                int pk = Integer.parseInt(c.getString("pk"));
                String info = c.getString("fields");
                JSONObject fields = new JSONObject(info);
                String nor = fields.getString(AUT_NOR);
                String email = fields.getString(AUT_EMAIL);
                String webgunea = fields.getString(AUT_WEBGUNEA);
                String created = fields.getString(AUT_CREATED);
                String updated = fields.getString(AUT_UPDATED);
                String deskribapena = fields.getString(AUT_DESKRIBAPENA);
                String ikonoa = fields.getString(AUT_IKONOA);
                String goiburua = fields.getString(AUT_GOIBURUAK);


                if(idMax < pk){
                    try {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put(AUT_ID, pk);
                        initialValues.put(AUT_NOR, nor);
                        initialValues.put(AUT_EMAIL, email);
                        initialValues.put(AUT_WEBGUNEA, webgunea);
                        initialValues.put(AUT_CREATED, created);
                        initialValues.put(AUT_UPDATED, updated);
                        initialValues.put(AUT_DESKRIBAPENA, deskribapena);
                        initialValues.put(AUT_IKONOA, ikonoa);
                        initialValues.put(AUT_GOIBURUAK, goiburua);

                        long id = db.insert(TAULA_elkartea, null, initialValues);
                        if (id == -1) {
                            Log.d(nor, "Ez da gehitu elkartea");
                        } else {
                            Log.d(nor, "+elkartea");
                        }
                    } catch (Exception e) {
                        Log.e("elkarteasortu",e.toString());
                    }
                }
            }
            catch (JSONException e) {
                Log.e("Elkarteak",e.toString());
            }
        }
    }

    private void eguneratuElkarteak(JSONArray jarrayElkarteak){
        for (int i = 0; i < jarrayElkarteak.length(); i++) {
            try {
                JSONObject c = jarrayElkarteak.getJSONObject(i);
                int pk = Integer.parseInt(c.getString("pk"));
                String info = c.getString("fields");
                JSONObject fields = new JSONObject(info);

                String nor = fields.getString(AUT_NOR);
                String email = fields.getString(AUT_EMAIL);
                String webgunea = fields.getString(AUT_WEBGUNEA);
                String created = fields.getString(AUT_CREATED);
                String updated = fields.getString(AUT_UPDATED);
                String deskribapena = fields.getString(AUT_DESKRIBAPENA);
                String ikonoa = fields.getString(AUT_IKONOA);
                String goiburua = fields.getString(AUT_GOIBURUAK);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                java.util.Date dbAzkenUpdated = null;
                java.util.Date elkarteaUpdated = null;

                String query = "SELECT updated_at FROM " + TAULA_elkartea +" WHERE id ="+pk;
                Cursor cursor = db.rawQuery(query, null);
                String updated_at_db = null;
                if (cursor.moveToFirst()) {
                    do {
                        try {
                            updated_at_db = cursor.getString(0);
                            Log.i("update", updated_at_db);

                        } catch (Exception e) {
                            Log.e("DbE-azken_pub_date", e.toString());
                            updated_at_db = "2015-02-25T16:44:56.441";
                        }
                    } while (cursor.moveToNext());
                }

                try {
                    dbAzkenUpdated = sdf.parse(updated_at_db, new ParsePosition(0));
                } catch (Exception e) {
                    Log.e("String to date-DbE", e.toString());
                }

                try {
                    elkarteaUpdated = sdf.parse(updated, new ParsePosition(0));
                } catch (Exception e) {
                    Log.e("Parse date", e.toString());
                }
                if (dbAzkenUpdated.before(elkarteaUpdated)) {
                    try {
                        ContentValues initialValues = new ContentValues();
                        initialValues.put(AUT_NOR, nor);
                        initialValues.put(AUT_EMAIL, email);
                        initialValues.put(AUT_WEBGUNEA, webgunea);
                        initialValues.put(AUT_CREATED, created);
                        initialValues.put(AUT_UPDATED, updated);
                        initialValues.put(AUT_DESKRIBAPENA, deskribapena);
                        initialValues.put(AUT_IKONOA, ikonoa);
                        initialValues.put(AUT_GOIBURUAK, goiburua);

                        String[] whereArgs = {pk+""};
                        String selection = "id = ?";
                        int id = db.update(TAULA_elkartea,initialValues, selection,whereArgs);
                        if (id > 0) {
                            Log.d(nor, "ondo eguneratu da");
                        }
                    } catch (Exception e) {
                        Log.e("elkarteaEguneratu", e.toString());
                    }
                }
            } catch (JSONException e) {
                Log.e("Elkarteak", e.toString());
            } catch (Exception e) {
                Log.e("Eguneratu", e.toString());
            }
        }
    }


    private int maxId(String taula){
        String query = "SELECT MAX(id) AS max_id FROM " + taula;
        Cursor cursor = db.rawQuery(query, null);
        int idMax = 0;
        if (cursor.moveToFirst()) {
            do {
                try {
                    idMax = Integer.parseInt(cursor.getString(0));
                    Log.i("max (id)", idMax+"");

                } catch (Exception e) {
                    Log.e("DbE-max id", e.toString());
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return idMax;
    }

    public Cursor elkarteaLortuDanak()
    {
        String query = "SELECT nor,email,webgunea FROM "+TAULA_elkartea+" order by nor";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor elkarteaLortuId(int ekintza_id) throws SQLException
    {
        String query = "SELECT elkartea_id FROM "+TAULA_ekintza_sortzailea+" WHERE ekintza_id = '"+ekintza_id+"'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor elkarteaLortu(int id)
    {
        String query = "SELECT nor,email,webgunea FROM "+TAULA_elkartea+" WHERE id = '"+id+"'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor ekitaldiakid(int urtea,int hilabeta,int egune)
    {
            String query = "SELECT id FROM "+TAULA_ekintza+" WHERE egune <= '"+urtea+"-"+hilabeta+"-"+egune+"' order by egune";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
    }

    public boolean garbitu()
    {
        final Calendar calendar = Calendar.getInstance();
        int urtea = calendar.get(Calendar.YEAR);
        String hilabetea = String.format("%02d",calendar.get(Calendar.MONTH)+1);   //urtarrila=0
        String egune= String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH));
        String ordue = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));

        boolean garbiketa;
        try {
            String query = "SELECT id FROM "+TAULA_ekintza+" WHERE amaiera <='"+urtea+"-"+hilabetea+"-"+egune+"T"+ordue+":00.000 'order by egune";
            Cursor c = db.rawQuery(query, null);
            int id;
            if (c != null) {
                c.moveToFirst();
                do{
                    id=c.getInt(0);
                    Log.e("id garbiketa",""+id);
                    try {
                        db.execSQL("DELETE FROM "+TAULA_ekintza_sortzailea+" WHERE ekintza_id ='"+id+"'");
                        db.execSQL("DELETE FROM "+TAULA_ekintza+" WHERE id ='"+id+"'");

                    }catch (Exception e){
                        Log.e("garbitu-DBEgokitua",e.toString());
                    }
                }while (c.moveToNext());
            }
            garbiketa = true;
        }catch (CursorIndexOutOfBoundsException e){
            Log.i("DbEgokitua-garbitu","ez dago ezer garbitako");
            garbiketa = false;
        }catch (Exception e){
            Log.e("DbEgokitua-garbitu",e.toString());
            garbiketa = false;
        }
        return garbiketa;
    }


    public Cursor ekitaldiaLortu(int id) throws SQLException
    {
            String query = "SELECT tituloa,egune,lekua,deskribapena FROM "+TAULA_ekintza+" WHERE id = '"+id+"'";
            Cursor c = db.rawQuery(query, null);
            if (c != null) {
                c.moveToFirst();
            }
            return c;
    }
    public Cursor ekitaldiaLortuDana(int id) throws SQLException
    {
        String query = "SELECT tituloa,egune,lekua,deskribapena,link,kartela FROM "+TAULA_ekintza+" WHERE id = '"+id+"'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    public boolean ekitaldiaAlarmaLortu(int id) throws SQLException
    {
        boolean aktibatuta = false;
        String query = "SELECT jakinarazpena_1 FROM "+TAULA_ekintza+" WHERE id = '"+id+"'";
        Cursor c = db.rawQuery(query, null);
        if (c.moveToFirst()) {
            do {
                aktibatuta = c.getInt(0)>0;
            } while(c.moveToNext());
        } return aktibatuta;
    }
    public void ekitaldiaAlarmaAktualizatu(int id) throws SQLException
    {
        db.execSQL("UPDATE "+TAULA_ekintza+" SET jakinarazpena_1 = '1' WHERE id = '"+id+"'");

    }
    public String blogazkendata(String blog)
    {
            final Calendar ca = Calendar.getInstance();
            int mYear = ca.get(Calendar.YEAR);
            int mMonth = ca.get(Calendar.MONTH)+1;   //urtarrila=0
            int mDay = ca.get(Calendar.DAY_OF_MONTH);
            int mhour = ca.get(Calendar.HOUR_OF_DAY);
            if(mMonth==1){
                mYear = mYear-1;
                mMonth = 12;
            }else{
                mMonth = mMonth-1;//bat kenduko dotzet pasadan hilabeteko data lortzeko
            }
            String data = null;
            String oraindelahilebat = mYear+"-"+mMonth+"-"+mDay+" "+mhour+":00:00";//yyyy-MM-dd HH:mm:ss

            try {
                String query = "SELECT MAX(blog_pub_date) FROM blog_links WHERE blog ='"+blog+"'";
                Cursor c = db.rawQuery(query, null);
                if (c.moveToFirst()) {
                    do {
                        data = c.getString(0);
                    } while(c.moveToNext());
                }
                if(data==null){
                    data = oraindelahilebat;
                }
            }catch (Exception ex){
                Log.e("blogakendata-dbEgokitua"+blog,ex.toString());
            }
            return data;
    }
    public void linkjarri(String blog,String tituloa,String link,String date)
    {
            String w = "";
            try{
                DateFormat dffrom = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
                DateFormat dfto = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date day = dffrom.parse(date);
                w = dfto.format(day);
            }catch (Exception e){
                Log.e("DbEgokitua-data",e.toString());
            }

            ContentValues initialValues = new ContentValues();
            initialValues.put(LINK_BLOG, blog);
            initialValues.put(LINK_TITULOA, tituloa);
            initialValues.put(LINK_LINK, link);
            initialValues.put(LINK_PUB_DATE,w);
            long id =db.insert(TAULA_blog_links, null, initialValues);
            if (id==-1) {
                Log.e("link-dbEgokitua", "Ez da gehitu linka");
            }else {
                Log.d("link-dbEgokitua", "+link");
            }
    }
    public Cursor linklortu ()
    {
            String query = "SELECT blog,tituloa,link FROM "+TAULA_blog_links+" order by blog_pub_date DESC";
            Cursor c = db.rawQuery(query, null);
            if (c != null) {
                c.moveToFirst();
            }
            return c;
    }
    public void linkkendu(String blog,int post)
    {
            String query = "SELECT id FROM "+TAULA_blog_links+" WHERE blog ='"+blog+"' order by blog_pub_date ASC";
            Cursor c = db.rawQuery(query, null);
            int id;
            if (c != null) {
                c.moveToFirst();
                if(c.getCount()>post){
                    id=c.getInt(0);
                    Log.e("id linkkendu",""+id);
                    try {
                        db.execSQL("DELETE FROM "+TAULA_blog_links+" WHERE id ='"+id+"'");
                    }catch (Exception e){
                        Log.e("linkkendu-DBEgokitua",e.toString());
                    }
                }
            }
    }
    public void linkgarbitu(String blog)
    {
        try {
            db.execSQL("DELETE FROM "+TAULA_blog_links+" WHERE blog NOT IN ("+blog+")");
        }catch (Exception e){
            Log.e("linkgarbitu-DBEgokitua",e.toString());
        }
    }

}