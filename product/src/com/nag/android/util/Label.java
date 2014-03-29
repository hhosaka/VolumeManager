package com.nag.android.util;

import com.nag.android.util.PrimitiveLabel;

public class Label<T> extends PrimitiveLabel<T> implements CharSequence{
	private String label;
	public Label(String label, T value) {
		super(value);
		this.label=label;
	}

	@Override
	public String toString(){
		return label;
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
