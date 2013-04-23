package com.gorka.rssjarioa;

import com.gorka.rssjarioa.R;
import android.provider.MediaStore;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class Menua extends Activity {

	DbEgokitua db=new DbEgokitua(this);

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.menua);
	        hilo1.start();
	        
	        
	        
	        
	    }

	    public void onclickbtnberriak(View view){
	    	startActivity(new Intent("berriak"));	
	    }
	    
	    public void onclickbtnagenda(View view){
	    	startActivity(new Intent("agenda"));	
	    }
	    
	    public void onclickbtnkontaktua(View view){
	    	startActivity(new Intent("kontaktua"));
	    }
	    
	    public void onclickbtnhobespenak(View view){
	    	startActivity(new Intent("hobespenak"));
	    }
	    
	    Thread hilo1 = new Thread(new Runnable(){
       	 
            @Override
            public void run() {
            	db.zabaldu();
    	        
    	        
    	        db.zarratu();

            }
 
        });
}
