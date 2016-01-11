package com.android.volley;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

public class VolleyActivity extends Activity {

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(0);
	}

	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id) {
		return super.onCreateDialog(id);
	}

	public final void sendReq(final String tag) {

	}

	protected void receiveResp(String tag) {

	}

}
