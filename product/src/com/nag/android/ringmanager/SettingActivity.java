package com.nag.android.ringmanager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
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

		((EditText)findViewById(R.id.editAutoArea4Checking)).setText(String.valueOf(ringmanager.getMaxArea4Checking()));
		((EditText)findViewById(R.id.editArea4Entry)).setText(String.valueOf(ringmanager.getMaxArea4Entry()));
		((EditText)findViewById(R.id.editAutoFrequency)).setText(String.valueOf(ringmanager.getfrequency()));
		((CheckBox)findViewById(R.id.checkBoxPreventSilentMode)).setChecked(ringmanager.getNoSilent());
		((CheckBox)findViewById(R.id.checkBoxResetAutoMode)).setChecked(ringmanager.getResetAuto());
		findViewById(R.id.buttonOK).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				ringmanager.setMaxArea4Checking(Integer.valueOf(((EditText)findViewById(R.id.editAutoArea4Checking)).getText().toString()));
				ringmanager.setMaxArea4Entry(Integer.valueOf(((EditText)findViewById(R.id.editArea4Entry)).getText().toString()));
				ringmanager.setFrequency(Integer.valueOf(((EditText)findViewById(R.id.editAutoFrequency)).getText().toString()));
				ringmanager.setResetAuto(((CheckBox)findViewById(R.id.checkBoxPreventSilentMode)).isChecked());
				ringmanager.setNoSilent(((CheckBox)findViewById(R.id.checkBoxResetAutoMode)).isChecked());
				ringmanager.save();
				finish();
			}
		});
		findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		findViewById(R.id.buttonDefault).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				// TODO is it correct model??
				((EditText)findViewById(R.id.editAutoArea4Checking)).setText(String.valueOf(1000));
				((EditText)findViewById(R.id.editArea4Entry)).setText(String.valueOf(100));
				((EditText)findViewById(R.id.editAutoFrequency)).setText(String.valueOf(2));
				((CheckBox)findViewById(R.id.checkBoxPreventSilentMode)).setChecked(false);
				((CheckBox)findViewById(R.id.checkBoxResetAutoMode)).setChecked(true);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}

}
