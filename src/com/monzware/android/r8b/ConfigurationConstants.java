package com.monzware.android.r8b;

public class ConfigurationConstants {

	public static final String PREFS_NAME = "Setup";
	public static final String LANGUAGE = "Language";
	public static final String SOUND = "Sound";

	public static String[] languages = new String[] { "da", "en", "de" };

	public static String getLanguageString(int lanuageIndex) {
		return languages[lanuageIndex];
	}
}
