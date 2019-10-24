package com.glebworx.pomodoro.model;

public class ReportPomodoroOverviewModel {

    private int pomodorosCompleted;
    private float averagePerDay;
    private int streak;

    public ReportPomodoroOverviewModel() {
        this.pomodorosCompleted = 0;
        this.averagePerDay = 0;
        this.streak = 0;
    }

    public int getPomodorosCompleted() {
        return pomodorosCompleted;
    }

    public void setPomodorosCompleted(int pomodorosCompleted) {
        this.pomodorosCompleted = pomodorosCompleted;
    }

    public float getAveragePerDay() {
        return averagePerDay;
    }

    public void setAveragePerDay(float averagePerDay) {
        this.averagePerDay = averagePerDay;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

}
