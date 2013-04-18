package com.gorka.rssjarioa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbEgokitua {
	static final String KEY_ROWID = "_id";
    static final String KEY_TITULOA = "tituloa";
	static final String KEY_PUB_DATE="pub_date"; //publikatutako data
	static final String KEY_EGUNE="egune";		 // ekutaldian egune
	static final String KEY_SORTZAILEA="sortzailea";
	static final String KEY_DESKRIBAPENA="deskribapena";
    
	static final String TAG = "DbEgokitua";

	static final int DB_BERTSIOA = 1;
    static final String DB_IZENA = "NireDB";
    static final String DB_TAULA= "egitaraua";
    static final String DB_SORTU = "create table egitaraua (_id integer primary key autoincrement, " + 
    								"tituloa text not null,pub_date text not null,egune text not null,sortzailea text not null," + 
    								"deskribapena text not null);";
	

    final Context context;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    
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
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Datu-basearen bertsioa eguneratzen " + oldVersion + " tik " + newVersion +"-ra" +", datu zahar guztiak kenduko dira");
            db.execSQL("DROP TABLE IF EXISTS egitaraua");
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

    public long ekitaldiaJarri(String tituloa,String pub_date,String egune, String sortzailea,String deskribapena) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITULOA, tituloa);
        initialValues.put(KEY_PUB_DATE,pub_date);
        initialValues.put(KEY_EGUNE,egune);
        initialValues.put(KEY_SORTZAILEA,sortzailea);
        initialValues.put(KEY_DESKRIBAPENA,deskribapena);
        return db.insert(DB_TAULA, null, initialValues);
    }

    public boolean ekitaldiaEguneratu(long rowId,String tituloa,String pub_date,String egune,String sortzailea,String deskribapena) 
    {
    	ContentValues args = new ContentValues();
    	args.put(KEY_TITULOA,tituloa);
    	args.put(KEY_PUB_DATE, pub_date);
    	args.put(KEY_EGUNE,egune);
    	args.put(KEY_SORTZAILEA, sortzailea);
    	args.put(KEY_DESKRIBAPENA, deskribapena);
    	return db.update(DB_TAULA, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    
    public Cursor ekitaldiGuztiekLortu()
    {
    	return db.query(DB_TAULA, new String[] {KEY_ROWID,KEY_TITULOA,KEY_PUB_DATE,KEY_EGUNE,KEY_SORTZAILEA,KEY_DESKRIBAPENA}, null, null, null, null, null, null);
        
    }

    public Cursor ekitaldiaLortu(long rowId) throws SQLException 
    {
        Cursor mCursor =
                db.query(true, DB_TAULA, new String[] {KEY_ROWID,KEY_TITULOA,KEY_PUB_DATE,KEY_EGUNE,KEY_SORTZAILEA,KEY_DESKRIBAPENA}, KEY_ROWID + "=" + rowId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


}