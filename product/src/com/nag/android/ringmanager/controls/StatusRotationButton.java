package com.nag.android.ringmanager.controls;

import android.content.Context;
import android.util.AttributeSet;

import com.nag.android.ringmanager.RingManager.STATUS;
import com.nag.android.util.RotationButton;
/**
 * 
 * @author H
 * for prevent warning, I should extends the SimpleSelector
 */
public class StatusRotationButton extends RotationButton<STATUS>{
	public StatusRotationButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
}
