package com.nag.android.util;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SimpleSelector<T> extends Spinner {

	public interface OnStatusSelectedListener{
		void OnSelected(int index, Object status);
	}

	private OnStatusSelectedListener listener=null;
	private int index=0;
	private final ArrayList<Pair<T>> statuspairs=new ArrayList<Pair<T>>();;
	private ArrayAdapter<Pair<T>> adapter=null;

	private int getIndex(T status){
		for(int i=0;i<statuspairs.size();++i){
			if(status==statuspairs.get(i).getValue()) return i;
		}
		throw new RuntimeException();
	}

	public void add(String title,T status){
		statuspairs.add(new Pair<T>(title,status));
		adapter.notifyDataSetChanged();
	}

	public SimpleSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		adapter=new ArrayAdapter<Pair<T>>(context, android.R.layout.simple_spinner_dropdown_item, statuspairs);
		setAdapter(adapter);
		setOnItemSelectedListener(new OnItemSelectedListener() { 
			@SuppressWarnings("unchecked")
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(listener!=null)listener.OnSelected(index, ((Pair<T>)((Spinner)parent).getSelectedItem()).getValue());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do Nothing
			} 
		});
	}

	public void setStatus(T status){
		setSelection(getIndex(status));
	}

	public T getStatus(){
		return statuspairs.get(getSelectedItemPosition()).getValue();
	}

	public void setOnStatusSelectedListener(OnStatusSelectedListener listener){
		this.listener=listener;
	}
	public void setIndex(int index){
		this.index=index;
	}
}
