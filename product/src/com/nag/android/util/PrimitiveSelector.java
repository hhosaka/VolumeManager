//package com.nag.android.util;
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Filter;
//import android.widget.Spinner;
//
//public class PrimitiveSelector<T> extends Spinner {
//
//	public interface OnSelectedListener<T>{
//		void OnSelected(View parent, T status);
//	}
//
//	private OnSelectedListener<T> listener=null;
////	private int index=0;
//	private final ArrayList<PrimitiveLabel<T>> labels=new ArrayList<PrimitiveLabel<T>>();;
//	private ArrayAdapter<PrimitiveLabel<T>> adapter=null;
//
//	public PrimitiveSelector(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		adapter=new InternalAdapter(context, labels);
//		setAdapter(adapter);
//		setOnItemSelectedListener(new OnItemSelectedListener() { 
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
////				if(listener!=null)listener.OnSelected(Selector.this, ((Label<T>)((Spinner)parent).getSelectedItem()).getValue());
//				if(listener!=null)listener.OnSelected(PrimitiveSelector.this, labels.get(position).getValue());
//			}
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// Do Nothing
//			} 
//		});
//	}
//
//	public void add(PrimitiveLabel<T> label){
//		labels.add(label);
//		adapter.notifyDataSetChanged();
//	}
//
//	private int getIndex(T value){
////		return labels.indexOf(value);
//		for(int i=0;i<labels.size();++i){
//			if(value==labels.get(i).getValue()) return i;
//		}
//		throw new RuntimeException();
//	}
//
//	public void setValue(T value){
//		setSelection(getIndex(value));
//		adapter.notifyDataSetChanged();// is it safety??
//	}
//
//	public T getValue(){
//		return labels.get(getSelectedItemPosition()).getValue();
//	}
//
//	public void setOnSelectedListener(OnSelectedListener<T> listener){
//		this.listener=listener;
//	}
//	
//	class InternalAdapter extends ArrayAdapter<PrimitiveLabel<T>>{
//		InternalAdapter(Context context, ArrayList<PrimitiveLabel<T>> labels){
//			super(context, android.R.layout.simple_spinner_dropdown_item, labels);
//		}
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			View view=super.getView(position, convertView, parent);
//			if(view!=null){
//				if(!labels.get(position).isEnable()){
//					view.setVisibility(View.GONE);
//				}else{
//					view.setVisibility(View.VISIBLE);
//				}
//			}
//			return view;
//		}
//		@Override
//		public boolean isEnabled(int position){
//			return labels.get(position).isEnable();
//		}
//	}
//}
