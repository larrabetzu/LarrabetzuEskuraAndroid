package com.gorka.rssjarioa;


import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLParser {
	private URL[] url=null;
	private int post;
	private static int len;
    private static HashMap mblog;
	
	public XMLParser(String[] arr,int post,HashMap mblog) {
            try {
                XMLParser.len=arr.length;
                this.url = new URL [len];
                this.post = post;
                XMLParser.mblog = mblog;
                for (int i = 0; i < len; i++) {
                    Log.i("array-XMLParse",""+arr[i]);
                    this.url[i]=new URL(arr[i]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
	}
	
	public LinkedList<HashMap<String, String>> parse() {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            LinkedList<HashMap<String, String>> entries = new LinkedList<HashMap<String, String>>();
            HashMap<String, String> entry;
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
            final Calendar ca = Calendar.getInstance();
            int mYear = ca.get(Calendar.YEAR);
            int mMonth = ca.get(Calendar.MONTH)+1-2;   //urtarrila=0 ,bi kenduko dotzet orain dela bi hilabeteko data lortzeko
            int mDay = ca.get(Calendar.DAY_OF_MONTH);
            int mhour = ca.get(Calendar.HOUR_OF_DAY);
            String oraindelahilebat = mYear+"-"+mMonth+"-"+mDay+" "+mhour+":00:00";//yyyy-MM-dd hh:mm:ss

        try {
                for (int x = 0; x < len; x++){
                    java.util.Date fecha1 = null;
                    java.util.Date fecha2 = null;
                    URL BlogUrl = this.url[x];
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document dom = builder.parse(BlogUrl.openConnection().getInputStream());

                    Element root = dom.getDocumentElement();
                    NodeList items = root.getElementsByTagName("item");
                    NodeList avatar = root.getChildNodes();
                    Log.i("avatar",""+avatar.toString());
                    String blog = null;
                    if (BlogUrl.toString().contains("larrabetzutik.org")){
                        blog = "larrabetzutik";

                    }else if (BlogUrl.toString().contains("horibai.org")){
                        blog = "horibai";

                    }else if (BlogUrl.toString().contains("larrabetzukoeskola.org")){
                        blog = "eskola";

                    }else if (BlogUrl.toString().contains("larrabetzu.org/gaztelumendi")){
                        blog = "gaztelumendi";

                    }else if (BlogUrl.toString().contains("larrabetzuko-udala")){
                        blog = "udala";
                    }
                    try{
                        String prueba = mblog.get(blog).toString();
                        fecha1 = sdf2.parse(prueba, new ParsePosition(0));
                    }catch (Exception e){
                        Log.e("String to date mblog",e.toString());
                    }
                    if (fecha1==null){
                        try {
                            String prueba = mblog.get(blog).toString();
                            fecha1 = sdf1.parse(prueba, new ParsePosition(0));
                        }catch (Exception ex){
                            fecha1 = sdf1.parse(oraindelahilebat, new ParsePosition(0));
                            Log.e("String to date mblog",ex.toString());
                        }
                    }
                    int numpost=this.post;
                    if (numpost>items.getLength()) {
                        numpost=items.getLength();
                    }
                    for (int i=0;i<numpost;i++){
                        entry = new HashMap<String, String>();
                        Node item = items.item(i);
                        NodeList properties = item.getChildNodes();
                        int properties_leng =properties.getLength();
                        for (int j=0;j<properties_leng;j++){
                            Node property = properties.item(j);
                            String name = property.getNodeName();
                            boolean confirmado = true;
                            if(name.equalsIgnoreCase("pubDate")){
                                String data = property.getFirstChild().getNodeValue().substring(4,25);
                                try{
                                    fecha2 = sdf2.parse(data, new ParsePosition(0));
                                }catch (Exception e){
                                    Log.e("String to date mblog",e.toString());
                                }
                                if(fecha1.before(fecha2)){
                                    entry.put(Berriak.DATA_DATE, data);
                                    Log.i("data-xmlParse", data);
                                }else {
                                    confirmado = false;
                                    i = numpost;
                                }
                            }
                            if (confirmado){
                                if (name.equalsIgnoreCase("title")){
                                    entry.put(Berriak.DATA_TITLE, property.getFirstChild().getNodeValue());
                                }else if (name.equalsIgnoreCase("link")){
                                    entry.put(Berriak.DATA_LINK, property.getFirstChild().getNodeValue());
                                }
                            }else {
                                entry.clear();
                            }
                        }
                        if (!entry.isEmpty())
                        entries.add(entry);
                    }
                }
            }catch (Exception e) {
                Log.e("XML",e.toString());
            }
            return entries;
	}		
}
