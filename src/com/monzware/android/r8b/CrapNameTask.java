package com.monzware.android.r8b;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class CrapNameTask extends AsyncTask<Void, Integer, String> {

	private String url = "http://crapname-bech.rhcloud.com/rs/crapname/generator";

	HttpClient httpclient = new DefaultHttpClient();
	HttpGet request = new HttpGet(url);

	ResponseHandler<String> handler = new BasicResponseHandler();

	protected String doInBackground(Void... urls) {

		try {
			return httpclient.execute(request, handler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onProgressUpdate(Integer... progress) {

	}

	protected void onPostExecute(Long result) {

	}
}