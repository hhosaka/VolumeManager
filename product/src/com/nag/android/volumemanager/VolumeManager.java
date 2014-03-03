package com.nag.android.volumemanager;

import com.nag.android.util.PreferenceHelper;
import com.nag.android.volumemanager.LocationHelper.OnLocationCollectedListener;

import android.content.Context;
import android.location.Location;
import android.media.AudioManager;

public class VolumeManager implements OnLocationCollectedListener{
	private final String PREF_VOLUME_MANAGER = "pref_volume_manager_";
	private final String PREF_STATUS = "status";
	private final String PREF_ENABLE_LOCATION = "enable_location";
	private final String PREF_ENABLE_SCHEDULE = "enable_schedule";
	private final String PREF_FREQUENCY = "frequency";
	private final String PREF_PRIORITY = "priority";
	private final String PREF_FINENESS = "fineness";

	public interface OnFinishPerformListener{
		void onFinishPerform(STATUS status);
	}

	private final PreferenceHelper pref;
	private final AudioManager audiomanager;

	private static VolumeManager instance;

	public enum STATUS {uncontrol, enable, manner, silent, auto, follow, confirming};
	public enum PRIORITY {schedulefirst, locationfirst, silentfirst, ringfirst};
	private final LocationSetting locationsetting;
	private final ScheduleSetting schedulesetting;
	private PRIORITY priority=PRIORITY.schedulefirst;
	private int frequency=1;
	private STATUS status=null;
	private STATUS sub_status=null;
	private double fineness=1.0;
	private boolean resetauto;

	public static VolumeManager getInstance(Context context){
		if(instance==null){
			instance=new VolumeManager(context, PreferenceHelper.getInstance(context));
		}
		return instance;
	}

	public VolumeManager(PreferenceHelper pref, AudioManager audiomanager, LocationSetting locationsetting, ScheduleSetting schedulesetting){
		this.pref=pref;
		this.audiomanager=audiomanager;
		this.locationsetting=locationsetting;
		this.schedulesetting=schedulesetting;
		load(pref);
	}

	private void load(PreferenceHelper pref){
		status=STATUS.valueOf(pref.getString(PREF_VOLUME_MANAGER+PREF_STATUS,STATUS.uncontrol.toString()));
		locationsetting.setEnable(pref.getBoolean(PREF_VOLUME_MANAGER+PREF_ENABLE_LOCATION, true));
		schedulesetting.setEnable(pref.getBoolean(PREF_VOLUME_MANAGER+PREF_ENABLE_SCHEDULE, true));
		this.frequency=pref.getInt(PREF_VOLUME_MANAGER+PREF_FREQUENCY, 1);
		this.priority=PRIORITY.valueOf(pref.getString(PREF_VOLUME_MANAGER+PREF_PRIORITY, PRIORITY.silentfirst.toString()));
		this.fineness=pref.getDouble(PREF_VOLUME_MANAGER+PREF_FINENESS, 1.0);
	}

	public VolumeManager(Context context, PreferenceHelper pref){
		this(pref
				,(AudioManager)context.getSystemService(Context.AUDIO_SERVICE)
				, new LocationSetting(context, pref)
				, new ScheduleSetting(context, pref));
	}

	public PRIORITY getPriority(){
		return priority;
	}

	public boolean isAuto(){
		return status==STATUS.auto;
	}

	public void setPriority(Context context, PRIORITY priority){
		this.priority=priority;
		doAuto(context);
	}

	public boolean getEnableLocation(){
		return locationsetting.getEnable();
	}

	public void setEnableLocation(boolean value){
		locationsetting.setEnable(value);
	}

	public boolean getEnableSchedule(){
		return schedulesetting.getEnable();
	}

	public void setEnableSchedule(boolean value){
		schedulesetting.setEnable(value);
	}

	public LocationSetting getLocationSettingManager(){
		return locationsetting;
	}

	public ScheduleSetting getScheduleSettingManager(){
		return schedulesetting;
	}

	public int getfrequentry(){
		return frequency;
	}

	public void setFrequency(int frequency){
		this.frequency=frequency;
	}

	public double getFineness(){
		return fineness;
	}

	public void setFineness(double fineness){
		this.fineness=fineness;
	}

	public boolean getResetAuto(){
		return resetauto;
	}

	public void setResetAuto(boolean resetauto){
		this.resetauto=resetauto;
	}

	public STATUS getStatus(){
		return status;
	}

	public STATUS getSubStatus(){
		return sub_status;
	}
/**
 * 
 * @param status it comes from device. for fail safe, it may gives up auto when status changes by outside,
 * @return true if VolumeManager gives up auto
 */
	public boolean confirm(STATUS status){
		if(resetauto && this.status==STATUS.auto && status!=sub_status){
			this.status=status;
			return true;
		}
		return false;
	}

	static STATUS convDeviceStatus(int device_status){
		switch(device_status){
		case AudioManager.RINGER_MODE_NORMAL:
			return STATUS.enable;
		case AudioManager.RINGER_MODE_VIBRATE:
			return STATUS.manner;
		case AudioManager.RINGER_MODE_SILENT:
			return STATUS.silent;
		default:
			throw new RuntimeException();	
		}
	}

	public void doAuto(Context context){
		if(isAuto()){
			if(locationsetting.getEnable()){
				new LocationHelper(context).start(false, fineness, this);
			}else{
				setStatusToDevice(sub_status=calcStatus(STATUS.uncontrol, schedulesetting.getStatus()));
			}
		}
	}

	public STATUS performByCurrentStatus(Context context){
		perform(context,status);
		return status;
	}

	public void perform(Context context, STATUS status){
		this.status=status;
		pref.putString(PREF_STATUS, status.toString());
		if(status==STATUS.auto){
			StartCheckingReciever.Start(context, getRepeatTime());
			sub_status=STATUS.confirming;
		}else{
			setStatusToDevice(status);
		}
	}

	private void setStatusToDevice(STATUS status){
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
		default:
			throw new RuntimeException();
		}
	}

	@Override
	public void onFinishLocationCollection(Location location, RESULT result) {
		if(result==RESULT.resultOK){
			setStatusToDevice(sub_status=calcStatus(locationsetting.getStatus(location), schedulesetting.getStatus()));
		}
	}

	private int getRepeatTime(){
		return 1000*60*60/frequency;
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

	private STATUS calcStatus(STATUS statusLocation, STATUS statusSchedule){
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
