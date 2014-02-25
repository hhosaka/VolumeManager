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
		Intent i = new Intent(context, AutoReciever.class); // ReceivedActivity���Ăяo���C���e���g���쐬
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, 0); // �u���[�h�L���X�g�𓊂���PendingIntent�̍쐬

		Calendar calendar = Calendar.getInstance(); // Calendar�擾
		calendar.setTimeInMillis(System.currentTimeMillis()); // ���ݎ������擾
		calendar.add(Calendar.MINUTE, raptime); // ���������15�b���ݒ�
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
