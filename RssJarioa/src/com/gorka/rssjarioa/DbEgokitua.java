package com.gorka.rssjarioa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbEgokitua {
    
    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    
    /*
	 * TABLE principal_autor
	 */
	static final String DB_TAULA_principal_autor= "principal_autor";
	static final String KEY_NOR = "nor";
	static final String KEY_EMAIL = "email";
	static final String KEY_webgunea = "webgunea";
    
	/*
	 * TABLE principal_ekintza
	 */
    static final String DB_TAULA_ekintza= "principal_ekintza";
	static final String KEY_ROWID = "id";
    static final String KEY_TITULOA = "tituloa";
	static final String KEY_PUB_DATE="pub_date"; //publikatutako data
	static final String KEY_EGUNE="egune";		 // ekitaldian egune
	static final String KEY_DESKRIBAPENA="deskribapena";
	static final String KEY_JAKINARAZPENA1="jakinarazpena_1";
	static final String KEY_JAKINARAZPENA2="jakinarazpena_2";
	
	/*
	 * TABLE principal_ekintza_sortzailea
	 */
	static final String DB_TAULA_principal_ekintza_sortzailea= "principal_ekintza_sortzailea";
	static final String KEY_SORTZAILEA="sortzailea";

	
	static final int DB_BERTSIOA = 2;
    static final String DB_IZENA = "NireDB1";
    
    
    static final String DB_SORTU = "CREATE TABLE principal_autor ("+
						    	    "id integer NOT NULL PRIMARY KEY,"+
						    	    "nor varchar(30) NOT NULL,"+
						    	    "email varchar(75) NOT NULL,"+
						    	    "webgunea varchar(200) NOT NULL); "+
						    	    
									"CREATE TABLE principal_ekintza ("+
									"id integer NOT NULL PRIMARY KEY,"+
									"tituloa varchar(100) NOT NULL,"+
									"egune datetime NOT NULL,"+
									"deskribapena varchar(300) NOT NULL,"+
									"pub_date datetime NOT NULL,"+
									"jakinarazpena_1 bool NOT NULL,"+
									"jakinarazpena_2 bool NOT NULL)"+
						    	    
						    	    "CREATE TABLE principal_ekintza_sortzailea ("+
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
                    db.execSQL(DB_SORTU);
                    //db autor internetetik aktualizatu
                    //db ekintza internetetik aktualizatu

                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e("error", " creando tablas bases de datos ");
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
            String query = "SELECT MAX(_id) AS max_id FROM principal_ekintza";
            //"SELECT MAX(_id) AS _id FROM egitaraua"
            //"SELECT MAX(id) AS max_id FROM mytable
            Cursor cursor = db.rawQuery(query, null);
            int id = 0;
            if (cursor.moveToFirst()) {
                do { id = cursor.getInt(0);
                } while(cursor.moveToNext());
            } return id;
    }
    
	
	public boolean garbitu(int urtea,int hilabeta,int egune)
	{
            db.execSQL("DELETE FROM principal_ekintza WHERE egune < '"+urtea+"-"+hilabeta+"-"+egune+"'"); //adibi:'1990-12-31'
            return true;
	}
    
    /*
    
    public boolean autorsortu(){
    	
            int numero=0;
            String webgunea=null;
            String email=null;
            String nor=null;
            boolean com=true;
        
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
                               if(carac!=' '){

                                   palabra=palabra+carac;
                               }else{
                                  // todas las palabras menos la ultima

                                    switch (numero){
                                       case 1:webgunea=palabra.replace("\"", "").replace(",", "");           numero=0; break;
                                       case 2:email=palabra.replace("\"", "").replace("]", "").replace(",", "");       numero=0; break;
                                       case 3:nor=palabra.replace("\"", "").replace(",", "");         numero=0;
                                           ContentValues initialValues = new ContentValues();
                                           initialValues.put(KEY_NOR, nor);
                                           initialValues.put(KEY_EMAIL, email);
                                           initialValues.put(KEY_webgunea, webgunea);
                                           long id =db.insert(DB_TAULA_principal_autor, null, initialValues);

                                           if (id==-1) {
                                                Log.d(nor, "No se ha añadido ningun autor");

                                            }else {
                                                Log.d(nor, "autor añadido");    }break;
                                                };
                                   if(palabra.equalsIgnoreCase("{\"webgunea\":")){
                                       numero=1;
                                    }
                                   if(palabra.equalsIgnoreCase("\"email\":")){
                                       numero=2;
                                    }
                                   if(palabra.equalsIgnoreCase("\"nor\":")){
                                       numero=3;
                                    }
                                   palabra="";
                                }
                             }
                        //ultima palabra antes de terminar

                        nor=palabra.replaceAll("}}]", "").replace("\"","");
                        ContentValues initialValues = new ContentValues();
                        initialValues.put(KEY_NOR, nor);
                        initialValues.put(KEY_EMAIL, email);
                        initialValues.put(KEY_webgunea, webgunea);
                        long id =db.insert(DB_TAULA_principal_autor, null, initialValues);

                        if (id==-1) {
                            Log.d("autoraktualizatu", "No se ha añadido ningun autor");
                            com=false;
                        }else {
                            Log.d("autoraktualizatu", "autor añadido");
                            com=true;

                        }


                        inputLine=in.readLine();
                        }
                        in.close();
                } catch (Exception e) {
                         System.out.println( e.toString());
                }

            return com;
    }
    
    public boolean ekintzasortu(){
    	
        int numero=0;
        String egune=null;
        String sortzailea=null;
        String tituloa=null;
        String pub_date=null;
        String deskribapena=null;
        
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
                           if(carac!=' '){
                               
                               palabra=palabra+carac;
                           }else{
                              // todas las palabras menos la ultima
                              
                                switch (numero){
                                   case 1:egune=palabra.replace("\"", "").replace(",", "");           numero=0; break;
                                   case 2:sortzailea=palabra.replace("[", "").replace("]", "").replace(",", "");       numero=0; break;
                                   case 3:tituloa=palabra.replace("\"", "").replace(",", "");         numero=0; break;
                                   case 4:pub_date=palabra.replace("\"", "").replace(",", "");        numero=0; break;
                                   case 5:deskribapena=palabra.replace("\"", "").replace("}},", "");    numero=0; 
	                                   ContentValues initialValues = new ContentValues();
	                                   initialValues.put(KEY_EGUNE, egune);
	                                   initialValues.put(KEY_SORTZAILEA, sortzailea);
	                                   initialValues.put(KEY_TITULOA, tituloa);
	                                   initialValues.put(KEY_PUB_DATE, pub_date);
	                                   initialValues.put(KEY_DESKRIBAPENA, deskribapena);
	                                   initialValues.put(KEY_JAKINARAZPENA1, false);
	                                   initialValues.put(KEY_JAKINARAZPENA2, false);
	                                   long id =db.insert(DB_TAULA_principal_autor, null, initialValues);
	                                   
	                                   if (id==-1) {Log.d(tituloa, "No se ha añadido ninguna ekintza");
	                              			
	                              		}else {Log.d(tituloa, "ekintza añadido");    }break;
	                            			};
                               
                               if(palabra.equalsIgnoreCase("{\"egune\":")){
                                   numero=1;
                                }
                               if(palabra.equalsIgnoreCase("\"sortzailea\":")){
                                   numero=2;
                                }
                               if(palabra.equalsIgnoreCase("\"tituloa\":")){
                                   numero=3;
                                }
                               if(palabra.equalsIgnoreCase("\"pub_date\":")){
                                   numero=4;
                                }
                               if(palabra.equalsIgnoreCase("\"deskribapena\":")){
                                   numero=5;
                                }
                               
                               palabra="";
                            }
                           
                         }
                    //ultima palabra antes de terminar
                    
                    deskribapena=palabra.replaceAll("}}]", "").replace("\"","");
                    ContentValues initialValues = new ContentValues();
                    initialValues.put(KEY_EGUNE, egune);
                    initialValues.put(KEY_SORTZAILEA, sortzailea);
                    initialValues.put(KEY_TITULOA, tituloa);
                    initialValues.put(KEY_PUB_DATE, pub_date);
                    initialValues.put(KEY_DESKRIBAPENA, deskribapena);
                    initialValues.put(KEY_JAKINARAZPENA1, false);
                    initialValues.put(KEY_JAKINARAZPENA2, false);
                    long id =db.insert(DB_TAULA_principal_autor, null, initialValues);
                    
                    if (id==-1) {Log.d(tituloa, "No se ha añadido ninguna ekintza");
               			
               		}else {Log.d(tituloa, "ekintza añadido");   }
                    
                    
                    inputLine=in.readLine();//Leemos una nueva inputLine
                }
                in.close();
                //System.out.println(contenido);
	    } catch (Exception e) {
		         System.out.println( e.toString());
	    }
    
    	return true;
  }
    */
    
    
    

    /*

    public long ekitaldiaJarri(String tituloa,String pub_date,String egune, String sortzailea,String deskribapena,boolean Jakinarazpena) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITULOA, tituloa);
        initialValues.put(KEY_PUB_DATE,pub_date);
        initialValues.put(KEY_EGUNE,egune);
        initialValues.put(KEY_SORTZAILEA,sortzailea);
        initialValues.put(KEY_DESKRIBAPENA,deskribapena);
        initialValues.put(KEY_JAKINARAZPENA1,Jakinarazpena);
        return db.insert(DB_TAULA, null, initialValues);
    }

    public boolean ekitaldiaEguneratu(long rowId,String tituloa,String pub_date,String egune,String sortzailea,String deskribapena,boolean Jakinarazpena) 
    {
    	ContentValues args = new ContentValues();
    	args.put(KEY_TITULOA,tituloa);
    	args.put(KEY_PUB_DATE, pub_date);
    	args.put(KEY_EGUNE,egune);
    	args.put(KEY_SORTZAILEA, sortzailea);
    	args.put(KEY_DESKRIBAPENA, deskribapena);
    	args.put(KEY_JAKINARAZPENA1,Jakinarazpena);
    	return db.update(DB_TAULA, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    
    public Cursor ekitaldiGuztiekLortu()
    {
    	return db.query(DB_TAULA, new String[] {KEY_ROWID,KEY_TITULOA,KEY_PUB_DATE,KEY_EGUNE,KEY_SORTZAILEA,KEY_DESKRIBAPENA,KEY_JAKINARAZPENA1}, null, null, null, null, null, null);
        
    }

    public Cursor ekitaldiaLortu(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DB_TAULA, new String[] {KEY_ROWID,KEY_TITULOA,KEY_PUB_DATE,KEY_EGUNE,KEY_SORTZAILEA,KEY_DESKRIBAPENA,KEY_JAKINARAZPENA1}, KEY_ROWID + "=" + rowId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
   */
}