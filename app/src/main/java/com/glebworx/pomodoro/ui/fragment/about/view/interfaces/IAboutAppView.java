package com.glebworx.pomodoro.ui.fragment.about.view.interfaces;

import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutAppHeaderItem;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;

import java.util.List;

public interface IAboutAppView {

    void onInitView(AboutAppHeaderItem headerItem, List<AboutItem> items);

}
