package com.nag.android.volumemanager.controls;

import com.nag.android.util.Label;
import com.nag.android.volumemanager.VolumeManager.STATUS;

public class StatusLabel extends Label<STATUS> {
	public StatusLabel(String label, STATUS value) {
		super(label, value);
	}
}
