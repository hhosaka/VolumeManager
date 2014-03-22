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
	private final LocationManager manager;
	private OnLocationCollectedListener listener=null;
	private int max_count;
	private double limit_accuracy=1000.0;

	public interface OnLocationCollectedListener{
		public enum RESULT{resultOK,resultRetryError,resultDisabled};
		void onFinishLocationCollection(Location location, RESULT result);
	}

	public LocationHelper(Context context){
		manager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	}

	synchronized public boolean start(boolean isGPSRequired, double limit_accuracy, int max_count, OnLocationCollectedListener listener){
		assert(listener==null);
		this.max_count=max_count;
		this.listener=listener;
		this.limit_accuracy=limit_accuracy;
		if (!startByGPS(listener) && !isGPSRequired){
			return startByNet(listener);
		}
		return false;
	}

	public void stop(){
		manager.removeUpdates(this);
	}

	private boolean startByGPS(OnLocationCollectedListener listener){
		if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
			return true;
		}
		return false;
	}

	private boolean startByNet(OnLocationCollectedListener listener){
		if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
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
			if(--max_count>0){
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
