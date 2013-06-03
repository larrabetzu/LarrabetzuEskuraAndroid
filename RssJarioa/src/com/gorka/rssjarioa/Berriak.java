package com.gorka.rssjarioa;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Berriak extends Activity {

	static final String DATA_TITLE = "T";
	static final String DATA_LINK  = "L";
	static LinkedList<HashMap<String, String>> data;
	public static ArrayList<String> arr_blogs= new ArrayList<String>(); 
	private ProgressDialog progressDialog;//
	private int post;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.berriak);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            try {
                this.post =Integer.parseInt(sharedPrefs.getString("post","Null"));
                arr_blogs.clear();
                for (int i = 1; i <= 4; i++) {
                    boolean posi=sharedPrefs.getBoolean(""+i, false);
                    if (posi) {
                        switch (i) {
                        case 1:
                            arr_blogs.add("http://larrabetzutik.org/feed/");
                                Log.i("array", "http://larrabetzutik.org/feed/");
                            break;
                        case 2:
                            arr_blogs.add("http://horibai.org/feed/rss/");
                                Log.i("array", "http://horibai.org/feed/rss/");
                            break;
                        case 3:
                            arr_blogs.add("http://www.larrabetzukoeskola.org/feed/");
                                Log.i("array", "http://www.larrabetzukoeskola.org/feed/");
                            break;
                        case 4:
                            arr_blogs.add("http://www.larrabetzu.net/?feed=rss2");
                                Log.i("array", "http://www.larrabetzu.net/?feed=rss2");
                            break;
                        case 5:
                            arr_blogs.add("http://www.larrabetzu.org/gaztelumendi/?feed=rss2");
                                Log.i("array", "http://www.larrabetzu.org/gaztelumendi/?feed=rss2");
                            break;
                        }
                    }
                }
                loadData();
                Log.i("Arraylist", ""+arr_blogs.size());
            }catch (Exception ex) {
                eleccion("Lehenengo zure hobespenak jarri behar dituzu");
                Log.e("patapan", ex.getMessage());
            }
            ListView lv = (ListView) findViewById(R.id.lstData);
            //linka nabigatzailean ireki
            lv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> av, View v, int position,
                        long id) {
                    HashMap<String, String> entry = data.get(position);
                    Intent browserAction = new Intent(Intent.ACTION_VIEW,Uri.parse(entry.get(DATA_LINK)));
                    startActivity(browserAction);
                }
            });
    }
  
    
    /**
     * Mapan lista bat jaso eta ListView-a sortu
     *
     * */
    private void setData(LinkedList<HashMap<String, String>> data){
            SimpleAdapter sAdapter = new SimpleAdapter(getApplicationContext(), data, R.layout.layout_items,
            new String[] { DATA_TITLE,DATA_LINK},new int[] {R.id.tituloa_berriak});
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
                    String [] arr = arr_blogs.toArray(new String[arr_blogs.size()]);
                    XMLParser parser = new XMLParser(arr,post);
                    Message msg = progressHandler.obtainMessage();
                    msg.obj = parser.parse();
                    progressHandler.sendMessage(msg);

                }}).start();
    }
    
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
	
    
    
	public void eleccion(String cadena){
	        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	        alertbox.setMessage(cadena);
	        alertbox.setPositiveButton("Bale", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) {startActivity(new Intent("hobespenak"));finish();
	            	}
	        	});
	        alertbox.setNegativeButton("Ez", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) {
	                finish();
	            	}
	        	});
	        alertbox.show();
	}

    
    
}