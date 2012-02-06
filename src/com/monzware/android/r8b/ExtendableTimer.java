package com.monzware.android.r8b;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import android.util.Log;

public class ExtendableTimer implements Runnable {

	private Collection<ExtendableTimerListener> list = Collections.synchronizedList(new ArrayList<ExtendableTimerListener>());

	private final int delay;
	private final int tick;

	Thread timeThread = new Thread(this);
	private boolean stop;

	private int restTime;
	private int tickRestTime;

	private final int accuracy;

	public ExtendableTimer(int delay, int tick, int accuracy) {
		this.delay = delay;
		this.tick = tick;
		this.accuracy = accuracy;
	}

	public void addExtendableTimerListener(ExtendableTimerListener listener) {
		list.add(listener);
	}

	public void schedue() {

		synchronized (timeThread) {

			restTime = delay;
			// tickRestTime = tick;

			if (!timeThread.isAlive()) {
				timeThread.start();
				notifyStart();
			} else {
				timeThread.notifyAll();
			}
		}

	}

	private void notifyStart() {
		Log.d(this.getClass().getName(), "NotifyStart");
		for (ExtendableTimerListener l : list) {
			l.timerStarted();
		}
	}

	private void notifyTick() {
		Log.d(this.getClass().getName(), "NotifyTick");
		for (ExtendableTimerListener l : list) {
			l.timerTick();
		}
	}

	private void notifyDone() {
		Log.d(this.getClass().getName(), "NotifyDone");
		for (ExtendableTimerListener l : list) {
			l.timerDone();
		}
	}

	public void stop() {

		synchronized (timeThread) {
			stop = true;
			timeThread.notify();
		}
	}

	@Override
	public void run() {

		while (!stop) {

			try {

				synchronized (timeThread) {

					if (restTime <= 0) {
						notifyDone();
						timeThread.wait();
						if (!stop) {
							notifyStart();
						}
					}

					restTime -= accuracy;
					tickRestTime -= accuracy;
					if (tickRestTime <= 0) {
						tickRestTime = tick;
						notifyTick();
					}

					timeThread.wait(accuracy);
				}

			} catch (InterruptedException e) {

			}
		}

	}

}
