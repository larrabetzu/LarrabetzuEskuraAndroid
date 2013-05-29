package com.gorka.rssjarioa;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class Agenda extends Activity {
	  
    private Button mPickDate;    
    private int mYear;    
    private int mMonth;    
    private int mDay;    
    private static final int DATE_DIALOG_ID = 0;
     
    private DatePickerDialog.OnDateSetListener mDateSetListener =            
    	new DatePickerDialog.OnDateSetListener() {                
    	public void onDateSet(DatePicker view, int year,                                       
    			int monthOfYear, int dayOfMonth) {                    
    		mYear = year;                    
    		mMonth = monthOfYear;                    
    		mDay = dayOfMonth;                    
    		updateDisplay();                
    		}            
    	};
    
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {        
    	super.onCreate(savedInstanceState);        
    	setContentView(R.layout.agenda);  
    	     
    	mPickDate = (Button) findViewById(R.id.pickDate);       
    	mPickDate.setOnClickListener(new View.OnClickListener() 
    		{            
    		public void onClick(View v) 
    			{                
    			showDialog(DATE_DIALOG_ID); 
    			}});        
       
    	final Calendar c = Calendar.getInstance();        
    	mYear = c.get(Calendar.YEAR);        
    	mMonth = c.get(Calendar.MONTH);        
    	mDay = c.get(Calendar.DAY_OF_MONTH);        
    	      
    	updateDisplay();    
    	
    	//mirar los eventos hasta la fecha seleccionada
    	
    	//crear el listView
    	
    	//mirar si cambia algo y actualizar las notificaciones en la base de datos
    	
    	
    	
    }
    
       
    private void updateDisplay() {        
    	mPickDate.setText(            
    			new StringBuilder()                                      
    			.append(mMonth + 1).append("-")  //urtarrila=0                
    			.append(mDay).append("-")                    
    			.append(mYear).append(" "));    
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {    
    	switch (id) {   
    		case DATE_DIALOG_ID:        
    			return new DatePickerDialog(this,                    
    					mDateSetListener,                    
    					mYear, mMonth, mDay); 
                
    			}    
    	return null;
    	}
    
    

}
