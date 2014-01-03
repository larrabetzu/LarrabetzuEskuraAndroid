package com.gorka.rssjarioa;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
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

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.StandardExceptionParser;
import com.google.analytics.tracking.android.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Berriak extends Activity {

    DbEgokitua db=new DbEgokitua(this);
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
                for (int i = 1; i <= 6; i++) {
                    boolean posi=sharedPrefs.getBoolean(""+i, false);
                    if (posi) {
                        switch (i) {
                        case 1:
                            arr_blogs.add("http://www.larrabetzutik.org/feed/");
                                Log.i("array-Berriak", "http://larrabetzutik.org/feed/");
                            break;
                        case 2:
                            arr_blogs.add("http://www.horibai.org/feed/");
                                Log.i("array-Berriak", "http://horibai.org/feed/");
                            break;
                        case 3:
                            arr_blogs.add("http://www.larrabetzukoeskola.org/feed/");
                                Log.i("array-Berriak", "http://www.larrabetzukoeskola.org/feed/");
                            break;
                        case 4:
                            arr_blogs.add("http://gaztelumendi.tumblr.com/rss");
                                Log.i("array-Berriak", "http://gaztelumendi.tumblr.com/rss");
                            break;
                        case 5:
                            arr_blogs.add("http://www.larrabetzuko-udala.com/_layouts/feed.aspx?xsl=1&web=%2Feu-ES&page=80690b0d-69fd-4e54-901d-309ace29e156&wp=e062f3df-e82b-4a0f-9365-2aefefa7a8a5");
                            Log.i("array-Berriak", "http://www.larrabetzuko-udala.com/_layouts/feed.aspx?xsl=1&web=%2Feu-ES&page=80690b0d-69fd-4e54-901d-309ace29e156&wp=e062f3df-e82b-4a0f-9365-2aefefa7a8a5");
                            break;
                        case 6:
                            arr_blogs.add("http://www.literaturaeskola.org/?feed=rss2");
                            Log.i("array-Berriak", "http://www.literaturaeskola.org/?feed=rss2");
                            break;
                        }
                    }
                }
                EasyTracker tracker = EasyTracker.getInstance(this);
                tracker.send(MapBuilder.createEvent("Berriak","blogs numeroa","blogs numeroa" ,(long) arr_blogs.size()).build());
                loadData();
                Log.i("Arraylist", ""+arr_blogs.size());
            }catch (Exception ex) {
                eleccion();
                Log.e("patapan", ex.getMessage());
            }
            ListView lv = (ListView) findViewById(R.id.berriak_lstData);
            //linka nabigatzailean ireki
            lv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> av, View v, int position,long id) {
                    String link = arr_data.get(position).get_link();

                    Log.e("link",link);
                    String blog = "ez dafo";
                    if (link.contains("larrabetzutik.org")) {
                        blog = "larrabetzutik";
                    }else if (link.contains("horibai.org")) {
                        blog = "horibai";
                    }else if (link.contains("larrabetzukoeskola.org")) {
                        blog = "eskola";
                    }else if (link.contains("gaztelumendi")) {
                        blog = "gaztelumendi";
                    }else if (link.contains("larrabetzuko-udala")) {
                        blog = "udala";
                    }else if (link.contains("literaturaeskola")) {
                        blog = "literaturaeskola";
                    }

                    EasyTracker tracker = EasyTracker.getInstance(Berriak.this);
                    tracker.send(MapBuilder.createEvent("web", "navigation", blog, (long) position).build());

                    Intent intent=new Intent("webnavigation");
                    Bundle bundle =new Bundle();
                    bundle.putString("weblink", link);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int pos, long id) {
                        String link = arr_data.get(pos).get_link();
                        String title = "Aukeratu aplikazioa berria elkarbanatzeko";
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, link+" @larrabetzu");
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, title));
                        return true;
                    }
                });
            }
    }

    /**
     *  progressDialog eta datuak kargatu
     */
    private void loadData() {
            progressDialog = ProgressDialog.show(Berriak.this,"","Mesedez itxaron datuak kargatu arte...",true);
            new Thread(new Runnable(){
                @Override
                public void run() {
                    db.zabaldu();
                    HashMap mblog = new HashMap();
                    int i = arr_blogs.size();
                    String [] arr = arr_blogs.toArray(new String[i]);
                    String blogak ="";
                    try{
                        for (String getblog : arr_blogs)
                            if (getblog.contains("larrabetzutik.org")) {
                                String data = db.blogazkendata("larrabetzutik");
                                mblog.put("larrabetzutik", data);
                                blogak = blogak + "'larrabetzutik',";

                            } else if (getblog.contains("horibai.org")) {
                                String data = db.blogazkendata("horibai");
                                mblog.put("horibai", data);
                                blogak = blogak + "'horibai',";

                            } else if (getblog.contains("larrabetzukoeskola.org")) {
                                String data = db.blogazkendata("eskola");
                                mblog.put("eskola", data);
                                blogak = blogak + "'eskola',";

                            } else if (getblog.contains("gaztelumendi")) {
                                String data = db.blogazkendata("gaztelumendi");
                                mblog.put("gaztelumendi", data);
                                blogak = blogak + "'gaztelumendi',";

                            } else if (getblog.contains("larrabetzuko-udala")) {
                                String data = db.blogazkendata("udala");
                                mblog.put("udala", data);
                                blogak = blogak + "'udala',";
                            } else if (getblog.contains("literaturaeskola")) {
                                String data = db.blogazkendata("literaturaeskola");
                                mblog.put("literaturaeskola", data);
                                blogak = blogak + "'literaturaeskola',";
                            }
                    }catch (Exception e){
                        Log.e("loaddata-Berriak",e.toString());
                        Tracker myTracker = EasyTracker.getInstance(Berriak.this);
                        myTracker.send(MapBuilder.createException(new StandardExceptionParser(Berriak.this, null)
                                                        .getDescription(Thread.currentThread().getName(),e), false).build());
                    }
                    db.linkgarbitu(blogak.substring(0,blogak.length()-1));
                    XMLParser parser = new XMLParser(arr,post,mblog);
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
            getDataDb();
            lvsortu();
			progressDialog.dismiss();
	    }
	};

    /**
     * Mapan lista bat jaso eta ListView-a sortu
     * */
    private void setData(LinkedList<HashMap<String, String>> data){
            for (HashMap<String, String> aData : data) {
                String blog = null;
                if (aData.get("L").contains("larrabetzutik.org")) {
                    blog = "larrabetzutik";
                }else if (aData.get("L").contains("horibai.org")) {
                    blog = "horibai";
                }else if (aData.get("L").contains("larrabetzukoeskola.org")) {
                    blog = "eskola";
                }else if (aData.get("L").contains("gaztelumendi")) {
                    blog = "gaztelumendi";
                }else if (aData.get("L").contains("larrabetzuko-udala")) {
                    blog = "udala";
                }else if (aData.get("L").contains("literaturaeskola")) {
                    blog = "literaturaeskola";
                }
                if (blog!=null){
                    db.linkjarri(blog,aData.get("T"),aData.get("L"),aData.get("D"));
                    db.linkkendu(blog,post);
                }
            }

    }
    /**
     * Datu basetik informazioa hartu eta arr_data-n sartu
     */
    private void getDataDb(){
        try{
            int logo = R.drawable.rsslogo;
            Cursor cursor = db.linklortu();
            do {
                String s = cursor.getString(0);
                if (s != null) {
                    if (s.equals("larrabetzutik")) {
                        logo = R.drawable.larrabetzutik;
                    }else if (s.equals("horibai")) {
                        logo = R.drawable.horibai;
                    }else if (s.equals("eskola")) {
                        logo = R.drawable.eskola;
                    }else if (s.equals("gaztelumendi")) {
                        logo = R.drawable.iptx;
                    }else if (s.equals("udala")) {
                        logo = R.drawable.udala;
                    }else if (s.equals("literaturaeskola")) {
                        logo = R.drawable.literatura;
                    }
                }
                arr_data.add(new List_Sarrera(cursor.getString(1), cursor.getString(2), logo));

            } while(cursor.moveToNext());

        }catch (Exception ex){
            Log.e("arr_data-datubasetik-Berriak",ex.toString());
        }
        db.zarratu();

    }
    private void lvsortu(){
        ListView lv = (ListView) findViewById(R.id.berriak_lstData);
        lv.setAdapter(new List_adaptador(this, R.layout.layout_items, arr_data){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    TextView tituloa = (TextView) view.findViewById(R.id.layout_items_tituloa);
                    if (tituloa != null)
                        tituloa.setText(((List_Sarrera) entrada).get_tituloa());

                    ImageView imagen = (ImageView) view.findViewById(R.id.layout_items_logo);
                    if (imagen != null)
                        imagen.setImageResource(((List_Sarrera) entrada).get_idImagen());
                }
            }
        });
    }

	public void eleccion(){
	        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setTitle("Lehenengo zure hobespenak jarri behar dozuz");
	        alertbox.setPositiveButton("Bai", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) {startActivity(new Intent("hobespenak"));finish();}
	        	});
	        alertbox.setNegativeButton("Ez", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface arg0, int arg1) {
	                finish();
	            	}
	        	});
	        alertbox.show();
	}

    @Override
    public void onStart() {
        super.onStart();
        // The rest of your onStart() code.
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // The rest of your onStop() code.
        EasyTracker.getInstance(this).activityStop(this);
    }
}