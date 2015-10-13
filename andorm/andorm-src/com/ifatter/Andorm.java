package com.ifatter;

import android.content.Context;

public class Andorm {

	public static Context context;

	public static void initialize(Context context) {
		Andorm.context = context;
	}

	public static String getAppPath() {
		return Andorm.context.getFilesDir().getPath();
	}
}
