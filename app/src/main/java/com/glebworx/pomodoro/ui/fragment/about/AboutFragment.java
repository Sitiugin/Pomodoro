package com.glebworx.pomodoro.ui.fragment.about;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.adapter.AboutPagerAdapter;
import com.glebworx.pomodoro.ui.fragment.about.interfaces.IAboutFragmentInteractionListener;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class AboutFragment extends Fragment {

    @BindView(R.id.button_close)
    AppCompatImageButton closeButton;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private Context context;
    private IAboutFragmentInteractionListener fragmentListener;
    private Unbinder unbinder;

    public AboutFragment() {
    }

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        context = getContext();
        if (context == null) {
            fragmentListener.onCloseFragment();
        }
        unbinder = ButterKnife.bind(this, rootView);
        initTabs();
        initClickEvents();
        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (IAboutFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }

    private void initTabs() {
        AboutPagerAdapter adapter = new AboutPagerAdapter(context);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initClickEvents() {
        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_close) {
                fragmentListener.onCloseFragment();
            }
        };
        closeButton.setOnClickListener(onClickListener);
    }

}
