package com.nag.android.volumemanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.nag.android.volumemanager.LocationCollector.OnFinishLocationCollectionListener;
import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnFinishLocationCollectionListener{

	private Map<STATUS, String> status2label=new HashMap<STATUS, String>(){
		{
			put(STATUS.enable, "Enable");
			put(STATUS.manner, "Manner");
			put(STATUS.silent, "Silent");
			put(STATUS.auto, "Auto(Checking...)");
		}
	};

	private Iterator<STATUS> iterator=status2label.keySet().iterator();

	private boolean enable_by_location=true;
	private boolean enable_by_schedule=true;
	private LocationSettingManager lm;
	private ScheduleSettingManager sm;
	private VolumeManager vm;

	private STATUS getStatusBySchedule(){
		if(enable_by_schedule){
			return sm.getStatus(this);
		}else{
			return STATUS.uncontrol;
		}
	}

	private STATUS getNextSTATUS(){
		assert(iterator!=null);
		if(!iterator.hasNext()){
			iterator=status2label.keySet().iterator();
		}
		return iterator.next();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PreferenceHelper pref=new PreferenceHelper(this);
		lm=LocationSettingManager.getInstance(this);
		sm=ScheduleSettingManager.getInstance(this);
		vm=new VolumeManager(this, pref);
		initStatusButton();
		initScheduleButtons();
		initLocationButtons();
	}

	private void initLocationButtons() {
		((ToggleButton)findViewById(R.id.buttonByLocation)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
				enable_by_location=isChecked;
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
				enable_by_schedule=isChecked;
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
		setStatusButton(getNextSTATUS());
		btnStatus.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				setStatusButton(getNextSTATUS());
			}
			
		});
	}

	private void setStatusButton(STATUS status){
		vm.perform(getApplicationContext(), status);
		((Button)findViewById(R.id.buttonStatus)).setText(status2label.get(status));
		if(status==STATUS.auto && enable_by_location){
			LocationCollector.getInstance(this).start(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onFinishLocationCollection(Location location,RESULT result) {
		switch(result){
		case resultOK:
			STATUS status=vm.getProgrammedStatus(lm.getStatus(location),getStatusBySchedule());
			((Button)findViewById(R.id.buttonStatus)).setText("Auto"+"("+status2label.get(status)+")");
			break;
		case resultRetryError://TODO
		case resultDisabled://TODO
		default:
			throw new RuntimeException();
		}
	}
}
