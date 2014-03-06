package com.nag.android.volumemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;

import com.nag.android.util.PreferenceHelper;
import com.nag.android.volumemanager.LocationHelper.OnLocationCollectedListener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;

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
	private OnFinishPerformListener listener=null;
	
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
//		this.frequency=pref.getInt(PREF_VOLUME_MANAGER+PREF_FREQUENCY, 1);
		this.frequency=pref.getInt(PREF_VOLUME_MANAGER+PREF_FREQUENCY, 60);// TODO test
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
		if(isAuto()){
			doAuto(context, null);
		}
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
	public boolean confirmStatusChangeFromOutside(STATUS status){
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

	private void doAuto(Context context, OnFinishPerformListener listener){
		assert(status==STATUS.auto);
		if(locationsetting.getEnable()){
			this.listener=listener;
			new LocationHelper(context).start(false, fineness, this);
		}else{
			setStatusToDevice(context, sub_status=pickStatus(STATUS.uncontrol, schedulesetting.getStatus()));
			listener.onFinishPerform(sub_status);
		}
	}

	public STATUS setStatus(Context context){
		setStatus(context, status, null);
		return status;
	}

	public void setStatus(Context context, STATUS status, OnFinishPerformListener listener){
		this.status=status;
		pref.putString(PREF_STATUS, status.toString());
		if(status==STATUS.auto){
			sub_status=STATUS.confirming;
			this.listener=listener;
			StartCheckingReciever.Start(context, getRepeatTime());
		}else{
			setStatusToDevice(context, status);
			if(listener!=null){listener.onFinishPerform(status);}
		}
	}

	private int number=0;
	private void sendNotification(Context context, STATUS status) {// TODO just for testing
		if(context!=null)
		{
			NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			Notification n = new Notification();
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:1234567890"));
			PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
			
			n.icon = R.drawable.ic_launcher;
			n.tickerText = "status changed to"+status;
			n.number = number;
			n.setLatestEventInfo(context, context.getString(R.string.app_name), "("+DateFormat.format("kk:mm:ss", Calendar.getInstance()).toString()+") status changed to "+ status, pi);
			
			manager.notify(number++, n);
		}
	}

	private void setStatusToDevice(Context context, STATUS status){
		sendNotification(context, status);
		Log.d("HDEBUG","setStatusToDevice="+status);
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
			setStatusToDevice(null, sub_status=pickStatus(locationsetting.getStatus(location), schedulesetting.getStatus()));
			if(listener!=null){listener.onFinishPerform(sub_status);}
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
