package com.gorka.rssjarioa;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

public class XMLParser {
	private URL[] url=null;
	private int post;
	private static int len;
	
	public XMLParser(String[] arr,int post) {
		try {
			XMLParser.len=arr.length;
			this.url=new URL [len];
			for (int i = 0; i < len; i++) {
				Log.i("array",""+arr[i]);
				this.url[i]=new URL(arr[i]);
			}
			this.post=post;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public LinkedList<HashMap<String, String>> parse() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		LinkedList<HashMap<String, String>> entries = new LinkedList<HashMap<String, String>>();
		HashMap<String, String> entry;
		try {
			for (int x = 0; x < len; x++) 
			{
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(this.url[x].openConnection().getInputStream());
			Element root = dom.getDocumentElement();
			NodeList items = root.getElementsByTagName("item");
			
			NodeList avatar = root.getChildNodes();
			Log.i("avater",""+avatar.toString());
			
			
			
			int numpost=this.post;
			if (this.post>items.getLength()) {
				numpost=items.getLength();
			}
			for (int i=0;i<numpost;i++){
				entry = new HashMap<String, String>();				
				Node item = items.item(i);
				NodeList properties = item.getChildNodes();
				
				for (int j=0;j<properties.getLength();j++){
					Node property = properties.item(j);
					String name = property.getNodeName();
					if (name.equalsIgnoreCase("title")){
						entry.put(Berriak.DATA_TITLE, property.getFirstChild().getNodeValue());
					} else if (name.equalsIgnoreCase("link")){
						entry.put(Berriak.DATA_LINK, property.getFirstChild().getNodeValue());						
					}/**
					*else if(name.equalsIgnoreCase("pubDate")){
						entry.put(Berriak.DATA_DATE, property.getFirstChild().getNodeValue());		
					**/
					
				}
				
				
				entries.add(entry);
			}
		}	
			
		} catch (Exception e) {
			throw new RuntimeException(e);
			
		} 

		return entries;
	}		
}
