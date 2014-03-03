package com.nag.android.volumemanager;

import com.nag.android.util.PreferenceHelper;
import com.nag.android.util.SimpleSelector.OnStatusSelectedListener;
import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduleSettingActivity extends Activity{

	private ScheduleSetting ssm=null;
	private InternalAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_schedule_setting);

		ssm=new ScheduleSetting(this, new PreferenceHelper(this));
		initListView();
	}

	private void initListView(){
		adapter=new InternalAdapter(this,0);// TODO : day is not supported yet
		ListView lv =((ListView)findViewById(R.id.listViewLocation));
		lv.setAdapter(adapter);
		findViewById(R.id.buttonDone).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				ssm.save(0);// TODO: day does not support yet
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

	class InternalAdapter extends BaseAdapter implements OnStatusSelectedListener{
		private final LayoutInflater inflater;
		private final int day;

		public InternalAdapter(Context context, int day) {
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.day=day;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.layout_schedule_list_item, null);
			}
			((TextView)convertView.findViewById(R.id.textHour)).setText(position+getString(R.string.label_time_postfix));
			StatusSelector s=((StatusSelector)convertView.findViewById(R.id.buttonStatus));
			s.add(getString(R.string.label_enable), STATUS.enable);
			s.add(getString(R.string.label_manner), STATUS.manner);
			s.add(getString(R.string.label_silent), STATUS.silent);
			s.add(getString(R.string.label_uncontrol), STATUS.uncontrol);
			s.add(getString(R.string.label_Follow), STATUS.follow);
			s.setStatus((STATUS)getItem(position));
			s.setIndex(position);
			s.setOnStatusSelectedListener(this);
			return convertView;
		}

		@Override
		public void OnSelected(int index, Object status) {
			ssm.setStatus(day, index, (STATUS)status);
		}

		@Override
		public int getCount() {
			return ScheduleSetting.HOUR_OF_A_DAY;
		}

		@Override
		public Object getItem(int position) {
			return ssm.getStatus(day,position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_setting, menu);
		return true;
	}
}
