package com.nag.android.ringmanager.controls;

import com.nag.android.ringmanager.RingManager.STATUS;
import com.nag.android.util.Label;

public class StatusLabel extends Label<STATUS> {
	public StatusLabel(String label, STATUS value) {
		super(label, value);
	}
}
