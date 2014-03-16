package com.nag.android.ringmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class StatusRefreshReciever extends BroadcastReceiver {
	private static PendingIntent getIntent(Context context){
		return PendingIntent.getBroadcast(context.getApplicationContext(), 0, new Intent(context, StatusRefreshReciever.class), 0);
	}
	private static AlarmManager getManager(Context context){
		return (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}
	public static void Start(Context context, int raptime){
		getManager(context).setRepeating( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), raptime,getIntent(context));
	}

	public static void Stop(Context context){
		getManager(context).cancel(getIntent(context));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(RingManager.getInstance(context).isAuto())
		{
			RingManager.getInstance(context).doAuto(context);
		}
	}
}
