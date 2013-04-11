package com.gorka.rssjarioa;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


	
	public class Hobespenak extends PreferenceActivity {
		@SuppressWarnings("deprecation")
		@Override
		public void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    
		    if (Build.VERSION.SDK_INT<Build.VERSION_CODES.HONEYCOMB) {
		    addPreferencesFromResource(R.xml.aukerak);
		    }
		    
		}
		 @Override
		  public void onBuildHeaders(List<Header> target) {
		   // loadHeadersFromResource(R.xml.preference_headers, target);
		  }
	
	
	public void mensaje(String cadena){
	    Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show();
	    }
}
