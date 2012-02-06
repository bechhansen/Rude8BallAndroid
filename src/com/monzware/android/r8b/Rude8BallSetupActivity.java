package com.monzware.android.r8b;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Rude8BallSetupActivity extends Activity implements ShakeListener, ExtendableTimerListener {

	private SensorManager sensorMgr;

	private ExtendableTimer timer = new ExtendableTimer(2000, 250, 250);

	private TextView tv = null;
	private ImageView button = null;

	private String rudeComment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.setup);

		ImageView next = (ImageView) findViewById(R.id.imageView5);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}

		});

	}

	@Override
	public void shaking() {
		timer.schedue();
	}

	@Override
	public void timerStarted() {

		tv.post(new Runnable() {

			@Override
			public void run() {
				tv.setText("You are a...");
			}
		});

		try {
			CrapNameTask crapNameTask = new CrapNameTask();
			AsyncTask<Void, Integer, String> execute = crapNameTask.execute();
			rudeComment = execute.get();
		} catch (InterruptedException e) {

		} catch (ExecutionException e) {

		}

		shakeAnimation();

	}

	@Override
	public void timerTick() {
		shakeAnimation();
	}

	@Override
	public void timerDone() {
		setSviner();
	}

	private void setSviner() {

		if (rudeComment != null) {

			tv.post(new Runnable() {

				@Override
				public void run() {
					tv.setText(rudeComment);
				}
			});

		} else {

			runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getApplicationContext(), "The 8 ball can not be bothered at this time", Toast.LENGTH_LONG).show();
				}
			});
		}

	}

	private void shakeAnimation() {

		button.post(new Runnable() {

			@Override
			public void run() {
				int currentRotation = 0;

				RotateAnimation anim = new RotateAnimation(currentRotation, currentRotation + 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				currentRotation = (currentRotation + 30) % 360;

				anim.setInterpolator(new LinearInterpolator());
				anim.setDuration(250);
				anim.setFillEnabled(true);

				anim.setFillAfter(true);
				button.startAnimation(anim);

			}
		});
	}
}