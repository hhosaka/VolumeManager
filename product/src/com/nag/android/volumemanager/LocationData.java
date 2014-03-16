package com.nag.android.volumemanager;

import java.util.StringTokenizer;

import com.nag.android.volumemanager.VolumeManager.STATUS;

public class LocationData{
	public enum TYPE {typeDefault, typeProtected, typeEditable};
	private String title;
	private double latitude;
	private double longitude;
	private STATUS status;
	private TYPE type;

	LocationData(String title, double latitude, double longitude, STATUS status, TYPE type){
		this.title=title;
		this.latitude=latitude;
		this.longitude=longitude;
		this.status=status;
		this.type=type;
	}

	LocationData(String buf){
		decode(buf);
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
}
