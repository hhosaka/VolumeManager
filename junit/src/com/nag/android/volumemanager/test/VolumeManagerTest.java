package com.nag.android.volumemanager.test;

import android.content.Context;
import android.location.Location;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.test.InstrumentationTestCase;

import com.nag.android.util.PreferenceHelper;
import com.nag.android.volumemanager.LocationSetting;
import com.nag.android.volumemanager.ScheduleSetting;
import com.nag.android.volumemanager.VolumeManager;
import com.nag.android.volumemanager.VolumeManager.OnFinishPerformListener;
import com.nag.android.volumemanager.VolumeManager.PRIORITY;
import com.nag.android.volumemanager.VolumeManager.STATUS;

public class VolumeManagerTest extends InstrumentationTestCase implements OnFinishPerformListener{

	VolumeManager instance=null;
	private STATUS result;
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

	private Context getApplicationContext() {
		return this.getInstrumentation().getTargetContext().getApplicationContext();
	}

	@Override
	public void onFinishPerform(STATUS status) {
		result=status;
	}

	protected void setUp(){
		PreferenceHelper pref=new PreferenceHelper(getApplicationContext());
		instance=new VolumeManager(pref
		,(AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE)
		,ls=new MockLocationSetting(getApplicationContext(), pref)
		,ss=new MockScheduleSetting(getApplicationContext(), pref));
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
		instance.perform(getApplicationContext(),STATUS.auto);
		assertEquals(instance.getStatus(),STATUS.auto);
		assertEquals(instance.getSubStatus(),STATUS.uncontrol);
		instance.setPriority(getApplicationContext(), PRIORITY.schedulefirst);
		assertEquals(instance.getSubStatus(),STATUS.uncontrol);
	}

	public void testSetEnableSchedule() {
		fail("Not yet implemented");
	}

	public void testGetLocationSettingManager() {
		fail("Not yet implemented");
	}

	public void testGetScheduleSettingManager() {
		fail("Not yet implemented");
	}

	public void testGetfrequentry() {
		fail("Not yet implemented");
	}

	public void testSetFrequency() {
		fail("Not yet implemented");
	}

	public void testDoAuto() {
		fail("Not yet implemented");
	}

	public void testPerformContext() {
		fail("Not yet implemented");
	}

	public void testPerformContextSTATUS() {
		fail("Not yet implemented");
	}

	public void testOnFinishLocationCollection() {
		fail("Not yet implemented");
	}

}
