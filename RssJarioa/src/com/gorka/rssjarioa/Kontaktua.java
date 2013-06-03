package com.gorka.rssjarioa;

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
import android.widget.Toast;

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
            if(etBody.getText().toString().equals("") || etSubject.getText().toString().equals("")){
                Toast.makeText(Kontaktua.this, "Testua eta gaia beteta egon behar dira", Toast.LENGTH_SHORT).show();
            }else{
	            itSend.setType("message/rfc822");
	            itSend.putExtra(android.content.Intent.EXTRA_EMAIL, "ercillagorka@gmail.com");                            
	            itSend.putExtra(android.content.Intent.EXTRA_SUBJECT, etSubject.getText().toString());
	            itSend.putExtra(android.content.Intent.EXTRA_TEXT, etBody.getText());
	            try {
		            startActivity(itSend);
		            setDefault(Notification.DEFAULT_ALL);
		            startActivity(Intent.createChooser(itSend, "Send mail...")); 
	       	 	}catch (android.content.ActivityNotFoundException ex){ 
	       	 		Toast.makeText(Kontaktua.this, "Posta bezeroa ez dago instalatuta.", Toast.LENGTH_SHORT).show(); 
	       	 		}
	       	 	finish();
				}

							}
	                });
	    }

	public void onclicktwitter(View view){
            Intent browserAction = new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/ercillagorka"));
            startActivity(browserAction);
	}
	 
	@SuppressWarnings("deprecation")
	private void setDefault(int defaults) {
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,new Intent(this, Kontaktua.class), 0);
            CharSequence text = "Emaila bidalia";
            final Notification notification = new Notification( R.drawable.arrowright, text,  System.currentTimeMillis());
            notification.setLatestEventInfo(this, "Email",text,  contentIntent);
            notification.defaults = defaults;
            mNotificationManager.notify(MOOD_NOTIFICATIONS,notification);
	}
	   
	 
}
	    		
	    	
