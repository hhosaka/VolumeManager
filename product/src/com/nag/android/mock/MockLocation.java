package com.nag.android.mock;

import android.location.Location;

public class MockLocation extends Location
{
	double latitude;
	double longitude;
	float accuracy;

	public MockLocation(double latitude, double longitude, float accuracy) {
		super((Location)null);
		this.latitude=latitude;
		this.longitude=longitude;
		this.accuracy=accuracy;
	}
	
	public double getLatitude(){return latitude;}
	public double getLongitude(){return longitude;}
	public float getAccuracy(){return accuracy;}
}
