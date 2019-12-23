package com.glebworx.pomodoro.ui.fragment.report_project;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.glebworx.pomodoro.model.ProjectModel;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragment;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.report_project.interfaces.IReportProjectFragmentPresenter;

import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;

import static com.glebworx.pomodoro.ui.fragment.report_project.ReportProjectFragment.ARG_PROJECT_MODEL;

public class ReportProjectFragmentPresenter implements IReportProjectFragmentPresenter {

    private @NonNull
    IReportProjectFragment presenterListener;
    private @NonNull
    IReportProjectFragmentInteractionListener interactionListener;
    private @NonNull
    ProjectModel projectModel;

    private CompositeDisposable compositeDisposable;

    ReportProjectFragmentPresenter(@NonNull IReportProjectFragment presenterListener,
                                   @NonNull IReportProjectFragmentInteractionListener interactionListener,
                                   @Nullable Bundle arguments) {
        this.presenterListener = presenterListener;
        this.interactionListener = interactionListener;
        init(Objects.requireNonNull(arguments));
    }

    @Override
    public void init(Bundle arguments) {
        projectModel = Objects.requireNonNull(arguments.getParcelable(ARG_PROJECT_MODEL));
        presenterListener.onInitView(projectModel.getName());
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
    }

}
