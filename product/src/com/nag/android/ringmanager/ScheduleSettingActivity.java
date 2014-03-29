package com.nag.android.ringmanager;

import com.nag.android.ringmanager.RingManager.STATUS;
import com.nag.android.ringmanager.ScheduleSetting.Day;
import com.nag.android.ringmanager.controls.StatusLabel;
import com.nag.android.ringmanager.controls.StatusSelector;
import com.nag.android.util.ButtonSelector;
import com.nag.android.util.OnValueChangedListener;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ScheduleSettingActivity extends FragmentActivity {
	private static String ARG_DAY="arg_day";
	private static final int[] labelDays=
		{R.string.label_day_sunday
		,R.string.label_day_monday
		,R.string.label_day_tueday
		,R.string.label_day_wednesday
		,R.string.label_day_thursday
		,R.string.label_day_friday
		,R.string.label_day_satueday
		,R.string.label_day_everyday};
	private ScheduleSetting setting=null;
	private static ProgressDialog progressdialog=null;

	SectionsPagerAdapter mSectionsPagerAdapter;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_schedule_setting);
		setting=RingManager.getInstance(this).getScheduleSetting();

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	protected void onResume(){
		super.onResume();
		if(progressdialog!=null){
			progressdialog.dismiss();
			progressdialog=null;
		}
	}

	public static void showProgressDialog(Context context){
		progressdialog=new ProgressDialog(context);
		progressdialog.setTitle("Searching...");
		progressdialog.setMessage("Please wait for finish searching");
		progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressdialog.setCancelable(false);
		progressdialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new InternalFragment();
			Bundle args=new Bundle();
			args.putInt(ARG_DAY, position);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			return labelDays.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getString(labelDays[position]);
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class InternalFragment extends Fragment {
		private static final int INITIAL_HOUR = 8;

		private int day;

		public InternalFragment(){
		}

		public void setArguments(Bundle args){
			this.day=args.getInt(ARG_DAY);
		}

		private ScheduleSetting getSetting(){
			return ((ScheduleSettingActivity)getActivity()).setting;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_schedule_setting, container, false);
			InternalAdapter adapter=new InternalAdapter(getActivity(), getSetting().getSchedule(day), getSetting().getInitialState(day));
			ListView lv =((ListView)rootView.findViewById(R.id.listViewLocation));
			lv.setAdapter(adapter);
			lv.setSelection(INITIAL_HOUR);
			return rootView;
		}

		class InternalAdapter extends BaseAdapter implements OnValueChangedListener<STATUS>{//OnSelectedListener<STATUS>{
			private final LayoutInflater inflater;
			private final Day day;
			private final StatusLabel labelEnable=new StatusLabel(STATUS.enable.toString(), STATUS.enable);
			private final StatusLabel labelManner=new StatusLabel(STATUS.manner.toString(), STATUS.manner);
			private final StatusLabel labelSilent=new StatusLabel(STATUS.silent.toString(), STATUS.silent);
			private final StatusLabel labelUncontrol=new StatusLabel(STATUS.uncontrol.toString(), STATUS.uncontrol);
//			private final StatusLabel labelFollow=new StatusLabel(STATUS.follow.toString(), STATUS.follow);
//			private final StatusLabel labelNA=new LabelNA();
//			class Holder{
//				private int index;
//				Holder(int index){
//					this.index=index;
//				}
//				STATUS get(){
//					return day.get(index);
//				}
//				STATUS set(STATUS status){
//					day.set(index, status);
//					return day.get(index);
//				}
//			}

//			class LabelNA extends StatusLabel{
//				public LabelNA(){
//					super("na", STATUS.na);
//				}
//				public boolean isEnable(){
//					return false;
//				}
//			}
			class LabelFollow extends StatusLabel
			{
				private int hour;
				LabelFollow()
				{
					super("Follow", STATUS.follow);// TODO
				}

				STATUS getSubStatus(){
					return day.resolveStatus(hour);
//					return day.get(index);
//					for(int i=index-1; i>=0; --i){
//						if(day.get(i)!=STATUS.follow){
//							return day.get(i);
//						}
//					}
//					return prev;
				}
				public void setIndex(int index){
					this.hour=index;
				}
				public String toString(){
					return STATUS.follow+"("+getSubStatus()+")";
				}
			}

			public InternalAdapter(Context context,Day statuslist, STATUS prev) {
				this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				this.day=statuslist;
//				this.prev=prev;
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
					LabelFollow labelfollow=new LabelFollow();
					selector.add(labelfollow);
					convertView.setTag(labelfollow);
				}
				((TextView)convertView.findViewById(R.id.textHour)).setText(position+getString(R.string.label_time_postfix));
				StatusSelector selector=((StatusSelector)convertView.findViewById(R.id.buttonStatus));
				selector.setStatus(day.get(position));
				selector.setOnValueChangedListener(this);
				selector.setTag(Integer.valueOf(position));
				((LabelFollow)convertView.getTag()).setIndex(position);
				return convertView;
			}

			@Override
//			public void OnSelected(View parent, STATUS status) {
			public String OnValueChanged(ButtonSelector<STATUS>parent, STATUS status) {
				day.set(((Integer)parent.getTag()).intValue(),status);
				this.notifyDataSetChanged();
				return "temp";//status.toString();
			}

			@Override
			public int getCount() {
				return day.size();
			}

			@Override
			public Object getItem(int position) {
				return day.get(position);
			}

			@Override
			public long getItemId(int position) {
				return 0;
			}
		}
	}
}
