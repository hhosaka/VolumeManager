package com.nag.android.ringmanager.controls;

import android.content.Context;
import android.util.AttributeSet;

import com.nag.android.ringmanager.RingManager.STATUS;
import com.nag.android.util.ButtonSelector;
/**
 * 
 * @author H
 * for prevent warning, I should extends the SimpleSelector
 */
public class StatusSelector extends ButtonSelector<STATUS>{//PrimitiveSelector<STATUS>{
	public StatusSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setStatus(STATUS status){
		super.setValue(status);
	}
}
