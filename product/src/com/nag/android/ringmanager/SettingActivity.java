package com.nag.android.ringmanager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.EditText;

public class SettingActivity extends Activity {

	private final RingManager ringmanager;
	
	SettingActivity(){
		ringmanager=RingManager.getInstance(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

//		((EditText)findViewById(R.id.editAutoArea4Checking)).setText(String.valueOf(ringmanager.getCheckingArea()));
		((EditText)findViewById(R.id.editArea4Entry)).setText(String.valueOf(ringmanager.getLocationSetting().getMaxArea()));
		((EditText)findViewById(R.id.editAutoFrequency)).setText(String.valueOf(ringmanager.getfrequency()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

}
