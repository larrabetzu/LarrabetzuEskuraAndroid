package com.gorka.rssjarioa;

import com.gorka.timessquare.*;
import java.sql.Date;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;


public class Agenda extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agenda);
		
		
		Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);

		CalendarPickerView calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
		calendar.init(new Date(4), new Date(5), nextYear.getTime());
	
	
	}

	

}
