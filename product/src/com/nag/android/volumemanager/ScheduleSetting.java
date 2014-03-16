package com.nag.android.volumemanager;

import java.util.Calendar;

import android.content.Context;

import com.nag.android.util.PreferenceHelper;
import com.nag.android.volumemanager.VolumeManager.STATUS;

public class ScheduleSetting {
	static final int HOUR_OF_A_DAY=24;
	static final int EVERYDAY=-1;
	private static final int DAY_OF_A_WEEK=7;
	private final String PREF_SCHEDULE = "schedule_";
	private final String PREF_SCHEDULE_ENABLE = "schedule_enable";
	private final PreferenceHelper pref;
	private final STATUS[][] schedules = new STATUS[DAY_OF_A_WEEK][];
	private boolean enable=true;

	protected ScheduleSetting(Context context, PreferenceHelper pref){
		this.pref=pref;
		for(int i=0;i<DAY_OF_A_WEEK; ++i){
			schedules[i]=new STATUS[HOUR_OF_A_DAY];
		}
		loadSetting();
		loadAll();
	}

	public void setSchedule(int day, STATUS[] schedule){
		if(day==EVERYDAY){
			for(int i=0;i<HOUR_OF_A_DAY;++i){
				if(schedule[i]!=STATUS.na){
					for(int j=0;j<DAY_OF_A_WEEK;++j){
						schedules[j][i]=schedule[i];
					}
				}
			}
			saveAll();
		}else{
			schedules[day]=schedule;
			save(day);
		}
	}

	public STATUS getStatus(){
		if(enable){
			return resolveStatus(Calendar.getInstance().get(Calendar.DAY_OF_WEEK), Calendar.getInstance().get(Calendar.HOUR));
		}else{
			return STATUS.uncontrol;
		}
	}

	public STATUS getInitialState(int day){
		if(day==EVERYDAY){
			return STATUS.na;
		}else{
			return resolveStatus((DAY_OF_A_WEEK+day-1)%DAY_OF_A_WEEK,HOUR_OF_A_DAY-1);
		}
	}

	private STATUS resolveStatus(int day, int hour){
		for(int i=0;i<DAY_OF_A_WEEK;++i){
			for(int j=0;j<HOUR_OF_A_DAY;++j){
				STATUS status=schedules[(DAY_OF_A_WEEK+day-i)%DAY_OF_A_WEEK][(HOUR_OF_A_DAY+hour-j)%HOUR_OF_A_DAY];
				if(status!=STATUS.follow){
					return status;
				}
			}
		}
		return STATUS.uncontrol;
	}

	public boolean getEnable(){
		return enable;
	}

	public void setEnable(boolean value){
		this.enable=value;
		saveSetting();
	}

	public STATUS[] cloneSchedule(int day){
		if(day==EVERYDAY){
			return cloneScheduleEveryDay();
		}else{
			return schedules[day].clone();
		}
	}

	private STATUS[] cloneScheduleEveryDay(){
		STATUS[]ret=
		{STATUS.na,STATUS.na,STATUS.na,STATUS.na,STATUS.na,STATUS.na,
			STATUS.na,STATUS.na,STATUS.na,STATUS.na,STATUS.na,STATUS.na,
			STATUS.na,STATUS.na,STATUS.na,STATUS.na,STATUS.na,STATUS.na,
			STATUS.na,STATUS.na,STATUS.na,STATUS.na,STATUS.na,STATUS.na};
//		STATUS[]ret=new STATUS[24];
//		for(STATUS status:ret){
//			status=STATUS.na;
//		}
		for(int i=0;i<HOUR_OF_A_DAY;++i){
			for(int j=0;j<DAY_OF_A_WEEK;++j){
				if(ret[i]==STATUS.na){
					ret[i]=schedules[j][i];
				}else if(ret[i]!=schedules[j][i]){
					ret[i]=STATUS.na;
					break;
				}
			}
		}
		return ret;
	}

	private void loadSetting(){
		pref.getBoolean(PREF_SCHEDULE_ENABLE, true);
	}

	private void loadAll(){
		for(int i=0;i<DAY_OF_A_WEEK;++i){
			load(i);
		}
	}

	private void load(int day){
		if(day==EVERYDAY){
			for(int i=0; i<DAY_OF_A_WEEK;++i){
				load(i);
			}
		}else{
			String buf=pref.getString(PREF_SCHEDULE+day,null);
			if(buf==null){
				for(int i=0;i<HOUR_OF_A_DAY;++i){
					schedules[day][i]=STATUS.follow;
				}
			}else{
				String[]token=buf.split(",");
				assert(token.length==HOUR_OF_A_DAY);
				for(int i=0;i<HOUR_OF_A_DAY;++i){
					schedules[day][i]=STATUS.valueOf(token[i]);
				}
			}
		}
	}

	private void saveSetting(){
		pref.putBoolean(PREF_SCHEDULE_ENABLE, enable);
	}

	private void saveAll(){
		for(int i=0; i<DAY_OF_A_WEEK;++i){
			save(i);
		}
	}

	private void save(int day){
		assert(day!=EVERYDAY);
		StringBuffer sb=new StringBuffer();
		for(STATUS status:schedules[day]){
			if(sb.length()>0){sb.append(",");}
			sb.append(status.toString());
		}
		pref.putString(PREF_SCHEDULE+day, sb.toString());
	}
}
