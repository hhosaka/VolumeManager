package com.nag.android.ringmanager;

import com.nag.android.ringmanager.RingManager.STATUS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmartTimerReceiver extends BroadcastReceiver {
	private static PendingIntent getIntent(Context context){
		return PendingIntent.getBroadcast(context.getApplicationContext(), 0, new Intent(context, SmartTimerReceiver.class), 0);
	}
	private static AlarmManager getManager(Context context){
		return (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}
	public static void start(Context context, long time){
		getManager(context).set( AlarmManager.RTC_WAKEUP, time, getIntent(context));
	}

	public static void stop(Context context){
		getManager(context).cancel(getIntent(context));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		RingManager.getInstance(context).setStatus(context, STATUS.enable);
		// TODO reset UI
	}
}
