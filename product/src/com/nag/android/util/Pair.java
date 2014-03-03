package com.nag.android.util;

class Pair<T>{
	private String label;
	private T value;

	public Pair(String label, T value){
		this.label=label;
		this.value=value;
	}

	public String getLabel(){
		return label;
	}

	public String toString(){
		return label;
	}

	public T getValue(){
		return value;
	}
}
