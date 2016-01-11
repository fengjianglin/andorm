package com.android.volley.toolbox.ext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

public class MultipartRequest extends Request<String> {

	private HttpEntity mHttpEntity;

	private Response.Listener<String> mListener;

	public MultipartRequest(String url, Map<String, String> keyFilePathMap,
			Response.Listener<String> listener,
			Response.ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		mListener = listener;
		mHttpEntity = buildMultipartEntity(keyFilePathMap);
	}

	private HttpEntity buildMultipartEntity(Map<String, String> keyFilePathMap) {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		for (Entry<String, String> entry : keyFilePathMap.entrySet()) {
			String key = entry.getKey();
			File file = new File(entry.getValue());
			FileBody fileBody = new FileBody(file);
			builder.addPart(key, fileBody);
		}
		return builder.build();
	}

	@Override
	public String getBodyContentType() {
		return mHttpEntity.getContentType().getValue();
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			mHttpEntity.writeTo(bos);
		} catch (IOException e) {
			VolleyLog.e("IOException writing to ByteArrayOutputStream");
		}
		return bos.toByteArray();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed,
				HttpHeaderParser.parseCacheHeaders(response));
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}
}