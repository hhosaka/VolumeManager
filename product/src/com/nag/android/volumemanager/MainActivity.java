package com.nag.android.volumemanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nag.android.volumemanager.VolumeManager.OnFinishPerformListener;
import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity{

	private Map<STATUS, String> status2label=new HashMap<STATUS, String>(){
		{
			put(STATUS.enable, "Enable");
			put(STATUS.manner, "Manner");
			put(STATUS.silent, "Silent");
			put(STATUS.auto, "Auto(Checking...)");
		}
	};

	private Iterator<STATUS> iterator=status2label.keySet().iterator();
	private VolumeManager vm;

	private STATUS getNextSTATUS(){
		assert(iterator!=null);
		if(!iterator.hasNext()){
			iterator=status2label.keySet().iterator();
		}
		return iterator.next();
	}

	class StatusChangedReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
				String buf;
			if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
				switch(intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1))
				{
				case AudioManager.RINGER_MODE_NORMAL:
					buf="Auto(Enable)";
					break;
				case AudioManager.RINGER_MODE_VIBRATE:
					buf="Auto(Manner)";
					break;
				case AudioManager.RINGER_MODE_SILENT:
					buf="Auto(Silent)";
					break;
				default:
					throw new RuntimeException();
				}
				((Button)findViewById(R.id.buttonStatus)).setText(buf);
			}
		}
	}
	private StatusChangedReciever reciever=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PreferenceHelper pref=new PreferenceHelper(this);
		vm=new VolumeManager(this, pref);
		initStatusButton();
		initScheduleButtons();
		initLocationButtons();
		STATUS status=vm.perform(this);
		setStatusTitle(status);
	}

	private void setStatusTitle(STATUS status) {
		if(status==STATUS.auto){
			IntentFilter filter = new IntentFilter();
			filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
			registerReceiver(reciever=new StatusChangedReciever(),filter);
			((Button)findViewById(R.id.buttonStatus)).setText("Auto(Checking)");
		}else{
			if(reciever!=null){
				unregisterReceiver(reciever);
				reciever=null;
			}
			((Button)findViewById(R.id.buttonStatus)).setText(status.toString());
		}
	}

	private void initLocationButtons() {
		((ToggleButton)findViewById(R.id.buttonByLocation)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				vm.setEnableLocation(isChecked);
			}
		});
		findViewById(R.id.buttonByLocationSetting).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this,LocationSettingActivity.class));
			}
		});
	}

	private void initScheduleButtons() {
		((ToggleButton)findViewById(R.id.buttonBySchedule)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				vm.setEnableSchedule(isChecked);
			}
		});
		findViewById(R.id.buttonByScheduleSetting).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(MainActivity.this,ScheduleSettingActivity.class));
			}
		});
	}

	private void initStatusButton() {
		Button btnStatus=(Button)findViewById(R.id.buttonStatus);
		btnStatus.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				setStatusButton(getNextSTATUS());
			}
		});
	}

	private void setStatusButton(STATUS status){
		vm.perform(getApplicationContext(), status);
		setStatusTitle(status);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
