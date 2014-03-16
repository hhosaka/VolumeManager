package com.nag.android.volumemanager.mock;

import android.content.Context;
import android.location.Location;

import com.nag.android.volumemanager.LocationHelper;
import com.nag.android.volumemanager.LocationHelper.OnLocationCollectedListener.RESULT;
import com.nag.android.volumemanager.VolumeManager.STATUS;

public class MockLocationHelper extends LocationHelper {

	private RESULT result;
	private Location location;

	public MockLocationHelper(Context context, Location location, RESULT result) {
		super(context);
		this.location=location;
		this.result=result;
	}
	public boolean start(boolean isGPSRequired, double fineness, OnLocationCollectedListener listener)
	{
		listener.onFinishLocationCollection(location, result);
		return true;
	}
	

}
