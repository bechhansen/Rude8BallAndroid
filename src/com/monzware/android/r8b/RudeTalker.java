package com.monzware.android.r8b;

import java.util.Locale;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

public class RudeTalker implements OnInitListener {

	private TextToSpeech textToSpeech;
	private boolean enabled;
	private Locale language;

	public void enable(Rude8BallAndroidActivity a) {

		if (textToSpeech == null) {
			textToSpeech = new TextToSpeech(a, this);
		}

		enabled = true;
	}

	@Override
	public void onInit(int status) {

	}

	public void setLanguage(String str) {
		if (textToSpeech != null) {
			int languageAvailable = textToSpeech.isLanguageAvailable(new Locale(str));
			if (languageAvailable == TextToSpeech.LANG_AVAILABLE) {
				language = new Locale(str);
			} else {
				language = new Locale("de");
			}
		}

	}

	public void talk(String rudeComment) {
		if (enabled) {
			rudeComment = fix(rudeComment);
			textToSpeech.setLanguage(language);
			textToSpeech.speak(rudeComment, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	private String fix(String rudeComment) {
		return rudeComment.toLowerCase().replace("f**k", "fuck");
	}

	public void stop() {
		if (textToSpeech != null) {
			textToSpeech.shutdown();
		}
	}

	public void disable() {
		enabled = false;
	}
}
