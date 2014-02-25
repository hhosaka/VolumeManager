package com.nag.android.volumemanager;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;

import com.nag.android.volumemanager.VolumeManager.STATUS;

public class ScheduleSettingManager {
	private static final int HOUR_OF_A_DAY=24;
	private final String PREF_SCHEDULE = "schedule_";
	private final PreferenceHelper pref;
	private static ScheduleSettingManager instance;
	private final ArrayList<STATUS> schedules=new ArrayList<STATUS>();
	private boolean enable=true;

	public static ScheduleSettingManager getInstance(Context context){
		if(instance==null){
			instance=new ScheduleSettingManager(context);
		}
		return instance;
	}

	public STATUS getStatus(Context context){
		if(enable){
			return schedules.get(Calendar.getInstance().get(Calendar.HOUR));
		}else{
			return STATUS.uncontrol;
		}
//		return load(Calendar.getInstance().get(Calendar.HOUR));
	}

	public void setEnable(boolean value){
		this.enable=enable;
	}
	private ScheduleSettingManager(Context context){
		pref=new PreferenceHelper(context);
		loadAll(context);
	}

	private void loadAll(Context contex){
		schedules.clear();
		for(int i=0;i<HOUR_OF_A_DAY;++i){
			schedules.add(load(i));
		}
	}

	private STATUS load(int hour){
		return STATUS.valueOf(pref.getString(getKeyPrefSchedule(hour),STATUS.uncontrol.toString()));
	}

	private void save(int hour, STATUS status){
		assert(hour>=0&&hour<24);
		pref.putString(getKeyPrefSchedule(hour), status.toString());
	}

	public ArrayList<STATUS> getList(){
		return schedules;
	}

	public void edit(int hour, STATUS status){
		schedules.set(hour, status);
		save(hour,status);
	}
	private String getKeyPrefSchedule(int hour){
		return PREF_SCHEDULE+String.valueOf(hour);
	}
}
