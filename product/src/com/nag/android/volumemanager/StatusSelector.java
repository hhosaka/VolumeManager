package com.nag.android.volumemanager;

import android.content.Context;
import android.util.AttributeSet;

import com.nag.android.util.SimpleSelector;
import com.nag.android.volumemanager.VolumeManager.STATUS;
/**
 * 
 * @author H
 * for prevent warning, I should extends the SimpleSelector
 */
class StatusSelector extends SimpleSelector<STATUS>{
	public StatusSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
}
