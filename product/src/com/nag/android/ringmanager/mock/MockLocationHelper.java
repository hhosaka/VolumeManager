package com.nag.android.ringmanager.mock;

import android.content.Context;
import android.location.Location;

import com.nag.android.ringmanager.LocationHelper;
import com.nag.android.ringmanager.LocationHelper.OnLocationCollectedListener.RESULT;
import com.nag.android.ringmanager.RingManager.STATUS;

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
