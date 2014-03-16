package com.nag.android.volumemanager.test;

import java.lang.reflect.Method;

import android.content.Context;
import android.location.Location;
import android.media.AudioManager;
import android.test.InstrumentationTestCase;

import com.nag.android.util.PreferenceHelper;
import com.nag.android.volumemanager.LocationSetting;
import com.nag.android.volumemanager.ScheduleSetting;
import com.nag.android.volumemanager.VolumeManager;
import com.nag.android.volumemanager.VolumeManager.PRIORITY;
import com.nag.android.volumemanager.VolumeManager.STATUS;

public class VolumeManagerTest extends InstrumentationTestCase{

	VolumeManager instance=null;
	private STATUS status_result;
	private MockLocationSetting ls;
	private MockScheduleSetting ss;
	class MockLocationSetting extends LocationSetting{
		private STATUS status;

		MockLocationSetting(Context context, PreferenceHelper pref) {
			super(context, pref);
		}

		void setStatus(STATUS status){
			this.status=status;
		}

		public STATUS getStatus(Location location){
			return status;
		}

	}

	class MockScheduleSetting extends ScheduleSetting{
		private STATUS status;

		MockScheduleSetting(Context context, PreferenceHelper pref) {
			super(context, pref);
		}

		void setStatus(STATUS status){
			this.status=status;
		}

		public STATUS getStatus(Location location){
			return status;
		}

	}

	protected void setUp(){
		PreferenceHelper pref=new PreferenceHelper(getApplicationContext());
		instance=new VolumeManager(pref
		,(AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE)
		,ls=new MockLocationSetting(getApplicationContext(), pref)
		,ss=new MockScheduleSetting(getApplicationContext(), pref));
	}

	private Context getApplicationContext() {
		return this.getInstrumentation().getTargetContext().getApplicationContext();
	}

	public void testGetPriority() {
		instance.setPriority(getApplicationContext(), PRIORITY.locationfirst);
		assertEquals(instance.getPriority(), PRIORITY.locationfirst);
	}

	public void testSetPriority() {
		instance.setPriority(getApplicationContext(), PRIORITY.locationfirst);
		assertEquals(instance.getPriority(),PRIORITY.locationfirst);
	}

	public void testSetEnableLocation() {
		ls.setStatus(STATUS.uncontrol);
		ss.setStatus(STATUS.enable);
		instance.setPriority(getApplicationContext(), PRIORITY.locationfirst);
		instance.setEnableLocation(true);
//		instance.setStatus(getApplicationContext(),STATUS.auto, this);//TODO atode
//		assertEquals(instance.getStatus(),STATUS.auto);
//		assertEquals(instance.getSubStatus(),STATUS.uncontrol);
//		instance.setEnableLocation(false);
//		assertEquals(instance.getSubStatus(),STATUS.enable);
	}

	public void testSetEnableSchedule() {
		ls.setStatus(STATUS.enable);
		ss.setStatus(STATUS.uncontrol);
		instance.setPriority(getApplicationContext(), PRIORITY.schedulefirst);
		instance.setEnableSchedule(true);
//		instance.setStatus(getApplicationContext(),STATUS.auto, this);// TODO
//		assertEquals(instance.getStatus(),STATUS.uncontrol);
//		assertEquals(instance.getSubStatus(),STATUS.uncontrol);
//		instance.setEnableSchedule(false);
//		assertEquals(instance.getSubStatus(),STATUS.enable);
	}

	public void testGetLocationSetting() {
		assertEquals(instance.getLocationSetting(),ls);
	}

	public void testGetScheduleSetting() {
		assertEquals(instance.getScheduleSetting(),ss);
	}

	public void testGetfrequency() {
		instance.setFrequency(1);
		assertEquals(instance.getfrequentry(),1);
		instance.setFrequency(10);
		assertEquals(instance.getfrequentry(),10);
	}

