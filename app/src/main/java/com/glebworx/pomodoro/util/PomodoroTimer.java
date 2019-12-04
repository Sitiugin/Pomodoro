package com.glebworx.pomodoro.util;

/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import androidx.annotation.NonNull;


public abstract class PomodoroTimer {

    /**
     * Millis since epoch when alarm should stop.
     */
    private final long millisInFuture;

    /**
     * The interval in millis that the user receives callbacks
     */
    private final long countDownInterval;

    private long stopTimeInFuture;

    private long pauseTime;

    private boolean cancelled = false;

    private boolean paused = false;

    private Handler handler = new PomodoroHandler(this); // handles counting down

    /**
     * @param millisInFuture The number of millis in the future from the call
     *   to {@link #start()} until the countdown is done and {@link #onFinish()}
     *   is called.
     * @param countDownInterval The interval along the way to receive
     *   {@link #onTick(long)} callbacks.
     */
    protected PomodoroTimer(long millisInFuture, long countDownInterval) {
        this.millisInFuture = millisInFuture;
        this.countDownInterval = countDownInterval;
    }

    /**
     * Cancel the countdown.
     *
     * Do not call it from inside CountDownTimer threads
     */
    public final void cancel() {
        handler.removeMessages(MSG);
        cancelled = true;
    }

    /**
     * Start the countdown.
     */
    public synchronized final PomodoroTimer start() {
        if (millisInFuture <= 0) {
            onFinish();
            return this;
        }
        stopTimeInFuture = SystemClock.elapsedRealtime() + millisInFuture;
        handler.sendMessage(handler.obtainMessage(MSG));
        cancelled = false;
        paused = false;
        return this;
    }

    /**
     * Pause the countdown.
     */
    public long pause() {
        pauseTime = stopTimeInFuture - SystemClock.elapsedRealtime();
        paused = true;
        return pauseTime;
    }

    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();


    private static final int MSG = 1;

    /**
     * Resume the countdown.
     */
    public long resume() {
        stopTimeInFuture = pauseTime + SystemClock.elapsedRealtime();
        paused = false;
        handler.sendMessage(handler.obtainMessage(MSG));
        return pauseTime;
    }

    private boolean isPaused() {
        return paused;
    }

    private boolean isCancelled() {
        return cancelled;
    }

    private long getStopTimeInFuture() {
        return stopTimeInFuture;
    }

    private long getCountDownInterval() {
        return countDownInterval;
    }

    private static class PomodoroHandler extends Handler {

        private final PomodoroTimer timer;

        PomodoroHandler(PomodoroTimer timer) {
            this.timer = timer;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {

            synchronized (timer) {
                if (timer.isPaused()) {
                    return;
                }
                handleMessage();
            }

        }

        private void handleMessage() {

            final long millisLeft = timer.getStopTimeInFuture() - SystemClock.elapsedRealtime();
            final long countdownInterval = timer.getCountDownInterval();

            if (millisLeft <= 0) {

                timer.onFinish();

            } else if (millisLeft < countdownInterval) {

                // no tick, just delay until done
                sendMessageDelayed(obtainMessage(MSG), millisLeft);

            } else {

                long lastTickStart = SystemClock.elapsedRealtime();
                timer.onTick(millisLeft);

                // take into account user's onTick taking time to execute
                long delay = lastTickStart + countdownInterval - SystemClock.elapsedRealtime();

                // special case: user's onTick took more than interval to
                // complete, skip to next interval
                while (delay < 0) delay += countdownInterval;

                if (!timer.isCancelled()) {
                    sendMessageDelayed(obtainMessage(MSG), delay);
                }

            }

        }
    }

}