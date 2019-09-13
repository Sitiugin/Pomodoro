package com.glebworx.pomodoro.util.manager;

import android.content.Context;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;

import static com.glebworx.pomodoro.util.constants.Constants.ANIM_DURATION;

public class ConstraintTransitionManager {

    private ConstraintSet constraintSet;

    public ConstraintTransitionManager() {
        this.constraintSet = new ConstraintSet();
    }

    public void beginTransition(Context context, ConstraintLayout layout, int newLayoutId) {
        constraintSet.clone(context, newLayoutId);
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        transition.setDuration(ANIM_DURATION);
        androidx.transition.TransitionManager.beginDelayedTransition(layout, transition);
        constraintSet.applyTo(layout);
    }
}
