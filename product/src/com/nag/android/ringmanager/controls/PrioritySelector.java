package com.nag.android.ringmanager.controls;

import android.content.Context;
import android.util.AttributeSet;

import com.nag.android.ringmanager.RingManager.PRIORITY;
import com.nag.android.util.ButtonSelector;
import com.nag.android.util.OnValueChangedListener;
/**
 * 
 * @author H
 * for prevent warning, I should extends the SimpleSelector
 */
public class PrioritySelector extends ButtonSelector<PRIORITY>{
	public PrioritySelector(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setPriority(PRIORITY priority){
		super.setValue(priority);
	}

	public void setOnValueChangedListener(OnValueChangedListener<PRIORITY> listener){
		super.setOnValueChangedListener(listener);
	}
}
