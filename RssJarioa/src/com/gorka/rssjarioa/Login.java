package com.gorka.rssjarioa;

import com.gorka.rssjarioa.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	private EditText txt_izena;
	private EditText txt_pasahitza;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}*/
public void BalidatuOnClick(View view){
    	txt_izena=(EditText) findViewById(R.id.txt_izena);
    	txt_pasahitza=(EditText) findViewById(R.id.txt_pasahitza);
    	
    	if(txt_izena.getText().toString().equalsIgnoreCase("gorka") && txt_pasahitza.getText().toString().equalsIgnoreCase("123")){
    		
    			Intent intent=new Intent("menua");
    	    	Bundle bundle =new Bundle();
    	    	bundle.putString("izena", txt_izena.getText().toString());
    	    	intent.putExtras(bundle);
    	    	startActivity(intent);
    	    	finish();
    		
    		}else{
    			Toast.makeText(this, "Zure Erabiltzailea edo pasahitza okerra da ",Toast.LENGTH_LONG).show();
    			txt_izena.setText("");
    			txt_pasahitza.setText("");
    		
    	}
    	
    	
    }


}
