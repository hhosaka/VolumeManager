package com.nag.android.volumemanager;

import java.util.Calendar;

import com.nag.android.volumemanager.LocationCollector.OnFinishLocationCollectionListener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

public class AutoReciever extends BroadcastReceiver implements OnFinishLocationCollectionListener {

	private Context context;
	public static void Start(Context context, int raptime){
		Intent i = new Intent(context, AutoReciever.class); // ReceivedActivityを呼び出すインテントを作成
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, 0); // ブロードキャストを投げるPendingIntentの作成

		Calendar calendar = Calendar.getInstance(); // Calendar取得
		calendar.setTimeInMillis(System.currentTimeMillis()); // 現在時刻を取得
		calendar.add(Calendar.MINUTE, raptime); // 現時刻より15秒後を設定
		((AlarmManager)context.getSystemService(Context.ALARM_SERVICE)).set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context=context;
		LocationCollector.getInstance(context).start(this);
	}

	@Override
	public void onFinishLocationCollection(Location location, RESULT result) {
		VolumeManager.getInstance(context).perform(context, LocationSettingManager.getInstance(context).getStatus(location));
	}

}
