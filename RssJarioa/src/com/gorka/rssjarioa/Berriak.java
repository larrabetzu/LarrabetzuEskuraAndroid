package com.gorka.rssjarioa;

import java.util.HashMap;
import java.util.LinkedList;

import com.gorka.rssjarioa.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class Berriak extends Activity {
	/**
	 * HashMap,XML parser
	 */
	static final String DATA_TITLE = "T";
	static final String DATA_LINK  = "L";
	static LinkedList<HashMap<String, String>> data;
	static String feedUrl = "http://www.berria.info/rss/euskalherria.xml";
	private ProgressDialog progressDialog;//
	private int post;
	/**(Handler) Datuak kargetan amaituten direnean mesu bat bidaltzeko beste hari batera
	 */	
	private final Handler progressHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				data = (LinkedList<HashMap<String, String>>)msg.obj;
				setData(data);					
			}
			progressDialog.dismiss();
	    }
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.berriak);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
        	this.post =Integer.parseInt(sharedPrefs.getString("post","Null"));
        	//Log.i("Preferencias", "es: " + sharedPrefs.getBoolean("opcion1", false));
		} catch (Exception e) {
			this.post=6;
		}
        
        
        loadData();
		
      
        
        ListView lv = (ListView) findViewById(R.id.lstData);
        /**
         * linka nabigatzailean ireki
         */
        lv.setOnItemClickListener(new OnItemClickListener() {

    		@Override
    		public void onItemClick(AdapterView<?> av, View v, int position,
    				long id) {
		       
    			HashMap<String, String> entry = data.get(position);
    			Intent browserAction = new Intent(Intent.ACTION_VIEW, 
    					Uri.parse(entry.get(DATA_LINK)));
    			startActivity(browserAction);				
    		}
    	});        
    }
    
    /** 
     * Mapan lista bat jaso eta ListView-a sortu
     * */
    private void setData(LinkedList<HashMap<String, String>> data){
    	SimpleAdapter sAdapter = new SimpleAdapter(getApplicationContext(), data, 
    			android.R.layout.two_line_list_item, 
    			new String[] { DATA_TITLE, DATA_LINK }, 
    			new int[] { android.R.id.text1});
    	ListView lv = (ListView) findViewById(R.id.lstData);
    	lv.setAdapter(sAdapter);
    }   
    
    /**
     *  progressDialog eta datuak kargatu 
     */
    private void loadData() {
    	progressDialog = ProgressDialog.show(Berriak.this,"","Mesedez itxaron datuak kargatu arte...",true);
    	
    	new Thread(new Runnable(){
    		@Override
    		public void run() {
    			XMLParser parser = new XMLParser(feedUrl,post); 
                Message msg = progressHandler.obtainMessage();
                msg.obj = parser.parse();
    			progressHandler.sendMessage(msg);
    		}}).start();
    }    
}