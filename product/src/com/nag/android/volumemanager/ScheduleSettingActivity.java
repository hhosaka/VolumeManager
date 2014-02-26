package com.nag.android.volumemanager;

import java.util.ArrayList;

import com.nag.android.volumemanager.StatusSelector.OnStatusSelectedListener;
import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduleSettingActivity extends Activity implements AdapterView.OnItemClickListener{

	private ScheduleSettingManager ssm=null;
	private ArrayAdapter<STATUS> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_schedule_setting);

		ssm=new ScheduleSettingManager(this, new PreferenceHelper(this));
		initListView();
	}

	private void initListView(){
		adapter=new InternalAdapter(this, ssm.getList());
		ListView lv =((ListView)findViewById(R.id.listViewLocation));
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		findViewById(R.id.buttonDone).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				ssm.saveAll();
				finish();
			}
			
		});
		findViewById(R.id.buttonCancel).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				finish();
			}
			
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int position, long id){
//		if(ssm.getLocationData().get(position).getType()==LocationData.TYPE.typeEditable){
//			String[] items = {"Edit","Delete"};
//			new AlertDialog.Builder(ScheduleSettingActivity.this)
//				.setTitle("Select Action")
//				.setItems( items, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which){
//						switch(which){
//						case 0:
//							edit(position);
//							break;
//						case 1:
//							lsm.remove(getApplicationContext(), position);
//							adapter.notifyDataSetChanged();
//							break;
//						}
//					}
//				})
//				.create().show();
//		}
	}

	class InternalAdapter extends ArrayAdapter<STATUS> implements OnStatusSelectedListener{
		private final LayoutInflater inflater;
		private ArrayList<STATUS> schedules;

		public InternalAdapter(Context context, ArrayList<STATUS>schedules) {
			super(context, R.layout.layout_schedule_list_item, schedules);
			this.schedules=schedules;
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.layout_schedule_list_item, null);
			}
			((TextView)convertView.findViewById(R.id.textHour)).setText(position+"Žž");
			StatusSelector s=((StatusSelector)convertView.findViewById(R.id.buttonStatus));
			s.add("Enable",STATUS.enable);
			s.add("Manner",STATUS.manner);
			s.add("Silent",STATUS.silent);
			s.add("Uncontrol",STATUS.uncontrol);
			s.add("follow",STATUS.follow);
			s.setStatus(getItem(position));
			s.setIndex(position);
			s.setOnStatusSelectedListener(this);
			return convertView;
		}

		@Override
		public void OnSelected(int index, STATUS status) {
			schedules.set(index,status);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_setting, menu);
		return true;
	}
}
