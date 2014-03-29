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

	private OnValueChangedListener<T> listener=null;
	private String title;
	private String default_label="";
	private int index=0;
	private final ArrayList<PrimitiveLabel<T>> labels=new ArrayList<PrimitiveLabel<T>>();;

	public ButtonSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}

	public void add(PrimitiveLabel<T> label){
		labels.add(label);
	}

	public ButtonSelector<T> setTitle(String title){
		this.title=title;
		return this;
	}
	
	public ButtonSelector<T> setDefaultLabel(String default_label){
		this.default_label=default_label;;
		return this;
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
		setText(default_label);
	}

	ButtonSelector<T> getInstance(){
		return this;
	}

	@Override
	public void onClick(View v) {
		new AlertDialog.Builder(getContext()).setTitle(title).setSingleChoiceItems(labels.toArray(new CharSequence[0]),index
				, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which){
				index=which;
				if(listener!=null){
					setText(listener.OnValueChanged(getInstance(), labels.get(index).getValue()));
				}else{
					setText(labels.get(index).toString());
				}
				dialog.dismiss();
			}
		}).create().show();
	}
}
