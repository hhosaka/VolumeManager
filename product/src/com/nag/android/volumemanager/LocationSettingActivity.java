package com.nag.android.volumemanager;

import com.nag.android.volumemanager.LocationHelper.OnLocationCollectedListener;
import com.nag.android.volumemanager.LocationSettingManager.LocationData;
import com.nag.android.volumemanager.LocationSettingManager.OnLocationAddedListener;
import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocationSettingActivity extends Activity implements OnLocationCollectedListener, OnLocationAddedListener, AdapterView.OnItemClickListener{

	private static final int REQUEST_CODE=0x1234;
	private LocationSettingManager lsm=null;
	private final LocationHelper locationhelper;
	private ArrayAdapter<LocationData> adapter;

	LocationSettingActivity(){
		locationhelper=new LocationHelper(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_setting);

		lsm=VolumeManager.getInstance(this).getLocationSettingManager();
		initAddButton();
		initListView();
	}

	private void initAddButton() {
		findViewById(R.id.ButtonAdd).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				if(lsm.hasSpace()){
//					LocationCollector.getInstance(getApplicationContext()).start(LocationSettingActivity.this);
					locationhelper.start(LocationSettingActivity.this);
				}else{
					//TODO
				}
			}
		});
	}

	private void initListView(){
		adapter=new ArrayAdapter<LocationData>(this,android.R.layout.simple_list_item_1,lsm.getLocationData());
		ListView lv =((ListView)findViewById(R.id.listViewLocation));
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
	}

	@Override
	public void OnLocationAdded(Location location) {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
		if(lsm.getLocationData().get(position).getType()==LocationData.TYPE.typeEditable){
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
							lsm.remove(getApplicationContext(), position);
							adapter.notifyDataSetChanged();
							break;
						}
					}
				})
				.create().show();
		}
	}

	void edit(int position){
		LocationData ld=lsm.getLocationData().get(position);
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
			lsm.edit(this, index,
					data.getStringExtra(EditLocationSettingsActivity.PARAM_TITLE),
					STATUS.valueOf(data.getStringExtra(EditLocationSettingsActivity.PARAM_STATUS)));
			adapter.notifyDataSetChanged();// TODO is it enough?
		}
	}

	@Override
	public void onFinishLocationCollection(Location location, RESULT result) {
		edit(lsm.addCurrentLocation(this, location));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_setting, menu);
		return true;
	}
}
