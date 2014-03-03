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
	private final PreferenceHelper pref;
	private final STATUS[][] schedules = new STATUS[DAY_OF_A_WEEK][];
	private boolean enable=true;

	protected ScheduleSetting(Context context, PreferenceHelper pref){
		this.pref=pref;
		for(int i=0;i<DAY_OF_A_WEEK; ++i){
			schedules[i]=new STATUS[HOUR_OF_A_DAY];
		}
		loadAll();
	}

	public STATUS getStatus(){
		if(enable){
			return fixFollowState(Calendar.getInstance().get(Calendar.DAY_OF_WEEK), Calendar.getInstance().get(Calendar.HOUR));
		}else{
			return STATUS.uncontrol;
		}
	}

	public STATUS fixFollowState(int day, int hour){
		for(int i=0;i<DAY_OF_A_WEEK;++i){
			for(int j=0;j<HOUR_OF_A_DAY;++j){
				STATUS status=schedules[(day-i)%DAY_OF_A_WEEK][(hour-j)%HOUR_OF_A_DAY];
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

	public void save(int day){
		if(day==EVERYDAY){
			for(int i=0; i<DAY_OF_A_WEEK;++i){
				save(i);
			}
		}else{
			StringBuffer sb=new StringBuffer();
			for(STATUS status:schedules[day]){
				if(sb.length()>0){sb.append(",");}
				sb.append(status.toString());
			}
			pref.putString(PREF_SCHEDULE+day, sb.toString());
		}
	}

	public void edit(int day, int hour, STATUS status){
		schedules[day][hour]= status;
	}

	private STATUS getEverydayStatus(int hour){
		STATUS ret=schedules[0][hour];
		for(int i=1;i<DAY_OF_A_WEEK;++i){
			if(ret!=schedules[i][hour]){
				return null;
			}
		}
		return ret;
	}

	public Object getStatus(int day, int hour){
		if(day==EVERYDAY){
			return getEverydayStatus(hour);
		}else{
			return schedules[day][hour];
		}
	}

	private void setEverydayStatus(int hour, STATUS status){
		for(int i=0;i<DAY_OF_A_WEEK;++i){
			schedules[i][hour]=status;
		}
	}

	public void setStatus(int day, int hour, STATUS status) {
		if(day==EVERYDAY){
			setEverydayStatus(hour, status);
		}else{
			schedules[day][hour]=status;
		}
	}
}
