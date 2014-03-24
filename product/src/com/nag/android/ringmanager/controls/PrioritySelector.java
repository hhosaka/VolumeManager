package com.nag.android.ringmanager.controls;

import android.content.Context;
import android.util.AttributeSet;

import com.nag.android.ringmanager.RingManager.PRIORITY;
import com.nag.android.ringmanager.RingManager.STATUS;
import com.nag.android.util.PrimitiveSelector;
/**
 * 
 * @author H
 * for prevent warning, I should extends the SimpleSelector
 */
public class PrioritySelector extends PrimitiveSelector<PRIORITY>{
	public PrioritySelector(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setPriority(PRIORITY priority){
		super.setValue(priority);
	}
}
