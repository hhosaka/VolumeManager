package com.nag.android.util;

public class PrimitiveLabel<T>{
	private T value;
	public PrimitiveLabel(T value){
		this.value=value;
	}
	public T getValue(){
		return value;
	}
	@Override
	public boolean equals(Object obj){
		return obj==this || obj==value;
	}
	public boolean isEnable(){
		return true;
	}
	public String toString(){
		return value.toString();
	}
}

