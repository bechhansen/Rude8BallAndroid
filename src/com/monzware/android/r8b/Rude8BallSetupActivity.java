package com.monzware.android.r8b;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;

public class Rude8BallSetupActivity extends Activity {

	private static final int SPEECH_DATA_CHECK_CODE = 100;
	private static final int SPEECH_DATA_INSTALL = 101;
	private static final int SPEECH_DATA_RECHECK_CODE = 102;

	private CheckBox sound;
	private Spinner spinner;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.setup);

		spinner = (Spinner) findViewById(R.id.spinner1);

		ImageView next = (ImageView) findViewById(R.id.imageView5);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				int pos = spinner.getSelectedItemPosition();

				// We need an Editor object to make preference changes.
				// All objects are from android.context.Context
				SharedPreferences settings = getSharedPreferences(ConfigurationConstants.PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt(ConfigurationConstants.LANGUAGE, pos);

				editor.putBoolean(ConfigurationConstants.SOUND, sound.isChecked());

				// Commit the edits!
				editor.commit();

				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}

		});

		sound = (CheckBox) findViewById(R.id.checkBox1);
		sound.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				boolean checked = sound.isChecked();
				if (checked) {
					Intent checkIntent = new Intent();
					checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
					startActivityForResult(checkIntent, SPEECH_DATA_CHECK_CODE);
				}
			}

		});

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(ConfigurationConstants.PREFS_NAME, 0);
		int lang = settings.getInt(ConfigurationConstants.LANGUAGE, 0);
		spinner.setSelection(lang);

		boolean isSound = settings.getBoolean(ConfigurationConstants.SOUND, false);
		sound.setChecked(isSound);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEECH_DATA_CHECK_CODE) {
			if (resultCode != TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivityForResult(installIntent, SPEECH_DATA_INSTALL);
			}
		} else if (requestCode == SPEECH_DATA_INSTALL) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, SPEECH_DATA_RECHECK_CODE);

		} else if (requestCode == SPEECH_DATA_RECHECK_CODE) {
			sound.setChecked(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS);
		}
	}
}