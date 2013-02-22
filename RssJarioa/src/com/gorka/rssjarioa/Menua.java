package com.gorka.rssjarioa;

import com.gorka.rssjarioa.R;

import android.provider.MediaStore;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

public class Menua extends Activity {


	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.menua);
	        
	    }

	    public void onclickbtnberriak(View view){
	    	startActivity(new Intent("berriak"));	
	    }
	    
	    public void onclickbtnirudiak(View view){
	    	Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
	    	startActivity(intent);
	    }
	    public void onclickbtnkamara(View view){
	    	Intent intent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    	startActivity(intent);
	    	
	    }
	    
	    public void onclickbtnkontaktua(View view){
	    	startActivity(new Intent("kontaktua"));
	    }
	    
	    
	   
	    			
	    		
	    	
	    



}
