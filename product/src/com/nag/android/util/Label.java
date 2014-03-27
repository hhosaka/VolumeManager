package com.nag.android.util;

import com.nag.android.util.PrimitiveLabel;

public class Label<T> extends PrimitiveLabel<T>{
	private String label;
	public Label(String label, T value) {
		super(value);
		this.label=label;
	}

	public String toString(){
		return label;
	}
}
