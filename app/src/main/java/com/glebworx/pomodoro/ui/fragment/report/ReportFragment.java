package com.glebworx.pomodoro.ui.fragment.report;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.report.adapter.ReportPagerAdapter;
import com.glebworx.pomodoro.ui.fragment.report.interfaces.IReportFragment;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ReportFragment extends Fragment implements IReportFragment {

    @BindView(R.id.button_close)
    AppCompatImageButton closeButton;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    private Unbinder unbinder;

    public ReportFragment() { }

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initTabs();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initTabs() {
        ReportPagerAdapter adapter = new ReportPagerAdapter();
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
