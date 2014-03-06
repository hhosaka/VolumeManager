package com.nag.android.volumemanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nag.android.util.PreferenceHelper;
import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity{

	private Map<STATUS, String> status2label=new HashMap<STATUS, String>(){
		private static final long serialVersionUID = 1L;
		{
			put(STATUS.enable, "Enable");
			put(STATUS.manner, "Manner");
			put(STATUS.silent, "Silent");
			put(STATUS.auto, "Auto(Checking...)");
		}
	};

	private Iterator<STATUS> iterator=status2label.keySet().iterator();
	private VolumeManager vm;
	private StatusChangedReciever reciever=null;

	private STATUS getNextSTATUS(){
		assert(iterator!=null);
		if(!iterator.hasNext()){
			iterator=status2label.keySet().iterator();
		}
		return iterator.next();
	}

	class StatusChangedReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {//TODO ÇÍÇµÅOÉoÅ[ÇÃÇ†Ç∆ÇµÇ‹Ç¬
			if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
				String buf;
				STATUS status=VolumeManager.convDeviceStatus(intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1));
				vm.confirmStatusChangeFromOutside(status);
				setStatusTitle();
				if(vm.isAuto()){
					buf="Auto("+getStatusLabel(getApplicationContext(), status)+")";
				}else{
					buf=getStatusLabel(getApplicationContext(), status);
				}
				((Button)findViewById(R.id.buttonStatus)).setText(buf);
			}
		}
	}

	private static String getStatusLabel(Context context, STATUS status){
		switch(status){
		case enable:
			return context.getString(R.string.label_enable);
		case manner:
			return context.getString(R.string.label_manner);
		case silent:
			return context.getString(R.string.label_silent);
		case uncontrol:
			return context.getString(R.string.label_uncontrol);
		case confirming:
			return context.getString(R.string.label_delete);
		default:
			throw new RuntimeException();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PreferenceHelper pref=new PreferenceHelper(this);
		vm=new VolumeManager(this, pref);
		initStatusButton();
		initScheduleButtons();
		initLocationButtons();
		setStatusTitle();
		vm.setStatus(this);
	}

	private void setStatusTitle() {
		String buf;
		if(vm.isAuto()){
			IntentFilter filter = new IntentFilter();
			filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
			registerReceiver(reciever=new StatusChangedReciever(),filter);
			buf="Auto"+"("+getStatusLabel(getApplicationContext(), vm.getSubStatus())+")";
		}else{
			if(reciever!=null){
				unregisterReceiver(reciever);
				reciever=null;
			}
			buf=getStatusLabel(getApplicationContext(), vm.getStatus());
		}
		((Button)findViewById(R.id.buttonStatus)).setText(buf);
	}

	private void initLocationButtons() {
		ToggleButton tb=((ToggleButton)findViewById(R.id.buttonByLocation));
		tb.setChecked(vm.getEnableLocation());
		tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
		ToggleButton tb=((ToggleButton)findViewById(R.id.buttonBySchedule));
		tb.setChecked(vm.getEnableSchedule());
		tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
				vm.setStatus(getApplicationContext(), getNextSTATUS(), null);
				setStatusTitle();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_help:
			Intent intent=new Intent(this,WebViewActivity.class);
			intent.putExtra(WebViewActivity.PARAM_MODE, WebViewActivity.MODE_HELP);
			startActivity(intent);
			return true;
		case R.id.action_set_frequency:
			return true;
		case R.id.action_set_priority:
			return true;
		}
		return false;
	}
}
