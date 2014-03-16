package com.nag.android.ringmanager;

import com.nag.android.ringmanager.RingManager.STATUS;
import com.nag.android.util.PreferenceHelper;
import com.nag.android.util.PrimitiveLabel;
import com.nag.android.util.PrimitiveSelector.OnSelectedListener;
import com.nag.android.volumemanager.R;
import com.nag.android.volumemanager.controls.StatusLabel;
import com.nag.android.volumemanager.controls.StatusSelector;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduleSettingActivity extends Activity{

	private ScheduleSetting ssm=null;
	private int day=0;// TODO : day is not supported yet
	private STATUS[] schedule=null;
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
		adapter=new InternalAdapter(this, schedule=ssm.cloneSchedule(day), ssm.getInitialState(day));
		ListView lv =((ListView)findViewById(R.id.listViewLocation));
		lv.setAdapter(adapter);
		findViewById(R.id.buttonDone).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				ssm.setSchedule(day, schedule);
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

	class InternalAdapter extends ArrayAdapter<STATUS> implements OnSelectedListener<STATUS>{
		private final LayoutInflater inflater;
		private final STATUS prev;
		private final STATUS[] statuslist;
		private final StatusLabel labelEnable=new StatusLabel(STATUS.enable.toString(), STATUS.enable);
		private final StatusLabel labelManner=new StatusLabel(STATUS.manner.toString(), STATUS.manner);
		private final StatusLabel labelSilent=new StatusLabel(STATUS.silent.toString(), STATUS.silent);
		private final StatusLabel labelUncontrol=new StatusLabel(STATUS.uncontrol.toString(), STATUS.uncontrol);
		class Holder{
			private int index;
			Holder(int index){
				this.index=index;
			}
			STATUS get(){
				return statuslist[index];
			}
			STATUS set(STATUS status){
				return statuslist[index]=status;
			}
		}

		class LabelFollow extends PrimitiveLabel<STATUS>
		{
			private int index;
			LabelFollow()
			{
				super(STATUS.follow);
			}

			STATUS getSubStatus(){
				for(int i=index-1; i>=0; --i){
					if(statuslist[i]!=STATUS.follow){
						return statuslist[i];
					}
				}
				return prev;
			}
			public void setIndex(int index){
				this.index=index;
			}
			public String toString(){
				return STATUS.follow+"("+getSubStatus()+")";
			}
		}

		public InternalAdapter(Context context,STATUS[] statuslist, STATUS prev) {
			super(context, 0, statuslist);
			this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.statuslist=statuslist;
			this.prev=prev;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.layout_schedule_list_item, null);
				StatusSelector selector=((StatusSelector)convertView.findViewById(R.id.buttonStatus));
				selector.add(labelEnable);
				selector.add(labelManner);
				selector.add(labelSilent);
				selector.add(labelUncontrol);
				LabelFollow tag=new LabelFollow();
				selector.add(tag);
				convertView.setTag(tag);
			}
			((TextView)convertView.findViewById(R.id.textHour)).setText(position+getString(R.string.label_time_postfix));
			StatusSelector selector=((StatusSelector)convertView.findViewById(R.id.buttonStatus));
			selector.setStatus(statuslist[position]);
			Log.d("H:","H:position="+position);
			selector.setOnSelectedListener(this);
			selector.setTag(new Holder(position));
			((LabelFollow)convertView.getTag()).setIndex(position);
//			((LabelFollow)selector.getItemAtPosition(POSITION_LABEL_FOLLOW)).setSubStatus(STATUS.enable);// TODO
			return convertView;
		}

		@Override
		public void OnSelected(View parent, STATUS status) {
			((Holder)parent.getTag()).set(status);
			this.notifyDataSetChanged();
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location_setting, menu);
		return true;
	}
}
