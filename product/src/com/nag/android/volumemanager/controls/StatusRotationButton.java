package com.nag.android.volumemanager.controls;

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
	boolean isAuto=true;
	public StatusRotationButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setStatus(boolean isAuto, STATUS status){
		this.isAuto=isAuto;
		super.setValue(status);
	}
//
//	@Override
//	public String getLabel(){
//		if(isAuto){
//			return "Auto("+super.getLabel()+")";
//		}else{
//			return super.getLabel();
//		}
//	}
}
