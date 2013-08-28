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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Berriak extends Activity {

	static final String DATA_TITLE = "T";
	static final String DATA_LINK  = "L";
    static final String DATA_DATE  = "D";
	static LinkedList<HashMap<String, String>> data;
	public static ArrayList<String> arr_blogs= new ArrayList<String>(); 
	private ProgressDialog progressDialog;//
	private int post;
    public ArrayList<List_Sarrera> arr_data = new ArrayList<List_Sarrera>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.berriak);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            try {
                this.post =Integer.parseInt(sharedPrefs.getString("post","Null"));
                arr_blogs.clear();
                for (int i = 1; i <= 5; i++) {
                    boolean posi=sharedPrefs.getBoolean(""+i, false);
                    if (posi) {
                        switch (i) {
                        case 1:
                            arr_blogs.add("http://www.larrabetzutik.org/feed/");
                                Log.i("array-Berriak", "http://larrabetzutik.org/feed/");
                            break;
                        case 2:
                            arr_blogs.add("http://www.horibai.org/feed/rss/");
                                Log.i("array-Berriak", "http://horibai.org/feed/rss/");
                            break;
                        case 3:
                            arr_blogs.add("http://www.larrabetzukoeskola.org/feed/");
                                Log.i("array-Berriak", "http://www.larrabetzukoeskola.org/feed/");
                            break;
                        case 4:
                            arr_blogs.add("http://www.larrabetzu.org/gaztelumendi/?feed=rss2");
                                Log.i("array-Berriak", "http://www.larrabetzu.org/gaztelumendi/?feed=rss2");
                            break;
                        case 5:
                            arr_blogs.add("http://www.larrabetzuko-udala.com/_layouts/feed.aspx?xsl=1&web=%2Feu-ES&page=80690b0d-69fd-4e54-901d-309ace29e156&wp=e062f3df-e82b-4a0f-9365-2aefefa7a8a5");
                            Log.i("array-Berriak", "http://www.larrabetzuko-udala.com/_layouts/feed.aspx?xsl=1&web=%2Feu-ES&page=80690b0d-69fd-4e54-901d-309ace29e156&wp=e062f3df-e82b-4a0f-9365-2aefefa7a8a5");
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

    /**
     * Mapan lista bat jaso eta ListView-a sortu
     *
     * */
    private void setData(LinkedList<HashMap<String, String>> data){
            for (HashMap<String, String> aData : data) {
                int logo = R.drawable.ic_launcher;
                if (aData.get("L").contains("larrabetzutik.org")) {
                    logo = R.drawable.larrabetzutik;
                }else if (aData.get("L").contains("horibai.org")) {
                    logo = R.drawable.horibai;
                }else if (aData.get("L").contains("larrabetzukoeskola.org")) {
                    logo = R.drawable.eskola;
                }else if (aData.get("L").contains("larrabetzu.org/gaztelumendi")) {
                    logo = R.drawable.iptx;
                }else if (aData.get("L").contains("larrabetzuko-udala")) {
                    logo = R.drawable.udala;
                }
                arr_data.add(new List_Sarrera(aData.get("T"), aData.get("L"),aData.get("D"), logo));
            }
            ListView lv = (ListView) findViewById(R.id.lstData);
            lv.setAdapter(new List_adaptador(this, R.layout.layout_items, arr_data){
                @Override
                public void onEntrada(Object entrada, View view) {
                    if (entrada != null) {
                        TextView tituloa = (TextView) view.findViewById(R.id.tituloa_berriak);
                        if (tituloa != null)
                            tituloa.setText(((List_Sarrera) entrada).get_tituloa());

                        TextView ordue = (TextView) view.findViewById(R.id.berriak_ordue);
                        if (ordue != null)
                            ordue.setText(((List_Sarrera) entrada).get_ordue());

                        ImageView imagen = (ImageView) view.findViewById(R.id.logo);
                        if (imagen != null)
                            imagen.setImageResource(((List_Sarrera) entrada).get_idImagen());
                    }
                }
            });
    }

	public void eleccion(String cadena){
	        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	        alertbox.setMessage(cadena);
	        alertbox.setPositiveButton("Bale", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) {startActivity(new Intent("hobespenak"));finish();}
	        	});
	        alertbox.setNegativeButton("Ez", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) {
	                finish();
	            	}
	        	});
	        alertbox.show();
	}

    
    
}