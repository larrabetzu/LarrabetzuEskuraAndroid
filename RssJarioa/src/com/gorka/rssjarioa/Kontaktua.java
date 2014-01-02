package com.gorka.rssjarioa;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;

public class Kontaktua extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.kontaktua);
	        Button bidali = (Button) findViewById(R.id.kontaktua_bidali);
	        bidali.setOnClickListener(new OnClickListener() {                      
                @Override
                public void onClick(View v) {
                    final EditText etSubject = (EditText) findViewById(R.id.kontaktua_Gaia);
                    final EditText etBody = (EditText) findViewById(R.id.kontaktua_testua);
                    final CheckBox etBox = (CheckBox) findViewById(R.id.kontaktua_checkBox);
                    String dana = "";
                    if(etBox.isChecked()){
                        try {
                            dana = "\n \n Android API ="+Build.VERSION.SDK_INT +"\n APP VERSION ="+(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e("ba",e.toString());
                        }
                    }
                    Intent itSend = new Intent(Intent.ACTION_SEND);
                    if(etBody.getText().toString().equals("") || etSubject.getText().toString().equals("")){
                        Toast.makeText(Kontaktua.this, "Testua eta gaia beteta egon behar dira", Toast.LENGTH_SHORT).show();
                    }else{
                        final String[] to = { "larrabetzueskura@gmail.com" };
                        itSend.putExtra(Intent.EXTRA_EMAIL, to);
                        itSend.putExtra(Intent.EXTRA_SUBJECT, etSubject.getText().toString());
                        itSend.putExtra(Intent.EXTRA_TEXT, etBody.getText()+" "+dana);
                        itSend.setType("message/rfc822");
                        try {
                            startActivity(Intent.createChooser(itSend, "Aukeratu e-posta bezeroa"));
                        }catch (android.content.ActivityNotFoundException ex){
                            Toast.makeText(Kontaktua.this, "Posta bezeroa ez dago instalatuta.", Toast.LENGTH_SHORT).show();
                            }
                        finish();
                    }
                }
	        });
	    }

	public void onclicktwitter(@SuppressWarnings("UnusedParameters")View view){
            Intent browserAction = new Intent(Intent.ACTION_VIEW,Uri.parse("https://twitter.com/larrabetzu"));
            startActivity(browserAction);
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
	    		
	    	
