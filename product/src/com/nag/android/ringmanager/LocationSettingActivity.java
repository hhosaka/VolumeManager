package com.nag.android.ringmanager;

import java.util.Calendar;

import com.nag.android.ringmanager.LocationHelper.OnLocationCollectedListener;
import com.nag.android.ringmanager.RingManager.STATUS;
import com.nag.android.ringmanager.R;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocationSettingActivity extends Activity implements OnLocationCollectedListener,  AdapterView.OnItemClickListener{

	private static final int REQUEST_CODE=0x1234;
	private static final double fineness=1.0;//TODO tentative
	private LocationSetting locationsetting=null;
	private ArrayAdapter<LocationData> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_location_setting);

		locationsetting=RingManager.getInstance(this).getLocationSetting();
		initAddButton();
		initListView();
	}

	private void initAddButton() {
		findViewById(R.id.ButtonAdd).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(locationsetting.hasSpace()){
//					LocationCollector.getInstance(getApplicationContext()).start(LocationSettingActivity.this);
					new LocationHelper(getApplicationContext()).start(true, fineness, LocationSettingActivity.this);
				}else{
					//TODO
				}
			}
		});
	}

	private void initListView(){
		adapter=new ArrayAdapter<LocationData>(this,android.R.layout.simple_list_item_1,locationsetting.getLocationData());
		ListView lv =((ListView)findViewById(R.id.listViewLocation));
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
		if(locationsetting.getLocationData().get(position).getType()==LocationData.TYPE.typeEditable){
			String[] items = {"Edit","Delete"};
			new AlertDialog.Builder(LocationSettingActivity.this)
				.setTitle("Select Action")
				.setItems( items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which){
						switch(which){
						case 0:
							edit(position);
							break;
						case 1:
							locationsetting.remove(getApplicationContext(), position);
							adapter.notifyDataSetChanged();
							break;
						}
					}
				})
				.create().show();
		}
	}

	void edit(int position){
		LocationData ld=locationsetting.getLocationData().get(position);
		Intent intent=new Intent(this,EditLocationSettingsActivity.class);
		intent.putExtra(EditLocationSettingsActivity.PARAM_INDEX, position);
		intent.putExtra(EditLocationSettingsActivity.PARAM_TITLE, ld.getTitle());
		intent.putExtra(EditLocationSettingsActivity.PARAM_STATUS, ld.getStatus().toString());
		this.startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE && resultCode==RESULT_OK){
			int index=data.getIntExtra(EditLocationSettingsActivity.PARAM_INDEX, 0);
			assert(index>0);
			locationsetting.edit(this, index,
					data.getStringExtra(EditLocationSettingsActivity.PARAM_TITLE),
					STATUS.valueOf(data.getStringExtra(EditLocationSettingsActivity.PARAM_STATUS)));
			adapter.notifyDataSetChanged();// TODO is it enough?
		}
	}

	@Override
	public void onFinishLocationCollection(Location location, RESULT result) {
		edit(locationsetting.addCurrentLocation(this, DateFormat.format("yyyy/MM/dd kk:mm:ss", Calendar.getInstance()).toString(), location));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_setting, menu);
		return true;
	}
}
