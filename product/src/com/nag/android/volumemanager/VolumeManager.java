package com.nag.android.volumemanager;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.os.Bundle;

public class VolumeManager{
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

	public enum STATUS {uncontrol, enable, manner, silent, auto};
	public enum PRIORITY {schedulefirst, locationfirst, silentfirst, ringfirst};
	private STATUS status;
	private PRIORITY priority=PRIORITY.schedulefirst;

	public VolumeManager(Context context, PreferenceHelper pref){
		this.pref=pref;
		audiomanager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	}

	public PRIORITY getPriority(){
		return priority;
	}

	public void setPriority(PRIORITY priority){
		this.priority=priority;
	}

	public STATUS getStatus(Context context){
		return status;
	}

	public boolean isAuto(){
		return status==STATUS.auto;
	}

	public STATUS getDeviceStatus(Context context){
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

	public void perform(Context context, STATUS status){
		this.status=status;
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
		case auto://perform have to be called after calculation
		default:
			throw new RuntimeException();
		}
		pref.putString(PREF_STATUS, status.toString());// TODO ����ł����́H
	}

//	public void doAuto(Context context, Location location){
//		perform(getProgrammedStatus(context, location));
//	}

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

	STATUS getProgrammedStatus(Context context, Location location){
		
		return getProgrammedStatus(
				LocationSettingManager.getInstance(context).getStatus(location),
				ScheduleSettingManager.getInstance(context).getStatus(context));
	}

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