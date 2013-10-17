package com.gorka.rssjarioa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.StrictMode;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DbEgokitua {
    
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    
    /*
	 * TABLE principal_autor
	 */
	static final String TAULA_autor= "principal_autor";
	static final String AUT_NOR = "nor";
	static final String AUT_EMAIL = "email";
	static final String AUT_WEBGUNEA = "webgunea";
    
	/*
	 * TABLE principal_ekintza
	 */
    static final String TAULA_ekintza= "principal_ekintza";
    static final String KEY_TITULOA = "tituloa";
    static final String KEY_LEKUA = "lekua";
	static final String KEY_PUB_DATE="pub_date"; //publikatutako data
	static final String KEY_EGUNE="egune";		 // ekitaldian egune
	static final String KEY_DESKRIBAPENA="deskribapena";
    static final String KEY_link="link";
    static final String KEY_kartela_link="kartela_link";
	static final String KEY_JAKINARAZPENA1="jakinarazpena_1";
	static final String KEY_JAKINARAZPENA2="jakinarazpena_2";
	/*
	 * TABLE principal_ekintza_sortzailea
	 */
	static final String TAULA_ekintza_sortzailea= "principal_ekintza_sortzailea";
    static final String SOR_EKINTZA="ekintza_id";
    static final String SOR_AUTOR="autor_id";
    /*
     * TABLE blog_link
     */
    static final String TAULA_blog_links = "blog_links";
    static final String LINK_BLOG = "blog";
    static final String LINK_TITULOA = "tituloa";
    static final String LINK_LINK = "link";
    static final String LINK_PUB_DATE = "blog_pub_date";

	static final int DB_BERTSIOA = 2;
    static final String DB_IZENA = "NireDB";
    
    
    static final String DB_TAULA_autor = "CREATE TABLE principal_autor ("+
						    	    "id integer NOT NULL PRIMARY KEY,"+
						    	    "nor varchar(30) NOT NULL,"+
						    	    "email varchar(75) NOT NULL,"+
						    	    "webgunea varchar(200) NOT NULL); ";

    static final String DB_TAULA_ekintza ="CREATE TABLE principal_ekintza ("+
									"id integer NOT NULL PRIMARY KEY,"+
									"tituloa varchar(100) NOT NULL,"+
                                    "lekua varchar(100) NOT NULL,"+
									"egune datetime NOT NULL,"+
									"deskribapena varchar(600) NOT NULL,"+
                                    "link varchar(200)," +
                                    "kartela_link varchar(100),"+
									"pub_date datetime NOT NULL,"+
									"jakinarazpena_1 bool NOT NULL,"+
									"jakinarazpena_2 bool NOT NULL);";

    static final String DB_TAULA_ekintza_sortzailea = "CREATE TABLE principal_ekintza_sortzailea ("+
						    	    "id integer NOT NULL PRIMARY KEY,"+
						    	    "ekintza_id integer NOT NULL,"+
						    	    "autor_id integer NOT NULL REFERENCES principal_autor (id),"+
						    	    "UNIQUE (ekintza_id, autor_id)); ";
						    	    
						    	    
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
                    db.execSQL(DB_TAULA_autor);
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
                     * db autor internetetik aktualizatu
                     *
                     */
                    int numero=0;
                    String webgunea=null;
                    String email=null;
                    String nor;
                    int foo = 0;
                    try {
                        URL url = new URL("http://37.139.15.79/wsAutor/");
                        URLConnection uc = url.openConnection();
                        uc.connect();
                        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                        String inputLine=in.readLine();
                        while (inputLine != null) {
                            String palabra="";
                            for(int x=0;x<inputLine.length();x++){
                                char carac=inputLine.charAt(x);
                                if(carac!='"'){
                                    palabra=palabra+carac;
                                }else{
                                    foo = foo +1;
                                    if(0!=foo%2){
                                        // todas las palabras menos la ultima
                                        switch (numero){
                                            case 1:webgunea=palabra.replace(",", "");           numero=0; break;
                                            case 2:email=palabra.replace(",", "");       numero=0; break;
                                            case 3:nor=palabra.replace("}},", "").replace("{", "");         numero=0;
                                                ContentValues initialValues = new ContentValues();
                                                initialValues.put(AUT_NOR, nor);
                                                initialValues.put(AUT_EMAIL, email);
                                                initialValues.put(AUT_WEBGUNEA, webgunea);
                                                long id =db.insert(TAULA_autor, null, initialValues);
                                                if (id==-1) {
                                                    Log.d(nor, "Ez da gehitu autor");
                                                }else {
                                                    Log.d(nor, "+autor");    }break;
                                        }
                                        if(palabra.contains("webgunea:")){
                                            numero=1;
                                        }
                                        if(palabra.contains("email:")){
                                            numero=2;
                                        }
                                        if(palabra.contains("nor:")){
                                            numero=3;
                                        }
                                        palabra="";
                                    }
                                }
                            }
                            nor=palabra.replace("}}]","");
                            ContentValues initialValues = new ContentValues();
                            initialValues.put(AUT_NOR, nor);
                            initialValues.put(AUT_EMAIL, email);
                            initialValues.put(AUT_WEBGUNEA, webgunea);
                            long id =db.insert(TAULA_autor, null, initialValues);
                            if (id==-1) {
                                Log.d(nor, "Ez da gehitu autor");
                            }else {
                                Log.d(nor, "+autor");
                            }
                            inputLine=in.readLine();
                        }
                        in.close();
                    } catch (Exception e) {
                        Log.e("autorsortu",e.toString());
                    }
                    /**
                     * db ekintza internetetik aktualizatu
                     *
                     */
                   numero=0;
                    String egune = null;
                    String sortzailea;
                    String tituloa = null;
                    String link = null;
                    String kartela_link = null;
                    String deskribapena = null;
                    String lekua = null;
                    String pub_date;
                    foo = 0;
                    String substring = null;
                    try {
                        URL url = new URL("http://37.139.15.79/wsEkintza/");
                        URLConnection uc = url.openConnection();
                        uc.connect();
                        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                        String inputLine=in.readLine();
                        while (inputLine != null) {
                            String palabra="";
                            for(int x=0;x<inputLine.length();x++){
                                char carac=inputLine.charAt(x);
                                if(carac!='"'){

                                    palabra=palabra+carac;
                                }else{
                                    foo = foo +1;
                                    if(0!=foo%2){
                                        // todas las palabras menos la ultima
                                        switch (numero){
                                            case 1:tituloa = palabra.replace("\"", "").replace(",", "");
                                                    numero=0; break;
                                            case 2:deskribapena = palabra.replace("\"", "").replace("}},", "").replace(",","");
                                                    numero=0; break;
                                            case 4:lekua = palabra.replace("[", "").replace("]", "").replace(",", "");
                                                    numero=0; break;
                                            case 5:egune = palabra.replace("\"", "").replace(",", "").replace("T"," ").replace("Z","");
                                                    numero=0; break;
                                            case 6:link = null;
                                                    if(palabra.replace("\"", "").replace(",", "").length()>6){
                                                    link = palabra.replace("\"", "").replace(",", "");}
                                                    numero=0; break;
                                            case 7:kartela_link = null;
                                                    if (palabra.replace("\"", "").replace(",", "").length()>4){
                                                    kartela_link = "http://37.139.15.79/media/"+palabra.replace("\"", "").replace(",", "");
                                                    }
                                                    numero=0; break;
                                            case 8:pub_date = palabra.replace("\"", "").replace(",", "").replace("T", " ").replace("Z","").replace("}}", "").substring(0, 19);
                                                    numero=0;
                                                ContentValues initialValues = new ContentValues();
                                                initialValues.put(KEY_EGUNE, egune);
                                                initialValues.put(KEY_LEKUA,lekua);
                                                initialValues.put(KEY_TITULOA, tituloa);
                                                initialValues.put(KEY_PUB_DATE, pub_date);
                                                initialValues.put(KEY_link,link);
                                                initialValues.put(KEY_kartela_link,kartela_link);
                                                initialValues.put(KEY_DESKRIBAPENA, deskribapena);
                                                initialValues.put(KEY_JAKINARAZPENA1, false);
                                                initialValues.put(KEY_JAKINARAZPENA2, false);
                                                long id =db.insert(TAULA_ekintza, null, initialValues);
                                                Log.i("id",""+id);
                                                if (id==-1) {Log.d(tituloa, "Ez da ekintzarik gehitu");
                                                }else {Log.d(tituloa, "+ ekintza");
                                                    for (int i = 0; i < substring.length(); i++){
                                                        if(substring.charAt(i)!=' '){
                                                            sortzailea = substring.charAt(i)+"";
                                                            ContentValues initialValuesSortzailea = new ContentValues();
                                                            initialValuesSortzailea.put(SOR_AUTOR, sortzailea);
                                                            initialValuesSortzailea.put(SOR_EKINTZA, id);
                                                            long idsor=db.insert(TAULA_ekintza_sortzailea,null,initialValuesSortzailea);
                                                            if (idsor==-1) {Log.d(sortzailea, "Ez da sortzailea gehitu");
                                                            }else {Log.d(sortzailea,"+ sortzailea");}
                                                        }
                                                   }
                                                }
                                                break;
                                        }
                                        if(palabra.contains("tituloa:") ){
                                            numero=1;
                                        }
                                        if(palabra.contains("deskribapena:")){
                                            numero=2;
                                        }
                                        if(palabra.contains("sortzailea:")){
                                            substring = palabra.substring(13).replace(",", "").replace("]", "");
                                        }
                                        if(palabra.contains("lekua:") ){
                                            numero=4;
                                        }
                                        if(palabra.contains("egune:") ){
                                            numero=5;
                                        }
                                        if(palabra.contains("link:") ){
                                            numero=6;
                                        }
                                        if(palabra.contains("kartela:") ){
                                            numero=7;
                                        }
                                        if(palabra.contains("pub_date:")){
                                            numero=8;
                                        }

                                        palabra="";
                                    }
                                }

                            }
                            //ultima palabra antes de terminar
                            pub_date=palabra.replace("}}]", "").replace("\"","").replace("T", " ").replace("Z","").substring(0, 19);
                            ContentValues initialValues = new ContentValues();
                            initialValues.put(KEY_EGUNE, egune);
                            initialValues.put(KEY_LEKUA,lekua);
                            initialValues.put(KEY_TITULOA, tituloa);
                            initialValues.put(KEY_link,link);
                            initialValues.put(KEY_kartela_link,kartela_link);
                            initialValues.put(KEY_PUB_DATE, pub_date);
                            initialValues.put(KEY_DESKRIBAPENA, deskribapena);
                            initialValues.put(KEY_JAKINARAZPENA1, false);
                            initialValues.put(KEY_JAKINARAZPENA2, false);
                            long id =db.insert(TAULA_ekintza, null, initialValues);
                            if (id==-1){
                                Log.d(tituloa, "Ez da ekintzarik gehitu");
                            }else{
                                Log.d(tituloa, "+ ekintza");

                                for (int i = 0; i < substring.length(); i++){
                                    if(substring.charAt(i)!=' '){

                                        sortzailea = substring.charAt(i)+"";
                                        ContentValues initialValuesSortzailea = new ContentValues();
                                        initialValuesSortzailea.put(SOR_AUTOR, sortzailea);
                                        initialValuesSortzailea.put(SOR_EKINTZA, id);
                                        long idsor=db.insert(TAULA_ekintza_sortzailea,null,initialValuesSortzailea);
                                        if (idsor==-1) {Log.d(sortzailea, "Ez da sortzailea gehitu");
                                        }else {Log.d(sortzailea,"+ sortzailea");}
                                    }
                                }
                            }
                            inputLine=in.readLine();//Leemos una nueva inputLine
                        }
                        in.close();
                    } catch (Exception e) {
                        Log.e("ekintzasortu", e.toString());
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
                db.execSQL("DROP TABLE IF EXISTS principal_autor");
                db.execSQL("DROP TABLE IF EXISTS principal_ekintza_sortzailea");
                db.execSQL("DROP TABLE IF EXISTS principal_ekintza");
                db.execSQL("DROP TABLE IF EXISTS blog_links");
                onCreate(db);

            }
    }
    public void eguneratuEkintzak()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date fecha1 = null;
        java.util.Date fecha2 = null;

        String query = "SELECT MAX(pub_date) AS max  FROM "+TAULA_ekintza;
        Cursor cursor = db.rawQuery(query, null);
        String azken_pub_date = null;
        if (cursor.moveToFirst()) {
            do {
                try{
                azken_pub_date = cursor.getString(0);
                if (azken_pub_date == null){
                    azken_pub_date =  "2013-01-21 12:00:00";
                }
                }catch (Exception e){
                    Log.e("DbEgokitua-azken_pub_date",e.toString());
                    azken_pub_date = "2013-01-21 12:00:00";
                }
            } while(cursor.moveToNext());
        }
        Log.e("azken_pub_date-DbEgokitua",azken_pub_date);

        try{
            fecha1 = sdf.parse(azken_pub_date , new ParsePosition(0));
        }catch (Exception e){
            Log.e("String to date-DbEgokitua",e.toString());
        }
        /**
         * db ekintza internetetik aktualizatu
         */
        int numero=0;
        String egune=null;
        String sortzailea;
        String tituloa=null;
        String link = null;
        String kartela_link = null;
        String pub_date;
        String deskribapena=null;
        String lekua=null;
        int foo = 0;
        String substring = null;
        try {
            URL url = new URL("http://37.139.15.79/wsEkintza/");
            URLConnection uc = url.openConnection();
            uc.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine=in.readLine();
            while (inputLine != null) {
                String palabra="";
                for(int x=0;x<inputLine.length();x++){
                    char carac=inputLine.charAt(x);
                    if(carac!='"'){
                        palabra=palabra+carac;
                    }else{
                        foo = foo +1;
                        if(0!=foo%2){
                            // todas las palabras menos la ultima
                            switch (numero){
                                case 1:tituloa = palabra.replace("\"", "").replace(",", "");                        numero=0; break;
                                case 2:deskribapena = palabra.replace("\"", "").replace("}},", "").replace(",",""); numero=0; break;
                                case 4:lekua = palabra.replace("[", "").replace("]", "").replace(",", "");          numero=0; break;
                                case 5:egune = palabra.replace("\"", "").replace(",", "").replace("T"," ").replace("Z",""); numero=0; break;
                                case 6:link = null;
                                        if(palabra.replace("\"", "").replace(",", "").length()>6){
                                        link = palabra.replace("\"", "").replace(",", "");}numero=0; break;
                                case 7:kartela_link = null;
                                       if (palabra.replace("\"", "").replace(",", "").length()>4){
                                       kartela_link = "http://37.139.15.79/media/"+palabra.replace("\"", "").replace(",", "");}numero=0; break;
                                case 8:pub_date = palabra.replace("\"", "").replace(",", "").replace("T", " ").replace("Z","").replace("}}", "").substring(0, 19); numero=0;
                                    try{
                                    fecha2 = sdf.parse(pub_date , new ParsePosition(0));
                                    }catch (Exception e){
                                        Log.e("Parse date",e.toString());
                                    }
                                    if(fecha1.before(fecha2)){
                                        ContentValues initialValues = new ContentValues();
                                        initialValues.put(KEY_EGUNE, egune);
                                        initialValues.put(KEY_LEKUA,lekua);
                                        initialValues.put(KEY_TITULOA, tituloa);
                                        initialValues.put(KEY_link,link);
                                        initialValues.put(KEY_kartela_link,kartela_link);
                                        initialValues.put(KEY_PUB_DATE, pub_date);
                                        initialValues.put(KEY_DESKRIBAPENA, deskribapena);
                                        initialValues.put(KEY_JAKINARAZPENA1, false);
                                        initialValues.put(KEY_JAKINARAZPENA2, false);
                                        long id =db.insert(TAULA_ekintza, null, initialValues);
                                        Log.i("id",""+id);
                                        if (id==-1) {Log.d(tituloa, "Ez da ekintzarik gehitu");
                                        }else {Log.d(tituloa, "+ ekintza");
                                            for (int i = 0; i < substring.length(); i++){
                                                if(substring.charAt(i)!=' '){
                                                    sortzailea = substring.charAt(i)+"";
                                                    ContentValues initialValuesSortzailea = new ContentValues();
                                                    initialValuesSortzailea.put(SOR_AUTOR, sortzailea);
                                                    initialValuesSortzailea.put(SOR_EKINTZA, id);
                                                    long idsor=db.insert(TAULA_ekintza_sortzailea,null,initialValuesSortzailea);
                                                    if (idsor==-1) {Log.d(sortzailea, "Ez da sortzailea gehitu");
                                                    }else {Log.d(sortzailea,"+ sortzailea");}
                                                }
                                            }
                                        }
                                    }
                                    break;
                            }
                            if(palabra.contains("tituloa:") ){
                                numero=1;
                            }
                            if(palabra.contains("deskribapena:")){
                                numero=2;
                            }
                            if(palabra.contains("sortzailea:")){
                                substring = palabra.substring(13).replace(",", "").replace("]", "");
                            }
                            if(palabra.contains("lekua:") ){
                                numero=4;
                            }
                            if(palabra.contains("egune:") ){
                                numero=5;
                            }
                            if(palabra.contains("link:") ){
                                numero=6;
                            }
                            if(palabra.contains("kartela:") ){
                                numero=7;
                            }
                            if(palabra.contains("pub_date:")){
                                numero=8;
                            }
                            palabra="";
                        }
                    }

                }
                //ultima palabra antes de terminar
                pub_date=palabra.replace("}}]", "").replace("\"","").replace("T", " ").replace("Z","").substring(0, 19);
                try{
                    fecha2 = sdf.parse(pub_date , new ParsePosition(0));
                }catch (Exception e){
                    Log.e("Parse date",e.toString());
                }
                if(fecha1.before(fecha2)){
                    ContentValues initialValues = new ContentValues();
                    initialValues.put(KEY_EGUNE, egune);
                    initialValues.put(KEY_LEKUA,lekua);
                    initialValues.put(KEY_TITULOA, tituloa);
                    initialValues.put(KEY_link,link);
                    initialValues.put(KEY_kartela_link,kartela_link);
                    initialValues.put(KEY_PUB_DATE, pub_date);
                    initialValues.put(KEY_DESKRIBAPENA, deskribapena);
                    initialValues.put(KEY_JAKINARAZPENA1, false);
                    initialValues.put(KEY_JAKINARAZPENA2, false);
                    long id =db.insert(TAULA_ekintza, null, initialValues);
                    if (id==-1){
                        Log.d(tituloa, "Ez da ekintzarik gehitu");
                    }else{
                        Log.d(tituloa, "+ ekintza");

                        for (int i = 0; i < substring.length(); i++){
                            if(substring.charAt(i)!=' '){

                                sortzailea = substring.charAt(i)+"";
                                ContentValues initialValuesSortzailea = new ContentValues();
                                initialValuesSortzailea.put(SOR_AUTOR, sortzailea);
                                initialValuesSortzailea.put(SOR_EKINTZA, id);
                                long idsor=db.insert(TAULA_ekintza_sortzailea,null,initialValuesSortzailea);
                                if (idsor==-1) {Log.d(sortzailea, "Ez da sortzailea gehitu");
                                }else {Log.d(sortzailea,"+ sortzailea");}
                            }
                        }
                    }
                }
                inputLine=in.readLine();
            }
            in.close();
        } catch (Exception e) {
            Log.e("ekintzasortu", e.toString());
        }
    }


    public  DbEgokitua zabaldu() throws SQLException 
    {
            db = DBHelper.getWritableDatabase();
            try{

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
            DBHelper.close();
    }

	public int azkenId() 
	{ 
            String query = "SELECT MAX(id) AS max_id FROM "+TAULA_ekintza;
            Cursor cursor = db.rawQuery(query, null);
            int id = 0;
            if (cursor.moveToFirst()) {
                do { id = cursor.getInt(0);
                } while(cursor.moveToNext());
            } return id;
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

    public boolean garbitu(int urtea,String hilabetea,String egune, int ordue)
    {
            String query = "SELECT id FROM "+TAULA_ekintza+" WHERE egune <='"+urtea+"-"+hilabetea+"-"+egune+" "+ordue+":00:00 'order by egune";
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
        return true;
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
        String query = "SELECT tituloa,egune,lekua,deskribapena,link,kartela_link FROM "+TAULA_ekintza+" WHERE id = '"+id+"'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    public String blogazkendata(String blog)
    {
            final Calendar ca = Calendar.getInstance();
            int mYear = ca.get(Calendar.YEAR);
            int mMonth = ca.get(Calendar.MONTH)+1-2;   //urtarrila=0 ,bi kenduko dotzet orain dela bi hilabeteko data lortzeko
            int mDay = ca.get(Calendar.DAY_OF_MONTH);
            int mhour = ca.get(Calendar.HOUR_OF_DAY);
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

                DateFormat dffrom = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                DateFormat dfto = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date day = dffrom.parse(date);
                w = dfto.format(day);
            }catch (Exception e){
                Log.e("data",e.toString());
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



    /*
    // para alarma :SELECT * FROM mytable WHERE strftime('%m-%d', 'now') = strftime('%m-%d', birthday)
   */
}