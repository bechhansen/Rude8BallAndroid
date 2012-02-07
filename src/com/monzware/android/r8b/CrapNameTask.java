package com.monzware.android.r8b;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;

import android.os.AsyncTask;

public class CrapNameTask extends AsyncTask<Void, Integer, String> {

	private String url = "http://crapname-bech.rhcloud.com/rs/crapname/generator";

	private HttpClient httpclient = new DefaultHttpClient();
	private HttpGet request = new HttpGet(url);

	private ResponseHandler<String> handler = new BasicResponseHandler();

	public CrapNameTask(String str) {

		HttpProtocolParams.setUserAgent(httpclient.getParams(), "Rude8BallAndroid");
		request.setHeader("Accept-Language", str);
	}

	protected String doInBackground(Void... urls) {

		try {
			return httpclient.execute(request, handler);
		} catch (ClientProtocolException e) {

		} catch (IOException e) {

		} catch (Exception e) {

		}
		return null;
	}

	protected void onProgressUpdate(Integer... progress) {

	}

	protected void onPostExecute(Long result) {

	}
}