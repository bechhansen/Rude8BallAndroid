package com.monzware.android.r8b;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class Rude8BallAndroidActivity extends Activity {

	private String url = "http://crapname-bech.rhcloud.com/rs/crapname/generator";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		final ImageView button = (ImageView) findViewById(R.id.imageView1);

		button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				int currentRotation = 0;

				RotateAnimation anim = new RotateAnimation(currentRotation,
						currentRotation + 1440, Animation.RELATIVE_TO_SELF,
						0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				currentRotation = (currentRotation + 30) % 360;

				anim.setInterpolator(new LinearInterpolator());
				anim.setDuration(1000);
				anim.setFillEnabled(true);

				anim.setFillAfter(true);
				button.startAnimation(anim);
				setSviner();
			}

			private void setSviner() {

				final TextView tv = (TextView) findViewById(R.id.textView1);

				try {
					CrapNameTask crapNameTask = new CrapNameTask();
					AsyncTask<String, Integer, String> execute = crapNameTask
							.execute(url);
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