package com.nag.android.util;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;;

public class ButtonSelector<T> extends Button implements OnClickListener {

	public interface OnValueChangedListener<T>{
		String OnValueChanged(T value);
	}

	private OnValueChangedListener<T> listener=null;
	private String title;
	private int index=0;
	private final ArrayList<PrimitiveLabel<T>> labels=new ArrayList<PrimitiveLabel<T>>();;

	public ButtonSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}

	public void add(PrimitiveLabel<T> label){
		labels.add(label);
	}

	public void setTitle(String title){
		this.title=title;
	}
	public void setOnValueChangedListener(OnValueChangedListener<T> listener){
		this.listener=listener;
	}

	public T getValue(){
		return labels.get(index).getValue();
	}

	public void setValue(T value){
		for(int i=0; i<labels.size(); ++i){
			if(labels.get(i).getValue()==value){
				index=i;
				setText(labels.get(i).toString());
				return;
			}
		}
		throw new RuntimeException();
	}

	@Override
	public void onClick(View v) {
		new AlertDialog.Builder(getContext()).setTitle(title).setSingleChoiceItems(labels.toArray(new CharSequence[0]),1
				, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which){
				index=which;
				if(listener!=null){
					setText(listener.OnValueChanged(labels.get(index).getValue()));
				}else{
					setText(labels.get(index).toString());
				}
				dialog.dismiss();
			}
		}).create().show();
	}
}
