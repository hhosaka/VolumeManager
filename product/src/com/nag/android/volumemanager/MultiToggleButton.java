package com.nag.android.volumemanager;

import android.content.Context;
import android.widget.Button;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

public class MultiToggleButton extends Button implements OnClickListener {
	interface OnClickMultiToggleButtonListener{
		void onClckMultiToggleButton(int cur);
	}

	class MultiButtonInfo{
		private String label;

		MultiButtonInfo(String label){
			this.label=label;
		}

		String getLabel(){
			return label;
		}
	}
	private int cur=0;
	private OnClickMultiToggleButtonListener listener=null;
	private final MultiButtonInfo[] info={//TODO
			new MultiButtonInfo("enable")
			,new MultiButtonInfo("manner")
			,new MultiButtonInfo("silent")
			,new MultiButtonInfo("auto")
		};

	public MultiToggleButton(Context context){
		super(context);
		setText(info[cur].getLabel());
		setOnClickListener(this);
	}

	public MultiToggleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setText(info[cur].getLabel());
		setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		cur=++cur%info.length;
		setText(info[cur].getLabel());
		if(listener!=null){
			listener.onClckMultiToggleButton(cur);
		}
	}
	
	public int getCur(){
		return cur;
	}

	public void setListener(OnClickMultiToggleButtonListener listener){
		this.listener=listener;
	}
}
