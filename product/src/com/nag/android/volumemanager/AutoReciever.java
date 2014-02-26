package com.nag.android.volumemanager;

import java.util.Calendar;

import com.nag.android.volumemanager.LocationHelper.OnLocationCollectedListener;
import com.nag.android.volumemanager.VolumeManager.OnFinishPerformListener;
import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

public class AutoReciever extends BroadcastReceiver implements OnFinishPerformListener {

	public static void Start(Context context, int raptime){
		Intent i = new Intent(context, AutoReciever.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, 0);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.MINUTE, raptime);
		((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		VolumeManager.getInstance(context).doAuto(context, this);
	}

	@Override
	public void onFinishPerform(STATUS status) {
		//TODO information
	}

}
