package com.ifatter.util;

import java.util.ResourceBundle;

public class LocalResourceBundle {

	private ResourceBundle mResourceBundle;

	private LocalResourceBundle(ResourceBundle mResourceBundle) {
		this.mResourceBundle = mResourceBundle;
	}

	public static final LocalResourceBundle getBundle(String bundleName) {
		try {
			LocalResourceBundle bundle = new LocalResourceBundle(
					ResourceBundle.getBundle(bundleName));
			return bundle;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public final String getString(String key) {
		try {
			return mResourceBundle.getString(key);
		} catch (Exception e) {
			return null;
		}
	}
}
