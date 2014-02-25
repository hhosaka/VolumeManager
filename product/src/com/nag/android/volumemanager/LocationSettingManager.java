package com.nag.android.volumemanager;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.nag.android.volumemanager.VolumeManager.STATUS;

class LocationSettingManager{
	private static LocationSettingManager instance;
	public static LocationSettingManager getInstance(Context context){
		if(instance==null){
			instance=new LocationSettingManager(context, new PreferenceHelper(context));
		}
		return instance;
	}
	public interface OnLocationAddedListener{
		void OnLocationAdded(Location location);
	}

	public interface OnFinishLocationCheckingListener{
		void OnFinishLocationChecking(STATUS status);
	}
	private static final double MAX_LOCATION=32;
	private static final String PREF_LOCATION="location_";

	 static class LocationData{
		public enum TYPE {typeDefault, typeProtected, typeEditable};
		LocationData(String buf){
			decode(buf);
		}

		LocationData(String title, double latitude, double longitude, STATUS status, TYPE type){
			this.title=title;
			this.latitude=latitude;
			this.longitude=longitude;
			this.status=status;
			this.type=type;
		}

		public String toString(){
			return title+"("+status.toString()+")";
		}

		public String encode(){
			return title+","+String.valueOf(latitude)+","+String.valueOf(longitude)+","+status.toString()+","+type.toString();
		}

		public void decode(String buf){
			StringTokenizer token=new StringTokenizer(buf,",");
			title=token.nextToken();
			latitude=Double.valueOf(token.nextToken());
			longitude=Double.valueOf(token.nextToken());
			status=STATUS.valueOf(token.nextToken());
			type=TYPE.valueOf(token.nextToken());
		}

		public String getTitle(){return title;}
		public double getLatitude(){return latitude;}
		public double getLongitude(){return longitude;}
		public STATUS getStatus(){return status;}
		public TYPE getType(){return type;}

		public void setTitle(String title){
			this.title=title;
		}
		public void setStatus(STATUS status){
			this.status=status;
		}

		private String title;
		private double latitude;
		private double longitude;
		private STATUS status;
		private TYPE type;
	}

	private final ArrayList<LocationData> locations=new ArrayList<LocationData>();
	private final PreferenceHelper pref;
	private final LocationData locationDefault;;
	private boolean enable=true;

	LocationSettingManager(Context context, PreferenceHelper pref){
		this.pref=pref;
		this.locationDefault=new LocationData(context.getString(R.string.location_default), 0.0, 0.0, STATUS.uncontrol, LocationData.TYPE.typeDefault);
		loadAll(context);
	}

	public void setEnable(boolean value){
		enable=value;
	}

	public boolean hasSpace(){
		return locations.size()<MAX_LOCATION;
	}

	public int addCurrentLocation(Context context, Location location){
		if(locations.size()<MAX_LOCATION){
			if(getLocationData(location)==locationDefault){
				LocationData data=new LocationData(DateFormat.format("yyyy/MM/dd kk:mm:ss", Calendar.getInstance()).toString(), location.getLatitude(), location.getLongitude(),STATUS.uncontrol,LocationData.TYPE.typeEditable );
				locations.add(data);
				save(data,locations.size()-1);
				return locations.size()-1;
			}
		}
		return -1;
	}

	public ArrayList<LocationData> getLocationData(){
		return locations;
	}

	public void edit(Context context, int index, String title, STATUS status){
		LocationData ld=locations.get(index);
		ld.setTitle(title);
		ld.setStatus(status);
		save(ld,index);
	}

	public void remove(Context context, int index){
		locations.remove(index);
		saveAll(context);
	}


	public boolean checkAcceptableString(String str){
		return str.indexOf(',')!=-1;
	}

	private void save(LocationData data, int index){
		if(index>0){
			pref.putString(PREF_LOCATION+String.valueOf(index), data.encode());
		}
	}

	private LocationData load(Context context, int index){
		if(index==0){
			return locationDefault;
		}else{
			String buf=PreferenceHelper.getInstance(context).getString(PREF_LOCATION+String.valueOf(index),null);
			if(buf==null){
				return null;
			}else{
				return new LocationData(buf);
			}
		}
	}

	void saveAll(Context context){
		int i=0;
		for(LocationData location:locations){
			save(location, i++);
		}
		pref.remove(PREF_LOCATION+String.valueOf(i));
	}

	public void loadAll(Context context){
		locations.clear();
		int index=0;
		LocationData locationdata;
		while((locationdata=load(context,index++))!=null){
			locations.add(locationdata);
		}
		if(locations.size()==0){
			locations.add(locationDefault);
		}
	}

	public STATUS getStatus(Location location){
		if(enable){
			return getLocationData(location).getStatus();
		}else{
			return STATUS.uncontrol;
		}
	}

	private LocationData getLocationData(Location location){
		for(LocationData loc:locations){
			if(loc.getType()!=LocationData.TYPE.typeDefault
					&&isInArea(location.getLatitude(), location.getLongitude(), loc.latitude, loc.longitude, location.getAccuracy())){
				return loc;
			}
		}
		return locationDefault;
	}

	private boolean isInArea(double lat1, double lon1, double lat2, double lon2, float accuracy){
		return distance(lat1, lon1, lat2, lon2)<accuracy;
	}

	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = sin(deg2rad(lat1)) * sin(deg2rad(lat2)) +  cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * cos(deg2rad(theta));
		dist = acos(dist);
		dist = rad2deg(dist);

		return ((dist * 60 * 1.1515) * 1609.344);
	}

	private double rad2deg(double radian) {
		return radian * (180f / Math.PI);
	}

	private static double deg2rad(double degrees) {
		return degrees * (Math.PI / 180f);
	}
}