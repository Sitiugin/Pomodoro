package com.glebworx.pomodoro.ui.fragment.report.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.glebworx.pomodoro.R;

public class ReportPagerAdapter extends PagerAdapter {

    private Context context;

    public ReportPagerAdapter(Context context) {
        super();
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return container.findViewById(getViewId(position));
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.report_title_history);
            case 1:
                return context.getString(R.string.report_title_pomodoros);
            case 2:
                return context.getString(R.string.report_title_tasks);
            default:
                return context.getString(R.string.report_title_history);
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }

    private int getViewId(int position) {
        switch (position) {
            case 0:
                return R.id.view_history;
            case 1:
                return R.id.view_pomodoros;
            case 2:
                return R.id.view_tasks;
            default:
                return R.id.view_history;
        }
    }

}
