package com.nag.android.volumemanager.test;

import java.lang.reflect.Method;

import android.content.Context;
import android.location.Location;
import android.media.AudioManager;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.nag.android.mock.MockLocation;
import com.nag.android.util.PreferenceHelper;
import com.nag.android.volumemanager.LocationData;
import com.nag.android.volumemanager.LocationSetting;
import com.nag.android.volumemanager.VolumeManager;
import com.nag.android.volumemanager.VolumeManager.STATUS;

public class LocationSettingTest extends InstrumentationTestCase {

	private static final double lat1=35.562066;// Home
	private static final double lng1=139.442946;
	private static final double lat2=35.562299;// nebor
	private static final double lng2=139.442901;
	private static final double lat3=35.562566;// far
	private static final double lng3=139.450392;
	
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
			method = LocationSetting.class.getDeclaredMethod("distance",double.class,double.class,double.class,double.class);
			method.setAccessible(true);
			return (Double)method.invoke(instance, la1, lg1, la2, lg2);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public void testDistance(){
		assertTrue(invokeDistance(lat1, lng1, lat2, lng2)<30.0);
		assertTrue(invokeDistance(lat1, lng1, lat3, lng3)>500.0);
	}

	private LocationData invokeGetLocationData(Location location){
		Method method;
		try {
			method = LocationSetting.class.getDeclaredMethod("getLocationData",Location.class);
			method.setAccessible(true);
			return (LocationData)method.invoke(instance, location);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	private Location location1=new MockLocation(lat1,lng1,100.0f);
	private Location location2=new MockLocation(lat2,lng2,100.0f);
	private Location location3=new MockLocation(lat3,lng3,100.0f);

	public void testGetLocationData(){
		instance.addCurrentLocation(getApplicationContext(),"test1", location1);
		instance.addCurrentLocation(getApplicationContext(), "test2", location3);
		assertEquals(invokeGetLocationData(location1).getTitle(),"test1");
	}
}
