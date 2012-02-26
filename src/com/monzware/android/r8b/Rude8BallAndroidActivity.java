package com.monzware.android.r8b;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class Rude8BallAndroidActivity extends Activity implements ShakeListener, ExtendableTimerListener {

	private static final String ADD_KEY = "a14f34e2648253d";

	private static final int SPEECH_DATA_CHECK_CODE = 100;
	private static final int MAIL_CODE = 101;

	private SensorManager sensorMgr;

	private ExtendableTimer timer = new ExtendableTimer(2000, 250, 250);

	private TextView tv = null;
	private ImageView button = null;

	private ShakeDetector sd = new ShakeDetector();

	private RudeTalker talker = new RudeTalker();

	private String rudeComment;

	private AdView adView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);

		tv = (TextView) findViewById(R.id.rudeText);
		button = (ImageView) findViewById(R.id.ballImage);

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
			@Override
			public void onClick(View v) {
				timer.schedue();
			}
		});

		ImageView next = (ImageView) findViewById(R.id.setupButton);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent myIntent = new Intent(view.getContext(), Rude8BallSetupActivity.class);
				startActivityForResult(myIntent, 0);
			}

		});

		ImageView mailButton = (ImageView) findViewById(R.id.mailButton);
		mailButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (rudeComment == null) {
					Toast.makeText(getApplicationContext(), R.string.mailNotRude, Toast.LENGTH_LONG).show();
					return;
				}

				if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
					Toast.makeText(getApplicationContext(), R.string.mailNotExternal, Toast.LENGTH_LONG).show();
					return;
				}

				try {
					File file = Environment.getExternalStorageDirectory();

					LayoutInflater inflater = getLayoutInflater();

					View mailView = inflater.inflate(R.layout.mail, null);
					TextView rudeMailText = (TextView) mailView.findViewById(R.id.rudeMailText);
					rudeMailText.setText(getString(R.string.mailYouAre) + " " + rudeComment);

					mailView.measure(410, 350);
					mailView.layout(0, 0, 410, 350);

					final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);

					emailIntent.setType("plain/text");

					String title = getString(R.string.mailTitle);
					String text = getString(R.string.mailText);

					ArrayList<Parcelable> uris = new ArrayList<Parcelable>();

					String imageFileName = file.getAbsolutePath() + getString(R.string.maiImageFilename);

					Bitmap b = Bitmap.createBitmap(mailView.getWidth(), mailView.getHeight(), Bitmap.Config.ARGB_8888);
					Canvas c = new Canvas(b);
					mailView.draw(c);
					FileOutputStream out = new FileOutputStream(imageFileName);
					b.compress(Bitmap.CompressFormat.PNG, 90, out);

					uris.add(Uri.parse("file://" + imageFileName));

					if (talker.isEnabled()) {
						String mp3Filename = file.getAbsolutePath() + getString(R.string.mailMP3Filename);
						talker.talkToFile(rudeComment, mp3Filename);
						uris.add(Uri.parse("file://" + mp3Filename));
					}

					emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, title);
					emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
					emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

					startActivityForResult(Intent.createChooser(emailIntent, text), MAIL_CODE);

				} catch (Exception e) {
					Log.e("", "Error", e);
				}
			}

		});

		initializeTalker();

		// Create the adView
		adView = new AdView(this, AdSize.BANNER, ADD_KEY);

		LinearLayout layout = (LinearLayout) findViewById(R.id.add);

		// Add the adView to it
		layout.addView(adView);

		adView.loadAd(new AdRequest());

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
			talker.setLanguage(str);
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

			talker.talk(rudeComment);

		} else {

			runOnUiThread(new Runnable() {
				@Override
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

	/**
	 * Is called when returned from the configuration dialog.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			initializeTalker();
		} else if (requestCode == SPEECH_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				talker.enable(this);
			} else {
				talker.disable();
			}
		} else if (requestCode == MAIL_CODE) {
			// Delete files
			File file = Environment.getExternalStorageDirectory();
			String mp3Filename = file.getAbsolutePath() + getString(R.string.mailMP3Filename);
			String imageFileName = file.getAbsolutePath() + getString(R.string.maiImageFilename);

			File mp3File = new File(mp3Filename);
			mp3File.delete();

			File imagefile = new File(imageFileName);
			imagefile.delete();
		}
	}

	private void initializeTalker() {
		// Restore preferences
		SharedPreferences settings = getSharedPreferences(ConfigurationConstants.PREFS_NAME, 0);
		boolean isSound = settings.getBoolean(ConfigurationConstants.SOUND, false);
		if (isSound) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, SPEECH_DATA_CHECK_CODE);
		} else {
			talker.disable();
		}
	}

	@Override
	protected void onDestroy() {
		talker.stop();
		adView.destroy();
		super.onDestroy();
	}

}