package com.gorka.rssjarioa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

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
	static final String AUT_webgunea = "webgunea";
    
	/*
	 * TABLE principal_ekintza
	 */
    static final String TAULA_ekintza= "principal_ekintza";
    static final String KEY_id= "id";
    static final String KEY_TITULOA = "tituloa";
    static final String KEY_LEKUA = "lekua";
	static final String KEY_PUB_DATE="pub_date"; //publikatutako data
	static final String KEY_EGUNE="egune";		 // ekitaldian egune
	static final String KEY_DESKRIBAPENA="deskribapena";
	static final String KEY_JAKINARAZPENA1="jakinarazpena_1";
	static final String KEY_JAKINARAZPENA2="jakinarazpena_2";
	/*
	 * TABLE principal_ekintza_sortzailea
	 */
	static final String TAULA_ekintza_sortzailea= "principal_ekintza_sortzailea";
    static final String SOR_EKINTZA="ekintza_id";
    static final String SOR_AUTOR="autor_id";
	
	static final int DB_BERTSIOA = 1;
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
									"deskribapena varchar(300) NOT NULL,"+
									"pub_date datetime NOT NULL,"+
									"jakinarazpena_1 bool NOT NULL,"+
									"jakinarazpena_2 bool NOT NULL)";

    static final String DB_TAULA_ekintza_sortzailea = "CREATE TABLE principal_ekintza_sortzailea ("+
						    	    "id integer NOT NULL PRIMARY KEY,"+
						    	    "ekintza_id integer NOT NULL,"+
						    	    "autor_id integer NOT NULL REFERENCES principal_autor (id),"+
						    	    "UNIQUE (ekintza_id, autor_id)); ";
						    	    
						    	    
	
    
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
                        URL url = new URL("http://10.0.2.2:8000/wsAutor/");
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
                                                initialValues.put(AUT_webgunea, webgunea);
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
                            initialValues.put(AUT_webgunea, webgunea);
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
                    String egune=null;
                    String sortzailea;
                    String tituloa=null;
                    String pub_date;
                    String deskribapena=null;
                    String lekua=null;
                    foo = 0;
                    String substring = null;
                    try {
                        URL url = new URL("http://10.0.2.2:8000/wsEkintza/");
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
                                            case 6:pub_date = palabra.replace("\"", "").replace(",", "").replace("T", " ").replace("Z","").replace("}}", "").substring(0, 19);
                                                    numero=0;
                                                ContentValues initialValues = new ContentValues();
                                                initialValues.put(KEY_EGUNE, egune);
                                                initialValues.put(KEY_LEKUA,lekua);
                                                initialValues.put(KEY_TITULOA, tituloa);
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
                                                break;
                                        }
                                        if(palabra.equals("tituloa: ") ){
                                            numero=1;
                                        }
                                        if(palabra.equals("deskribapena: ")){
                                            numero=2;
                                        }
                                        if(palabra.contains("sortzailea: ")){
                                            substring = palabra.substring(13).replace(",", "").replace("]", "");
                                        }
                                        if(palabra.equals("lekua: ") ){
                                            numero=4;
                                        }
                                        if(palabra.equals("egune: ") ){
                                            numero=5;
                                        }
                                        if(palabra.contains("pub_date: ")){
                                            numero=6;
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
                    e.printStackTrace();
                    Log.e("sql", "Taula ez da sortu ");
                }
            }
        
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
            {
                Log.w("DbEgokitua", "Datu-basearen bertsioa eguneratzen " + oldVersion + " tik " + newVersion +"-ra" +", datu zahar guztiak kenduko dira");
                db.execSQL("DROP TABLE IF EXISTS principal_autor");
                db.execSQL("DROP TABLE IF EXISTS principal_ekintza_sortzailea");
                db.execSQL("DROP TABLE IF EXISTS principal_ekintza");
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
            do { azken_pub_date = cursor.getString(0);
            } while(cursor.moveToNext());
        }
        Log.e("azken_pub_date",azken_pub_date);

        try{
            fecha1 = sdf.parse(azken_pub_date , new ParsePosition(0));
        }catch (Exception e){
            Log.e("String to date",e.toString());
        }
        /**
         * db ekintza internetetik aktualizatu
         */
        int numero=0;
        String egune=null;
        String sortzailea;
        String tituloa=null;
        String pub_date;
        String deskribapena=null;
        String lekua=null;
        int foo = 0;
        String substring = null;
        try {
            URL url = new URL("http://10.0.2.2:8000/wsEkintza/");
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
                                case 6:pub_date = palabra.replace("\"", "").replace(",", "").replace("T", " ").replace("Z","").replace("}}", "").substring(0, 19); numero=0;
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
                            if(palabra.equals("tituloa: ") ){
                                numero=1;
                            }
                            if(palabra.equals("deskribapena: ")){
                                numero=2;
                            }
                            if(palabra.contains("sortzailea: ")){
                                substring = palabra.substring(13).replace(",", "").replace("]", "");
                            }
                            if(palabra.equals("lekua: ") ){
                                numero=4;
                            }
                            if(palabra.equals("egune: ") ){
                                numero=5;
                            }
                            if(palabra.contains("pub_date: ")){
                                numero=6;
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

    public boolean garbitu(int urtea,String hilabetea,int egune, int ordue)
    {
            String query = "SELECT id FROM "+TAULA_ekintza+" WHERE egune <='"+urtea+"-"+hilabetea+"-"+egune+" "+ordue+"' order by egune";
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
                        Log.e("garbitu",e.toString());
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

    /**
     *
     * @param tituloa
     * @param pub_date
     * @param egune
     * @param lekua
     * @param sortzailea
     * @param deskribapena
     * @param Jakinarazpena
     * @return
    public boolean ekitaldiaJarri(String tituloa,String pub_date,String egune,String lekua, String sortzailea,String deskribapena,boolean Jakinarazpena)
    {
        boolean com = false;
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITULOA, tituloa);
        initialValues.put(KEY_PUB_DATE, pub_date);
        initialValues.put(KEY_EGUNE, egune);
        initialValues.put(KEY_LEKUA,lekua);
        initialValues.put(KEY_DESKRIBAPENA, deskribapena);
        initialValues.put(KEY_JAKINARAZPENA1, false);
        initialValues.put(KEY_JAKINARAZPENA2, false);
        long id =db.insert(TAULA_ekintza, null, initialValues);
        if (id==-1) {
            Log.d(tituloa, "Ez da ekintzarik gehitu");
        }else {
            Log.d(tituloa, "+ ekintza");
            ContentValues initialValuesSortzailea = new ContentValues();
            initialValuesSortzailea.put(SOR_AUTOR, sortzailea);
            initialValuesSortzailea.put(SOR_EKINTZA, id);
            long idsor=db.insert(TAULA_ekintza_sortzailea,null,initialValuesSortzailea);
            if (idsor==-1) {
                Log.d(sortzailea, "Ez da sortzailea gehitu");
            }else {
                com = true ;
                Log.d(sortzailea,"+ sortzailea");
            }
        }
        return com;

    }
     */

    /*
    public String autoreaLortu(int id)
    {
        String query = "SELECT autor_id FROM "+TAULA_ekintza_sortzailea+" WHERE ekintza_id ="+id;
        Cursor c = db.rawQuery(query, null);
        int autor_id = 0;
        if (c.moveToFirst()) {
            do {
                autor_id = c.getInt(0);
            } while(c.moveToNext());
        }
        query = "SELECT nor FROM "+TAULA_autor+" WHERE id = '"+autor_id+"'";
        c = db.rawQuery(query, null);
        String autor=null;
        if (c.moveToFirst()) {
            do {
                autor = c.getString(0);
            } while(c.moveToNext());
        }
        return autor;
    }

    public Cursor ekitaldiGuztiekLortu()
    {
    	return db.query(DB_TAULA, new String[] {KEY_ROWID,KEY_TITULOA,KEY_PUB_DATE,KEY_EGUNE,SOR_SORTZAILEA,KEY_DESKRIBAPENA,KEY_JAKINARAZPENA1}, null, null, null, null, null, null);

    }

    // para alarma :SELECT * FROM mytable WHERE strftime('%m-%d', 'now') = strftime('%m-%d', birthday)

   */
}