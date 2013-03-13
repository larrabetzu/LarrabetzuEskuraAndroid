package com.gorka.rssjarioa;

import java.util.ArrayList;
import java.util.Hashtable;

import android.os.Bundle;
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

public class Hobespenak extends Activity {
	ArrayList<Integer> mSelectedItems = new ArrayList(); 
	final String[] lista = {"Larrabetzutik", "Gaztelumendi", "hori bai"};
	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hobespenak);
		
	}
	public void onclickbtndialog(View v){
        //eleccion("Se ha pulsado AlertBox.\nElije opción:");
		onCreateDialog();
		mSelectedItems.size();
    }
	
	public void onCreateDialog() {
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("elejidu").setMultiChoiceItems(lista, null, new DialogInterface.OnMultiChoiceClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int which,
	                       boolean isChecked) {
	                   if (isChecked) {
	                       // If the user checked the item, add it to the selected items
	                       mSelectedItems.add(which);
	                   } else if (mSelectedItems.contains(which)) {
	                       // Else, if the item is already in the array, remove it 
	                       mSelectedItems.remove(Integer.valueOf(which));
	                   }
	               }
	           })
	    
	           .setPositiveButton("BAI", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // User clicked OK, so save the mSelectedItems results somewhere
	                   // or return them to the component that opened the dialog
	                   
	               }
	           })
	           .setNegativeButton("EZ", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   
	               }
	           });

	     builder.show();
	}
	
	/*
	public void eleccion(String cadena){
	    //se prepara la alerta creando nueva instancia
	        AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
	    //seleccionamos la cadena a mostrar
	        alertbox.setMessage(cadena);
	        //elegimos un positivo SI y creamos un Listener
	        alertbox.setPositiveButton("Si", new DialogInterface.OnClickListener() {
	            //Funcion llamada cuando se pulsa el boton Si
	            public void onClick(DialogInterface arg0, int arg1) {
	                mensaje("Pulsado el botón SI");
	            }
	        });
	 
	        //elegimos un positivo NO y creamos un Listener
	        alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
	            //Funcion llamada cuando se pulsa el boton No
	            public void onClick(DialogInterface arg0, int arg1) {
	                mensaje("Pulsado el botón NO");
	            }
	        });
	 
	        //mostramos el alertbox
	        alertbox.show();
	    }
	*/
	
	
	public void mensaje(String cadena){
	    Toast.makeText(this, cadena, Toast.LENGTH_SHORT).show();
	    }
	
	
		
		
	
	
}
