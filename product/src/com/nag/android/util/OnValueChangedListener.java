package com.nag.android.util;

public interface OnValueChangedListener<T>{
	String OnValueChanged(ButtonSelector<T> tag, T value);
}
