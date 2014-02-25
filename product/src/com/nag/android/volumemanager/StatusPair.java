package com.nag.android.volumemanager;

import com.nag.android.volumemanager.VolumeManager.STATUS;

class StatusPair{
	private String label;
	private STATUS status;

	public StatusPair(String label, STATUS status){
		this.label=label;
		this.status=status;
	}

	public String getLabel(){
		return label;
	}

	public String toString(){
		return label;
	}

	public STATUS getStatus(){
		return status;
	}
}
