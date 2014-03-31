package com.nag.android.util;

import android.widget.Button;

public interface OnValueChangedListener<T>{
	String OnValueChanged(Button button, T value);
}
