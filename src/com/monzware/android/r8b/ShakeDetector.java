package com.monzware.android.r8b;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class ShakeDetector implements SensorEventListener {

	private long lastUpdate = -1;
	private float x, y, z;
	private float last_x, last_y, last_z;
	private Collection<ShakeListener> list = Collections.synchronizedList(new ArrayList<ShakeListener>());

	private static final int SHAKE_THRESHOLD = 500;

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		float[] values = event.values;

		long curTime = System.currentTimeMillis();
		// only allow one update every 100ms.
		if ((curTime - lastUpdate) > 100) {
			long diffTime = (curTime - lastUpdate);
			lastUpdate = curTime;

			x = values[SensorManager.DATA_X];
			y = values[SensorManager.DATA_Y];
			z = values[SensorManager.DATA_Z];

			float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

			if (speed > SHAKE_THRESHOLD) {
				shakeRegistered();
			}

			last_x = x;
			last_y = y;
			last_z = z;
		}

	}

	private void shakeRegistered() {
		for (ShakeListener l : list) {
			l.shaking();
		}
	}

	public void addShakeListener(ShakeListener listener) {
		list.add(listener);
	}
}
