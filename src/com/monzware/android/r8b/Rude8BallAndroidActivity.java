package com.monzware.android.r8b;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Rude8BallAndroidActivity extends Activity {

	private String url = "http://crapname-bech.rhcloud.com/rs/crapname/generator";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final Button button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				setSviner();

			}

			private void setSviner() {

				final TextView tv = (TextView) findViewById(R.id.textView2);

				try {
					CrapNameTask crapNameTask = new CrapNameTask();
					AsyncTask<String, Integer, String> execute = crapNameTask.execute(url);
					String result = execute.get();
					tv.setText(result);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
	}
}