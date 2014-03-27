package com.nag.android.util;

public class PrimitiveLabel<T> implements CharSequence {
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
	@Override
	public char charAt(int index) {
		return toString().charAt(index);
	}

	@Override
	public int length() {
		return toString().length();
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return toString().subSequence(start, end);
	}
}

