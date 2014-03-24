package com.nag.android.ringmanager;

import com.nag.android.ringmanager.RingManager.PRIORITY;
import com.nag.android.ringmanager.RingManager.STATUS;
import com.nag.android.ringmanager.controls.PrioritySelector;
import com.nag.android.ringmanager.controls.StatusRotationButton;
import com.nag.android.util.Label;
import com.nag.android.util.PrimitiveSelector.OnSelectedListener;
import com.nag.android.util.RotationButton.OnValueChangedListener;
import com.nag.android.ringmanager.R;

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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity{

	private RingManager ringmanager;
	private StatusMonitor statusmonitor=null;
	private StatusRotationButton btnStatus=null; 

	class StatusMonitor extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(RingManager.RING_MANAGER_STATUS_CHANGED)){
				btnStatus.setValue(ringmanager.getStatus());
			}else if (intent.getAction().equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
				STATUS status=RingManager.convParam2Status(intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1));
				btnStatus.setValue(ringmanager.confirm(status));
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		ringmanager=RingManager.getInstance(this);
		initStatusButton();
		initScheduleButtons();
		initLocationButtons();
		initPriorityButton();
		setEnableSubControl();
		ringmanager.doAuto(this);
		IntentFilter filter = new IntentFilter();
		filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
		filter.addAction(RingManager.RING_MANAGER_STATUS_CHANGED);
		registerReceiver(statusmonitor=new StatusMonitor(),filter);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(statusmonitor!=null){
			unregisterReceiver(statusmonitor);
			statusmonitor=null;
		}
	}

	@Override
	protected void onResume(){
		super.onResume();
		ringmanager.doAuto(this);
	}
	class AutoLabel extends Label<STATUS>
	{
		public AutoLabel(String label, STATUS value) {
			super(label, value);
		}
		public String toString(){
			if(ringmanager.isAuto()){
				return "Auto(" + ringmanager.getStatus() +")";
			}else{
				return super.toString();
			}
		}
	}

	private void initStatusButton(){
		btnStatus=(StatusRotationButton)findViewById(R.id.buttonStatus);
		btnStatus.add(new AutoLabel(getString(R.string.label_enable), STATUS.enable));
		btnStatus.add(new AutoLabel(getString(R.string.label_manner), STATUS.manner));
		btnStatus.add(new AutoLabel(getString(R.string.label_silent), STATUS.silent));
		btnStatus.add(new AutoLabel(getString(R.string.label_auto), STATUS.auto));
		btnStatus.setValue(ringmanager.getStatus());
		btnStatus.setOnValueChangedListener(new OnValueChangedListener<STATUS>(){
			@Override
			public void OnValueChanged(STATUS value) {
				ringmanager.setStatus(MainActivity.this,value);
			}
		});
	}

	private void initLocationButtons() {
		ToggleButton tb=((ToggleButton)findViewById(R.id.buttonByLocation));
		tb.setChecked(ringmanager.getEnableLocation());
		tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				ringmanager.setEnableLocation(isChecked);
				ringmanager.doAuto(MainActivity.this);
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
		tb.setChecked(ringmanager.getEnableSchedule());
		tb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				ringmanager.setEnableSchedule(isChecked);
				ringmanager.doAuto(MainActivity.this);
			}
		});
		findViewById(R.id.buttonByScheduleSetting).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				startActivity(new Intent(MainActivity.this,ScheduleSettingActivity.class));
				ScheduleSettingActivity.showProgressDialog(MainActivity.this);
				startActivity(new Intent(MainActivity.this,ScheduleSettingActivity.class));
			}
		});
	}

	private MainActivity getActivity(){
		return this;
	}

	private void initPriorityButton(){
		PrioritySelector sp=((PrioritySelector)findViewById(R.id.buttonPriority));
		sp.add(new Label<PRIORITY>(getString(R.string.label_locationfirst),PRIORITY.locationfirst));
		sp.add(new Label<PRIORITY>(getString(R.string.label_schedulefirst),PRIORITY.schedulefirst));
		sp.add(new Label<PRIORITY>(getString(R.string.label_ringfirst),PRIORITY.ringfirst));
		sp.add(new Label<PRIORITY>(getString(R.string.label_silentfirst),PRIORITY.silentfirst));
		sp.setPriority(ringmanager.getPriority());
		sp.setOnSelectedListener(new OnSelectedListener<PRIORITY>(){
			@Override
			public void OnSelected(View parent, PRIORITY priority) {
				ringmanager.setPriority(getActivity(),(PRIORITY)priority);
			}
		});
	}
	
	private void setEnableSubControl(){
		boolean value=ringmanager.isAuto();
		findViewById(R.id.buttonPriority).setEnabled(value);
		findViewById(R.id.buttonByLocation).setEnabled(value);
		findViewById(R.id.buttonBySchedule).setEnabled(value);
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
		case R.id.action_open_setting:
			startActivity(new Intent(this,SettingActivity.class));
			return true;
		}
		return false;
	}
}
