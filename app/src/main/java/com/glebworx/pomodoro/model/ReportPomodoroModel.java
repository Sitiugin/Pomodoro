package com.glebworx.pomodoro.model;

public class ReportPomodoroModel {

    private int pomodorosCompleted;

    public ReportPomodoroModel() {
    }

    public ReportPomodoroModel(int pomodorosCompleted) {
        this.pomodorosCompleted = pomodorosCompleted;
    }

    public int getPomodorosCompleted() {
        return pomodorosCompleted;
    }

    public void setPomodorosCompleted(int pomodorosCompleted) {
        this.pomodorosCompleted = pomodorosCompleted;
    }
}
