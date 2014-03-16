package com.nag.android.util;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class PrimitiveSelector<T> extends Spinner {

	public interface OnSelectedListener<T>{
		void OnSelected(View parent, T status);
	}

	private OnSelectedListener<T> listener=null;
//	private int index=0;
	private final ArrayList<PrimitiveLabel<T>> labels=new ArrayList<PrimitiveLabel<T>>();;
	private ArrayAdapter<PrimitiveLabel<T>> adapter=null;

	public PrimitiveSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		adapter=new ArrayAdapter<PrimitiveLabel<T>>(context, android.R.layout.simple_spinner_dropdown_item, labels);
		setAdapter(adapter);
		setOnItemSelectedListener(new OnItemSelectedListener() { 
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//				if(listener!=null)listener.OnSelected(Selector.this, ((Label<T>)((Spinner)parent).getSelectedItem()).getValue());
				if(listener!=null)listener.OnSelected(PrimitiveSelector.this, labels.get(position).getValue());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do Nothing
			} 
		});
	}

	public void add(PrimitiveLabel<T> label){
		labels.add(label);
		adapter.notifyDataSetChanged();
	}

	private int getIndex(T value){
//		return labels.indexOf(value);
		for(int i=0;i<labels.size();++i){
			if(value==labels.get(i).getValue()) return i;
		}
		throw new RuntimeException();
	}

	public void setStatus(T status){
		setSelection(getIndex(status));
		adapter.notifyDataSetChanged();// is it safety??
	}

	public T getStatus(){
		return labels.get(getSelectedItemPosition()).getValue();
	}

	public void setOnSelectedListener(OnSelectedListener<T> listener){
		this.listener=listener;
	}
}
