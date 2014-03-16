package com.nag.android.ringmanager;

import com.nag.android.ringmanager.LocationHelper.OnLocationCollectedListener.RESULT;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
/**
 * 
 * @author H
 *Do not recycle this class
 */
public class LocationHelper implements LocationListener{
	private static final double LIMIT_ACCURACY_4_GPS=20.0;// TODO tentative
	private static final double LIMIT_ACCURACY_4_NET=1000.0;// TODO tentative
	private static final int MAX_RETRY_COUNT=10;// TODO tentative
	private final LocationManager manager;
	private OnLocationCollectedListener listener=null;
	private int retry;
	private double limit_accuracy=0.0;

	public interface OnLocationCollectedListener{
		public enum RESULT{resultOK,resultRetryError,resultDisabled};
		void onFinishLocationCollection(Location location, RESULT result);
	}

	public LocationHelper(Context context){
		manager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	}

	synchronized public boolean start(boolean isGPSRequired, double fineness, OnLocationCollectedListener listener){
		assert(listener==null);
		retry=0;
		this.listener=listener;
		if (!startByGPS(fineness, listener) && !isGPSRequired){
			return startByNet(fineness, listener);
		}
		return false;
	}

	private boolean startByGPS(double fineness, OnLocationCollectedListener listener){
		if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			limit_accuracy=LIMIT_ACCURACY_4_GPS / fineness;
			return true;
		}
		return false;
	}

	private boolean startByNet(double fineness, OnLocationCollectedListener listener){
		if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			limit_accuracy=LIMIT_ACCURACY_4_NET / fineness;
			return true;
		}
		return false;
	}

	@Override
	public void onLocationChanged(Location location){
//		Log.d(this.toString(),"Accracy="+location.getAccuracy());
		if(location.getAccuracy()<limit_accuracy){
			manager.removeUpdates(this);
			if(listener!=null)listener.onFinishLocationCollection(location,RESULT.resultOK);
		}else{
			if(++retry>MAX_RETRY_COUNT){
				if(listener!=null)listener.onFinishLocationCollection(location,RESULT.resultRetryError);
				manager.removeUpdates(this);
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider){
		manager.removeUpdates(this);
		if(listener!=null)listener.onFinishLocationCollection(null,RESULT.resultDisabled);
	}

	@Override
	public void onProviderEnabled(String provider){
		//DoNothing
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras){
		// Do Nothing
	}
}
