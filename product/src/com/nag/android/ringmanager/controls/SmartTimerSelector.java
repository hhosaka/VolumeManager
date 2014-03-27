package com.nag.android.ringmanager.controls;

import java.util.Calendar;

import android.content.Context;

import android.text.format.DateFormat;
import android.util.AttributeSet;

import com.nag.android.ringmanager.R;
import com.nag.android.ringmanager.RingManager;
import com.nag.android.ringmanager.RingManager.STATUS;
import com.nag.android.ringmanager.SmartTimerReceiver;
import com.nag.android.util.ButtonSelector;

public class SmartTimerSelector extends ButtonSelector<Integer> implements com.nag.android.util.ButtonSelector.OnValueChangedListener<Integer>{

	public SmartTimerSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnValueChangedListener(this);
	}
	public void setTime(Integer hour){
		super.setValue(hour);
	}
	@Override
	public String OnValueChanged(Integer value) {
		if(value==0){
			SmartTimerReceiver.stop(getContext());
			RingManager.getInstance(getContext()).doAuto(getContext());
			return "Smart Timer";
		}
		else{
			RingManager.getInstance(getContext()).setStatus(getContext(),STATUS.silent);
			Calendar t=Calendar.getInstance();
			t.add(Calendar.MINUTE, value);
			SmartTimerReceiver.start(getContext(), t.getTimeInMillis());
			return getContext().getString(R.string.label_smart_timer_prefix)+DateFormat.format("kk:mm:ss", t).toString()+getContext().getString(R.string.label_smart_timer_postfix);
		}
	}
}