	STATUS invokeDoAuto(Context context){
		Method method;
		try {
			method = VolumeManager.class.getDeclaredMethod("doAuto",Context.class);
			method.setAccessible(true);
			return (STATUS)method.invoke(instance, context);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public void testDoAuto() {
		ls.setStatus(STATUS.enable);
		ss.setStatus(STATUS.manner);
		instance.setEnableLocation(false);
		instance.setEnableSchedule(false);
		instance.setPriority(getApplicationContext(), PRIORITY.locationfirst);
		instance.setStatus(getApplicationContext(), STATUS.silent);
		assertEquals(status_result, STATUS.silent);
		invokeDoAuto(getApplicationContext());
		assertEquals(status_result, STATUS.uncontrol);

		invokeDoAuto(getApplicationContext());
		assertEquals(status_result, STATUS.uncontrol);

		instance.setEnableSchedule(true);
//		instance.doAuto(getApplicationContext(),this);// TODO
//		assertEquals(status_result, STATUS.manner);

//		instance.setEnableLocation(true);
//		instance.doAuto(getApplicationContext(),this);
//		assertEquals(status_result, STATUS.enable);
}

	public void testPerformContext() {
		ls.setStatus(STATUS.enable);
		ss.setStatus(STATUS.manner);
		instance.setPriority(getApplicationContext(), PRIORITY.locationfirst);
		instance.setEnableLocation(false);
		instance.setEnableSchedule(false);
		invokeDoAuto(getApplicationContext());
		assertEquals(status_result, STATUS.uncontrol);
	}

	public void testPerformContextSTATUS() {
		instance.setStatus(getApplicationContext(), STATUS.enable);
		assertTrue(true);// TODO : how should I?
	}

	STATUS invokeCalcStatus(STATUS st1, STATUS st2){
		Method method;
		try {
			method = VolumeManager.class.getDeclaredMethod("pickStatus", STATUS.class,STATUS.class);
			method.setAccessible(true);
			return (STATUS)method.invoke(instance, st1, st2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	public void testCalcStatus(){

		instance.setPriority(getApplicationContext(), PRIORITY.locationfirst);
		assertEquals(invokeCalcStatus(STATUS.enable, STATUS.enable),STATUS.enable);
		assertEquals(invokeCalcStatus(STATUS.manner, STATUS.enable),STATUS.manner);
		assertEquals(invokeCalcStatus(STATUS.silent, STATUS.enable),STATUS.silent);
		assertEquals(invokeCalcStatus(STATUS.uncontrol, STATUS.enable),STATUS.enable);
		assertEquals(invokeCalcStatus(STATUS.enable, STATUS.manner),STATUS.enable);
		assertEquals(invokeCalcStatus(STATUS.manner, STATUS.manner),STATUS.manner);
		assertEquals(invokeCalcStatus(STATUS.silent, STATUS.manner),STATUS.silent);
		assertEquals(invokeCalcStatus(STATUS.uncontrol, STATUS.manner),STATUS.manner);
		
		assertEquals(invokeCalcStatus(STATUS.enable, STATUS.silent),STATUS.enable);
		assertEquals(invokeCalcStatus(STATUS.manner, STATUS.silent),STATUS.manner);
		assertEquals(invokeCalcStatus(STATUS.silent, STATUS.silent),STATUS.silent);
		assertEquals(invokeCalcStatus(STATUS.uncontrol, STATUS.silent),STATUS.silent);

		instance.setPriority(getApplicationContext(), PRIORITY.schedulefirst);
		assertEquals(invokeCalcStatus(STATUS.enable, STATUS.silent),STATUS.silent);
		assertEquals(invokeCalcStatus(STATUS.manner, STATUS.silent),STATUS.silent);
		assertEquals(invokeCalcStatus(STATUS.silent, STATUS.silent),STATUS.silent);
		assertEquals(invokeCalcStatus(STATUS.enable, STATUS.uncontrol),STATUS.enable);

		//		ls.setStatus(STATUS.uncontrol);
//		ss.setStatus(STATUS.enable);
//		instance.setPriority(getApplicationContext(), PRIORITY.locationfirst);
//		instance.setEnableLocation(true);
//		instance.perform(getApplicationContext(),STATUS.auto);
//		assertEquals(instance.getStatus(),STATUS.auto);
//		assertEquals(instance.getSubStatus(),STATUS.uncontrol);
//		instance.setPriority(getApplicationContext(), PRIORITY.schedulefirst);
//		assertEquals(instance.getSubStatus(),STATUS.uncontrol);
	}

}
