package com.nag.android.util;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;;

public class RotationButton<T> extends Button implements OnClickListener {

	public interface OnValueChangedListener<T>{
		void OnValueChanged(T value);
	}

	private OnValueChangedListener<T> listener=null;
	private int index=0;
	private final ArrayList<PrimitiveLabel<T>> labels=new ArrayList<PrimitiveLabel<T>>();;

	public RotationButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}

	public void add(String lable,T value){//TODO
		labels.add(new PrimitiveLabel<T>(value));
	}

	public void setOnValueChangedListener(OnValueChangedListener<T> listener){
		this.listener=listener;
	}

	public T getValue(){
		return labels.get(index).getValue();
	}

	public void setValue(T value){
		for(PrimitiveLabel<T> label:labels)
		{
			if(label.getValue()==value){
				setText(label.toString());
				return;
			}
		}
		throw new RuntimeException();
	}

	@Override
	public void onClick(View v) {
		PrimitiveLabel<T>label=labels.get(index=++index%labels.size());
		setText(label.toString());
		if(listener!=null)listener.OnValueChanged(label.getValue());
	}
}
