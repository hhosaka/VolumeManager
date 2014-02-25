package com.nag.android.volumemanager;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.nag.android.volumemanager.LocationCollector.OnFinishLocationCollectionListener.RESULT;
import com.nag.android.volumemanager.LocationSettingManager.LocationData;
import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationCollector implements LocationListener{
	private static final double ACCEPTABLE_ACCURACY=20.0;
	private static final int MAX_RETRY_COUNT=10;
	private static LocationCollector instance;
	private final LocationManager manager;
	private OnFinishLocationCollectionListener listener;
	private int retry;

	public interface OnFinishLocationCollectionListener{
		enum RESULT{resultOK,resultRetryError,resultDisabled};
		void onFinishLocationCollection(Location location, RESULT result);
	}
	public static LocationCollector getInstance(Context context){
		if(instance==null){
			instance=new LocationCollector(context);
		}
		return instance;
	}

	private LocationCollector(Context context){
		manager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
	}

	public boolean start(OnFinishLocationCollectionListener listener){
		// TODO need lock?
		retry=0;
		this.listener=listener;
		if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		}else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}else{
			return false;
		}
		return true;
	}

	@Override
	public void onLocationChanged(Location location){
		Log.d(this.toString(),"Accracy="+location.getAccuracy());
		if(location.getAccuracy()<ACCEPTABLE_ACCURACY){
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm");
//			locations.add(data);
//			save(data,locations.size()-1);
			manager.removeUpdates(this);
			if(listener!=null)listener.onFinishLocationCollection(location,RESULT.resultOK);
//			.onLocationCollected(new LocationData(sdf.format(new Date()), location.getLatitude(), location.getLongitude(),STATUS.uncontrol,LocationData.TYPE.typeEditable ));
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
