package com.nag.android.ringmanager;

import com.nag.android.ringmanager.LocationHelper.OnLocationCollectedListener;
import com.nag.android.util.PreferenceHelper;
import com.nag.android.ringmanager.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;

public class RingManager implements OnLocationCollectedListener{
	private static final int MAX_COUNT = 10;
	private static final String PREF_RING_MANAGER = "pref_ring_manager_";
	private static final String PREF_AUTO = "auto";
//	private final String PREF_STATUS = "status";
	private static final String PREF_FREQUENCY = "frequency";
	private static final String PREF_PRIORITY = "priority";
	private static final String PREF_MAX_AREA_4_ENTRY = "entry";
	private static final String PREF_MAX_AREA_4_CHECKING = "checking";
	public static final String RING_MANAGER_STATUS_CHANGED="ring manager status changed";

	private final PreferenceHelper pref;
	private final AudioManager audiomanager;
	private static RingManager instance;

	public enum STATUS {uncontrol, enable, manner, silent, auto, follow,na};
	public enum PRIORITY {schedulefirst, locationfirst, silentfirst, ringfirst};
	private final LocationSetting locationsetting;
	private final ScheduleSetting schedulesetting;
	private PRIORITY priority=PRIORITY.schedulefirst;
	private Context context;
	private int frequency=1;
	private STATUS status=null;
//	private STATUS sub_status=null;
	private double max_area_4_checking;
	private double max_area_4_entry;
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

	private RingManager(Context context, PreferenceHelper pref){
		this(context, pref
				,(AudioManager)context.getSystemService(Context.AUDIO_SERVICE)
				, new LocationSetting(context, pref)
				, new ScheduleSetting(context, pref));
	}

	public PRIORITY getPriority(){
		return priority;
	}

	//for debug
	public RingManager(Context context, PreferenceHelper pref, AudioManager audiomanager, LocationSetting locationsetting, ScheduleSetting schedulesetting){
		this.context=context;
		this.pref=pref;
		this.audiomanager=audiomanager;
		this.locationsetting=locationsetting;
		this.schedulesetting=schedulesetting;
		load();
	}

	private void load(){
		auto=pref.getBoolean(PREF_AUTO, false);
		set(this.getDeviceStatus());
		this.frequency=pref.getInt(PREF_RING_MANAGER+PREF_FREQUENCY, 1);// TODO test
		this.priority=PRIORITY.valueOf(pref.getString(PREF_RING_MANAGER+PREF_PRIORITY, PRIORITY.silentfirst.toString()));
		this.max_area_4_checking=pref.getDouble(PREF_RING_MANAGER+PREF_MAX_AREA_4_CHECKING, 1500.0);
		this.max_area_4_entry=pref.getDouble(PREF_RING_MANAGER+PREF_MAX_AREA_4_ENTRY, 1500.0);
	}

	private void save(){
		pref.putBoolean(PREF_AUTO, auto);
		pref.putInt(PREF_RING_MANAGER+PREF_FREQUENCY, frequency);
		pref.putString(PREF_RING_MANAGER+PREF_PRIORITY, priority.toString());
		pref.putDouble(PREF_RING_MANAGER+PREF_MAX_AREA_4_CHECKING, max_area_4_checking);
		pref.putDouble(PREF_RING_MANAGER+PREF_MAX_AREA_4_ENTRY, max_area_4_entry);
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

	public int getfrequency(){
		return frequency;
	}

	public void setFrequency(int frequency){
		this.frequency=frequency;
		save();
	}

	public double getMaxArea4Checking(){
		return max_area_4_checking;
	}

	public void setMaxArea4Checking(double max_accuracy){
		this.max_area_4_checking=max_accuracy;
		save();
	}

	public double getMaxArea4Entry(){
		return max_area_4_entry;
	}

	public void setMaxArea4Entry(double max_accuracy){
		this.max_area_4_checking=max_area_4_entry;
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
 * @return true if ringManager gives up auto
 */
	synchronized public STATUS confirm(STATUS status){
		set(status);
		if(auto){
			if(resetauto && get()!=status){
				auto=false;
				return status;
			}else{
				return STATUS.auto;
			}
		}else{
			return status;
		}
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
			resolveStatus(context, pickStatus(locationsetting.getStatus(location), schedulesetting.getStatus()));
		}
	}

	public void setStatus(Context context){
		setStatus(context, get());
	}

	public void setStatus(Context context, STATUS status){
		if(status==STATUS.auto){
			auto=true;
			set(STATUS.auto);
			AutoReceiver.Start(context, getRepeatTime());
		}else{
			auto=false;
			AutoReceiver.Stop(context);
			resolveStatus(context, status);
		}
	}

	public void doAuto(Context context){//TODO lock
		if(isAuto()){
			if(locationsetting.getEnable()){
				new LocationHelper(context).start(false, max_area_4_checking, MAX_COUNT, this);
			}else{
				resolveStatus(context, pickStatus(STATUS.uncontrol, schedulesetting.getStatus()));
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
				audiomanager.setRingerMode(convStatus2Param(status));
			}else{
				context.sendBroadcast(new Intent(RING_MANAGER_STATUS_CHANGED));
			}
			break;
		default:
			throw new RuntimeException();
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
