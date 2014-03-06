package com.nag.android.volumemanager;

import com.nag.android.volumemanager.VolumeManager.OnFinishPerformListener;
import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class StartCheckingReciever extends BroadcastReceiver {

	public static void Start(Context context, int raptime){
		((AlarmManager)context.getSystemService(Context.ALARM_SERVICE))
			.setRepeating( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), raptime,
							PendingIntent.getService(context, 0, new Intent(context, StartCheckingReciever.class), 0));
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		VolumeManager.getInstance(context).setStatus(context);
	}
}
