package com.nag.android.ringmanager;

import com.nag.android.ringmanager.LocationHelper.OnLocationCollectedListener;
import com.nag.android.util.PreferenceHelper;
import com.nag.android.volumemanager.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;

public class RingManager implements OnLocationCollectedListener{
	private final String PREF_VOLUME_MANAGER = "pref_volume_manager_";
	private final String PREF_AUTO = "auto";
//	private final String PREF_STATUS = "status";
	private final String PREF_FREQUENCY = "frequency";
	private final String PREF_PRIORITY = "priority";
	private final String PREF_FINENESS = "fineness";

	private final PreferenceHelper pref;
	private final AudioManager audiomanager;
	private static RingManager instance;

	public enum STATUS {uncontrol, enable, manner, silent, auto, follow,na};
	public enum PRIORITY {schedulefirst, locationfirst, silentfirst, ringfirst};
	private final LocationSetting locationsetting;
	private final ScheduleSetting schedulesetting;
	private PRIORITY priority=PRIORITY.schedulefirst;
	private int frequency=1;
	private STATUS status=null;
//	private STATUS sub_status=null;
	private double fineness=0.0;
	private boolean auto;
	private boolean resetauto;

	private STATUS get(){
		return status;
	}

	private STATUS set(STATUS status){
		return this.status=status;
	}

	public static RingManager getInstance(Context context){
		if(instance==null){
			instance=new RingManager(context, PreferenceHelper.getInstance(context));
		}
		return instance;
	}

	//for debug
	public RingManager(PreferenceHelper pref, AudioManager audiomanager, LocationSetting locationsetting, ScheduleSetting schedulesetting){
		this.pref=pref;
		this.audiomanager=audiomanager;
		this.locationsetting=locationsetting;
		this.schedulesetting=schedulesetting;
		load();
	}

	private void load(){
		auto=pref.getBoolean(PREF_AUTO, false);
		set(this.getDeviceStatus());// TODO is is OK?
//		this.frequency=pref.getInt(PREF_VOLUME_MANAGER+PREF_FREQUENCY, 1);
		this.frequency=pref.getInt(PREF_VOLUME_MANAGER+PREF_FREQUENCY, 60);// TODO test
		this.priority=PRIORITY.valueOf(pref.getString(PREF_VOLUME_MANAGER+PREF_PRIORITY, PRIORITY.silentfirst.toString()));
		this.fineness=pref.getDouble(PREF_VOLUME_MANAGER+PREF_FINENESS, 0.5);
	}

	private void save(){
		pref.putBoolean(PREF_AUTO, auto);
		pref.putInt(PREF_VOLUME_MANAGER+PREF_FREQUENCY, frequency);
		pref.putString(PREF_VOLUME_MANAGER+PREF_PRIORITY, priority.toString());
		pref.putDouble(PREF_VOLUME_MANAGER+PREF_FINENESS, fineness);
	}

	private RingManager(Context context, PreferenceHelper pref){
		this(pref
				,(AudioManager)context.getSystemService(Context.AUDIO_SERVICE)
				, new LocationSetting(context, pref)
				, new ScheduleSetting(context, pref));
	}

	public PRIORITY getPriority(){
		return priority;
	}

	public boolean isAuto(){
		return auto;
	}

	public void setPriority(Context context, PRIORITY priority){
		this.priority=priority;
		save();
		doAuto(context);
	}

	public boolean getEnableLocation(){
		return locationsetting.getEnable();
	}

	public void setEnableLocation(boolean value){
		locationsetting.setEnable(value);
		save();
	}

	public boolean getEnableSchedule(){
		return schedulesetting.getEnable();
	}

	public void setEnableSchedule(boolean value){
		schedulesetting.setEnable(value);
		save();
	}

	public LocationSetting getLocationSetting(){
		return locationsetting;
	}

	public ScheduleSetting getScheduleSetting(){
		return schedulesetting;
	}

	public int getfrequentry(){
		return frequency;
	}

	public void setFrequency(int frequency){
		this.frequency=frequency;
		save();
	}

	public double getFineness(){
		return fineness;
	}

	public void setFineness(double fineness){
		this.fineness=fineness;
		save();
	}

	public boolean getResetAuto(){
		return resetauto;
	}

	public void setResetAuto(boolean resetauto){
		this.resetauto=resetauto;
		save();
	}

	public STATUS getStatus(){
		return get();
	}

//	public STATUS getSubStatus(){
//		return sub_status;
//	}
/**
 * 
 * @param status it comes from device. for fail safe, it may gives up auto when status changes by outside,
 * @return true if VolumeManager gives up auto
 */
	public boolean confirmStatusChangeFromOutside(STATUS status){
		if(resetauto && auto && get()!=status){
			auto=false;
			set(status);
			return true;
		}
		return false;
	}

