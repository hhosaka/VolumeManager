package com.nag.android.volumemanager.test;

import java.lang.reflect.Method;

import android.content.Context;
import android.media.AudioManager;
import android.test.InstrumentationTestCase;

import com.nag.android.util.PreferenceHelper;
import com.nag.android.volumemanager.LocationSetting;
import com.nag.android.volumemanager.VolumeManager;
import com.nag.android.volumemanager.VolumeManager.STATUS;
import com.nag.android.volumemanager.test.VolumeManagerTest.MockLocationSetting;
import com.nag.android.volumemanager.test.VolumeManagerTest.MockScheduleSetting;

import junit.framework.TestCase;

public class LocationSettingTest extends InstrumentationTestCase {

	LocationSetting instance;

	private Context getApplicationContext() {
		return this.getInstrumentation().getTargetContext().getApplicationContext();
	}

	protected void setUp(){
		instance=new LocationSetting(getApplicationContext(), new PreferenceHelper(getApplicationContext()));
	}

	double invokeDistance(double la1, double lg1, double la2, double lg2){
		Method method;
		try {
			method = VolumeManager.class.getDeclaredMethod("distance", STATUS.class,STATUS.class);
			method.setAccessible(true);
			return (Double)method.invoke(instance, la1, lg1, la2, lg2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	void testDistance(){
		assertTrue(invokeDistance(0.0, 0.0, 0.0, 0.0)<100.0);
		assertTrue(invokeDistance(0.0, 0.0, 0.0, 0.0)>100.0);
	}
}
