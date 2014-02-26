package com.nag.android.volumemanager;

import com.nag.android.volumemanager.LocationHelper.OnLocationCollectedListener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;

public class VolumeManager implements OnLocationCollectedListener{
	private final String PREF_STATUS = "pref_status";

	private static VolumeManager instance;
	public static VolumeManager getInstance(Context context){
		if(instance==null){
			instance=new VolumeManager(context, PreferenceHelper.getInstance(context));
		}
		return instance;
	}

	public interface OnFinishPerformListener{
		void onFinishPerform(STATUS status);
	}
	
	private final PreferenceHelper pref;
	private final AudioManager audiomanager;
	private final AlarmManager alarmmanager;

	public enum STATUS {uncontrol, enable, manner, silent, auto, follow};
	public enum PRIORITY {schedulefirst, locationfirst, silentfirst, ringfirst};
	private final LocationHelper locationhelper;
	private final LocationSettingManager lsm;
	private final ScheduleSettingManager ssm;
	private PRIORITY priority=PRIORITY.schedulefirst;
	private int frequentry=1;

	public VolumeManager(Context context, PreferenceHelper pref){
		this.pref=pref;
		locationhelper=new LocationHelper(context);
		lsm=new LocationSettingManager(context, pref);
		ssm=new ScheduleSettingManager(context, pref);

		audiomanager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		alarmmanager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}

	public PRIORITY getPriority(){
		return priority;
	}

	boolean isAuto(){
		return STATUS.auto.toString().equals(pref.getString(PREF_STATUS,STATUS.uncontrol.toString()));
	}

	public void setPriority(PRIORITY priority){
		this.priority=priority;
	}

	public void setEnableLocation(boolean value){
		lsm.setEnable(value);
	}

	public void setEnableSchedule(boolean value){
		ssm.setEnable(value);
	}

	public LocationSettingManager getLocationSettingManager(){
		return lsm;
	}

	public ScheduleSettingManager getScheduleSettingManager(){
		return ssm;
	}

	public int getfrequentry(){
		return frequentry;
	}

	public void setFrequently(int frequentry){
		this.frequentry=frequentry;
	}
	
	private int getRepeatTime(){
		return 1000*60*60/frequentry;
	}

	private STATUS getDeviceStatus(Context context){
		switch(audiomanager.getRingerMode()){
		case AudioManager.RINGER_MODE_NORMAL:
			return STATUS.enable;
		case AudioManager.RINGER_MODE_SILENT:
			return STATUS.silent;
		case AudioManager.RINGER_MODE_VIBRATE:
			return STATUS.manner;
		default:
			throw new RuntimeException();
		} 
	}

	public STATUS perform(Context context){
		if(isAuto()){
			perform(context, STATUS.auto);
			return STATUS.auto;
		}else{
			return getDeviceStatus(context);
		}
	}

	public void perform(Context context, STATUS status){
		pref.putString(PREF_STATUS, status.toString());// TODO Ç±ÇÍÇ≈Ç¢Ç¢ÇÃÅH
		// TODO
		switch(status){
		case enable:
			audiomanager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			break;
		case manner:
			audiomanager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			break;
		case silent:
			audiomanager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			break;
		case uncontrol:
			break;
		case auto:
			alarmmanager.setRepeating( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+getRepeatTime(), 0,
					PendingIntent.getService(context, 0, new Intent(context, AutoReciever.class), 0));
			break;
		default:
			throw new RuntimeException();
		}
	}

	public void doAuto(Context context, OnFinishPerformListener listener){
		locationhelper.start(this);
	}

	@Override
	public void onFinishLocationCollection(Location location, RESULT result) {
		if(result==RESULT.resultOK){
			getProgrammedStatus( lsm.getStatus(location), ssm.getStatus());
		}
	}

	private STATUS getMoreSilentStatus(STATUS st1, STATUS st2){
		switch(st1){
		case silent:
			return st1;
		case enable:
			if(st2==STATUS.uncontrol){
				return st1;
			}else{
				return st2;
			}
		case manner:
			if(st2==STATUS.silent){
				return st2;
			}else{
				return st1;
			}
		case uncontrol:
			return st2;
		default:
			throw new RuntimeException();
		}
	}

	private STATUS getModeRingStatus(STATUS st1, STATUS st2){
		switch(st1){
		case silent:
		case manner:
			if(st2==STATUS.uncontrol){
				return st1;
			}else{
				return st2;
			}
		case enable:
			return st1;
		case uncontrol:
			return st2;
		default:
			throw new RuntimeException();
		}
	}

//	STATUS getProgrammedStatus(Context context, Location location){
//		
//		return getProgrammedStatus(
//				LocationSettingManager.getInstance(context).getStatus(location),
//				ScheduleSettingManager.getInstance(context).getStatus(context));
//	}
//
	STATUS getProgrammedStatus(STATUS statusLocation, STATUS statusSchedule){
		STATUS status=null;
		switch(priority){
		case schedulefirst:
			status=statusSchedule;
			if(status!=STATUS.uncontrol){
				return status;
			}else{
				return statusLocation;
			}
		case locationfirst:
			status=statusLocation;
			if(status!=STATUS.uncontrol){
				return status;
			}else{
				return statusSchedule;
			}
		case silentfirst:
			return getMoreSilentStatus(statusLocation, statusSchedule);
		case ringfirst:
			return getModeRingStatus(statusLocation, statusSchedule);
		default:
			throw new RuntimeException();
		}
	}
}
