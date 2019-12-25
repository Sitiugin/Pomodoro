package com.glebworx.pomodoro.model.report;

public class ReportProjectOverviewModel {

    private int projectsCompleted;
    private int tasksCompleted;

    public ReportProjectOverviewModel() {
        projectsCompleted = 0;
        tasksCompleted = 0;
    }

    public ReportProjectOverviewModel(int projectsCompleted, int tasksCompleted) {
        this.projectsCompleted = projectsCompleted;
        this.tasksCompleted = tasksCompleted;
    }

    public int getProjectsCompleted() {
        return projectsCompleted;
    }

    public void setProjectsCompleted(int projectsCompleted) {
        this.projectsCompleted = projectsCompleted;
    }

    public int getTasksCompleted() {
        return tasksCompleted;
    }

    public void setTasksCompleted(int tasksCompleted) {
        this.tasksCompleted = tasksCompleted;
    }

}
