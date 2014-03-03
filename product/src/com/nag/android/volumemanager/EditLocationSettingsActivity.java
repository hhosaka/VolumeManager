package com.nag.android.volumemanager;

import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class EditLocationSettingsActivity extends Activity{
	static final String PARAM_INDEX="param_index";
	static final String PARAM_TITLE="param_title";
	static final String PARAM_STATUS="param_status";
	private int index;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_location_settings);

		index=getIntent().getIntExtra(PARAM_INDEX, 0);
		((EditText) findViewById(R.id.textLocationName)).setText(getIntent().getStringExtra(PARAM_TITLE));
		StatusSelector s=((StatusSelector)findViewById(R.id.buttonStatus));
		s.add("Enable",STATUS.enable);
		s.add("Manner",STATUS.manner);
		s.add("Silent",STATUS.silent);
		s.add("Uncontrol",STATUS.uncontrol);
		s.setStatus(STATUS.valueOf(getIntent().getStringExtra(PARAM_STATUS)));
		findViewById(R.id.buttonDone).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent =new Intent();
				intent.putExtra(PARAM_INDEX,index);
				intent.putExtra(PARAM_TITLE,((EditText) findViewById(R.id.textLocationName)).getText().toString());
				intent.putExtra(PARAM_STATUS,((StatusSelector)findViewById(R.id.buttonStatus)).getStatus().toString());
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_location_settings, menu);
		return true;
	}
}
