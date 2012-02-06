package com.monzware.android.r8b;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Rude8BallAndroidActivity extends Activity implements ShakeListener, ExtendableTimerListener {

	private SensorManager sensorMgr;

	private ExtendableTimer timer = new ExtendableTimer(2000, 250, 250);

	private TextView tv = null;
	private ImageView button = null;

	ShakeDetector sd = new ShakeDetector();

	private String rudeComment;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);

		tv = (TextView) findViewById(R.id.textView1);
		button = (ImageView) findViewById(R.id.imageView1);

		timer.addExtendableTimerListener(this);

		sd.addShakeListener(this);

		// start motion detection
		sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);

		Sensor mAccelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		boolean accelSupported = sensorMgr.registerListener(sd, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		if (!accelSupported) {
			sensorMgr.unregisterListener(sd);
		}

		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				timer.schedue();
			}
		});

		ImageView next = (ImageView) findViewById(R.id.imageView3);
		next.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), Rude8BallSetupActivity.class);
				startActivityForResult(myIntent, 0);
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
				tv.setText(R.string.waitingToServe);
			}
		});

		try {
			SharedPreferences settings = getSharedPreferences(ConfigurationConstants.PREFS_NAME, 0);
			String str = ConfigurationConstants.getLanguageString(settings.getInt(ConfigurationConstants.LANGUAGE, 0));
			CrapNameTask crapNameTask = new CrapNameTask(str);
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
					Toast.makeText(getApplicationContext(), R.string.unableToServe, Toast.LENGTH_LONG).show();
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

	@Override
	public void finish() {
		sensorMgr.unregisterListener(sd);
		super.finish();
	}

	@Override
	protected void onPause() {
		sensorMgr.unregisterListener(sd);
		super.onPause();
	}

	@Override
	protected void onResume() {
		Sensor mAccelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		boolean accelSupported = sensorMgr.registerListener(sd, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		if (!accelSupported) {
			sensorMgr.unregisterListener(sd);
		}
		super.onResume();
	}

}