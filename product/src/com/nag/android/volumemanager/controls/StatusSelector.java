package com.nag.android.volumemanager.controls;

import android.content.Context;
import android.util.AttributeSet;

import com.nag.android.util.PrimitiveSelector;
import com.nag.android.volumemanager.VolumeManager.STATUS;
/**
 * 
 * @author H
 * for prevent warning, I should extends the SimpleSelector
 */
public class StatusSelector extends PrimitiveSelector<STATUS>{
	public StatusSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
}