	static STATUS convParam2Status(int device_status){
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

	private static int convStatus2Param(STATUS status){
		switch(status){
		case enable:
			return AudioManager.RINGER_MODE_NORMAL;
		case manner:
			return AudioManager.RINGER_MODE_VIBRATE;
		case silent:
			return AudioManager.RINGER_MODE_SILENT;
		case uncontrol:
		case auto:
		default:
			throw new RuntimeException();
		}
	}

	@Override
	public void onFinishLocationCollection(Location location, RESULT result) {
		if(result==RESULT.resultOK){
			resolveStatus(null, set(pickStatus(locationsetting.getStatus(location), schedulesetting.getStatus())));
		}
	}

	public void setStatus(Context context){
		setStatus(context, get());
	}

	public void setStatus(Context context, STATUS status){
		set(status);
		if(status==STATUS.auto){
			auto=true;
			StatusRefreshReciever.Start(context, getRepeatTime());
		}else{
			StatusRefreshReciever.Stop(context);
			auto=false;
			resolveStatus(context, get());
		}
	}

	void doAuto(Context context){//TODO lock
		if(isAuto()){
			if(locationsetting.getEnable()){
				new LocationHelper(context).start(false, fineness, this);
			}else{
				resolveStatus(context, set(pickStatus(STATUS.uncontrol, schedulesetting.getStatus())));
			}
		}
	}

	private STATUS getDeviceStatus(){
		return convParam2Status(audiomanager.getRingerMode());
	}

	public void resolveStatus(Context context, STATUS status){
		switch(status)
		{
		case uncontrol:
			break;
		case enable:
		case manner:
		case silent:
			if(get()!=status){
				set(status);
				sendNotification(context, get());
				audiomanager.setRingerMode(convStatus2Param(get()));
			}
		}
	}

	private void sendNotification(Context context, STATUS status) {// TODO just for testing
		if(context!=null)
		{
			NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notificationn = new Notification();
			Intent intent = new Intent(context, MainActivity.class);
			
			notificationn.icon = R.drawable.ic_launcher;
			notificationn.tickerText = "status changed to "+get();
			notificationn.number = 0;
			notificationn.setLatestEventInfo(context, context.getString(R.string.app_name), "application changed to "+ get(), PendingIntent.getActivity(context, 0, intent, 0));
			
			manager.notify(0, notificationn);
		}
	}

	private int getRepeatTime(){
		return 1000*60*60/frequency;
	}

	private STATUS pickMoreSilentStatus(STATUS st1, STATUS st2){
		assert(st1!=STATUS.uncontrol);
		assert(st2!=STATUS.uncontrol);
		switch(st1){
		case silent:
			return st1;
		case manner:
			if(st2==STATUS.silent){
				return st2;
			}else{
				return st1;
			}
		case enable:
			return st2;
		default:
			throw new RuntimeException();
		}
	}

	private STATUS pickModeRingStatus(STATUS st1, STATUS st2){
		assert(st1!=STATUS.uncontrol);
		assert(st2!=STATUS.uncontrol);
		switch(st1){
		case silent:
			return st2;
		case manner:
			if(st2==STATUS.silent){
				return st1;
			}else{
				return st2;
			}
		case enable:
			return st1;
		default:
			throw new RuntimeException();
		}
	}

	private STATUS pickStatus(STATUS statusLocation, STATUS statusSchedule){
		assert(statusLocation==STATUS.enable||statusLocation==STATUS.manner||statusLocation==STATUS.silent||statusLocation==STATUS.uncontrol);
		assert(statusSchedule==STATUS.enable||statusSchedule==STATUS.manner||statusSchedule==STATUS.silent||statusSchedule==STATUS.uncontrol);
		if(statusLocation==STATUS.uncontrol){
			return statusSchedule;
		}else if(statusSchedule==STATUS.uncontrol){
			return statusLocation;
		}else{
			switch(priority){
			case schedulefirst:
				return statusSchedule;
			case locationfirst:
				return statusLocation;
			case silentfirst:
				return pickMoreSilentStatus(statusLocation, statusSchedule);
			case ringfirst:
				return pickModeRingStatus(statusLocation, statusSchedule);
			default:
				throw new RuntimeException();
			}
		}
	}
	
//	void log(STATUS status)
//	{
//		PrintStream out;
//		try {
//			out = new PrintStream(new FileOutputStream("log.txt", true));
//			out.println(DateFormat.format("kk:mm:ss", Calendar.getInstance()).toString()+","+status);
//			out.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//	}
}
