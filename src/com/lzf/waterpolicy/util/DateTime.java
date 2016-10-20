package com.lzf.waterpolicy.util;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;

public class DateTime {

	public static void getDate(Context context, final EditText et, String strCalendar) {

		if (!strCalendar.equals("") && strCalendar != null) {
			String ymd[] = strCalendar.split("/");
			new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					et.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
				}
			}, Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]) - 1, Integer.parseInt(ymd[2])).show();
		} else {
			Calendar calendar = Calendar.getInstance();
			new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					et.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
				}
			}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
		}
	}
}
