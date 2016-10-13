/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class NetworkResponseRequest extends Request<NetworkResponse> {
	private final Listener<NetworkResponse> mListener;

	public NetworkResponseRequest(int method, String url,
			Listener<NetworkResponse> listener, ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
	}

	public NetworkResponseRequest(String url,
			Listener<NetworkResponse> listener, ErrorListener errorListener) {
		this(Method.GET, url, listener, errorListener);
	}

	@Override
	protected void deliverResponse(NetworkResponse response) {
		mListener.onResponse(response);
	}

	@Override
	protected Response<NetworkResponse> parseNetworkResponse(
			NetworkResponse response) {
		return Response.success(response,
				HttpHeaderParser.parseCacheHeaders(response));
	}
}