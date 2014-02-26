package com.nag.android.volumemanager;

import java.util.ArrayList;

import com.nag.android.volumemanager.VolumeManager.STATUS;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class StatusSelector extends Spinner {

	public interface OnStatusSelectedListener{
		void OnSelected(int index, STATUS status);
	}

	private OnStatusSelectedListener listener=null;
	private int index=0;
	private final ArrayList<StatusPair> statuspairs=new ArrayList<StatusPair>();;
	private ArrayAdapter<StatusPair> adapter=null;

	private int getIndex(STATUS status){
		for(int i=0;i<statuspairs.size();++i){
			if(status==statuspairs.get(i).getStatus()) return i;
		}
		throw new RuntimeException();
	}

	public void add(String title,STATUS status){
		statuspairs.add(new StatusPair(title,status));
		adapter.notifyDataSetChanged();
	}

	public StatusSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		adapter=new ArrayAdapter<StatusPair>(context, android.R.layout.simple_spinner_dropdown_item, statuspairs);
		setAdapter(adapter);
		setOnItemSelectedListener(new OnItemSelectedListener() { 
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(listener!=null)listener.OnSelected(index, ((StatusPair)((Spinner)parent).getSelectedItem()).getStatus());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do Nothing
			} 
		});
	}

	public void setStatus(STATUS status){
		setSelection(getIndex(status));
	}

	public STATUS getStatus(){
		return statuspairs.get(getSelectedItemPosition()).getStatus();
	}

	public void setOnStatusSelectedListener(OnStatusSelectedListener listener){
		this.listener=listener;
	}
	public void setIndex(int index){
		this.index=index;
	}
}
