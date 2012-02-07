package com.monzware.android.r8b;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;

public class Rude8BallSetupActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.setup);

		ImageView next = (ImageView) findViewById(R.id.imageView5);
		next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
			}

		});

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(ConfigurationConstants.PREFS_NAME, 0);
		int lang = settings.getInt(ConfigurationConstants.LANGUAGE, 0);

		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		spinner.setSelection(lang);

	}

	@Override
	protected void onStop() {
		super.onStop();

		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		int pos = spinner.getSelectedItemPosition();

		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(ConfigurationConstants.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(ConfigurationConstants.LANGUAGE, pos);

		// Commit the edits!
		editor.commit();
	}
}