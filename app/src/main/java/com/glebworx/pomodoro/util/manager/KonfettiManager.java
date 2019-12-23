package com.glebworx.pomodoro.util.manager;

import android.content.Context;

import com.glebworx.pomodoro.R;

import java.util.Objects;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class KonfettiManager {

    private KonfettiManager() {

    }

    public static void buildKonfetti(KonfettiView konfettiView, Context context) {
        Objects.requireNonNull(konfettiView).build()
                .addColors(getColors(context))
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(1000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 4))
                .setPosition(-50f, 1050f, -50f, -50f)
                .streamFor(100, 2000L);
    }

    private static int[] getColors(Context context) {
        return new int[]{
                context.getColor(R.color.colorRed),
                context.getColor(R.color.colorPink),
                context.getColor(R.color.colorPurple),
                context.getColor(R.color.colorDeepPurple),
                context.getColor(R.color.colorIndigo),
                context.getColor(R.color.colorBlue),
                context.getColor(R.color.colorLightBlue),
                context.getColor(R.color.colorCyan),
                context.getColor(R.color.colorTeal),
                context.getColor(R.color.colorGreen),
                context.getColor(R.color.colorLightGreen),
                context.getColor(R.color.colorLime),
                context.getColor(R.color.colorYellow),
                context.getColor(R.color.colorAmber),
                context.getColor(R.color.colorOrange),
                context.getColor(R.color.colorDeepOrange)};
    }

}
