package com.gorka.rssjarioa;

import com.gorka.rssjarioa.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Kontaktua extends Activity {
	 private NotificationManager mNotificationManager;
	 private static int MOOD_NOTIFICATIONS = R.layout.kontaktua;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.kontaktua);              
	        
	        Button bidali = (Button) findViewById(R.id.bidali);
	        bidali.setOnClickListener(new OnClickListener() {                      
		@Override
		public void onClick(View v) {
			
	        EditText etSubject = (EditText) findViewById(R.id.Gaia);
	        EditText etBody = (EditText) findViewById(R.id.testua);					        
            Intent itSend = new Intent(android.content.Intent.ACTION_SEND);
            
            itSend.setType("plain/text");
            itSend.putExtra(android.content.Intent.EXTRA_EMAIL, "ercillagorka@gmail.com");                            
            itSend.putExtra(android.content.Intent.EXTRA_SUBJECT, etSubject.getText().toString());
            itSend.putExtra(android.content.Intent.EXTRA_TEXT, etBody.getText());
            
           
            startActivity(itSend);
            setDefault(Notification.DEFAULT_ALL);
            
            finish();

							}
	                });
	    }
	 public void onclickdeitu(View view){
	    	Intent i=new Intent(android.content.Intent.ACTION_CALL,Uri.parse("tel: +625329261"));
	    	startActivity(i);
	    	finish();
	    }
	 
	 @SuppressWarnings("deprecation")
	private void setDefault(int defaults) {
	        
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	        new Intent(this, Kontaktua.class), 0);
	        CharSequence text = getText("Emaila bidalia");
	        final Notification notification = new Notification( R.drawable.arrowright, text,  System.currentTimeMillis()); 
	        notification.setLatestEventInfo(this, getText("Email"),text,  contentIntent);           
	        notification.defaults = defaults;
	        mNotificationManager.notify(
	                MOOD_NOTIFICATIONS,          
	                notification);
	    }
	private CharSequence getText(String string) {
		// TODO Auto-generated method stub
		return null;
	}    
	 
	 
	}	
	    		
	    	
