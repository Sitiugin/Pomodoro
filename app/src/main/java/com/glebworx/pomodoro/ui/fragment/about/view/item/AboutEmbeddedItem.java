package com.glebworx.pomodoro.ui.fragment.about.view.item;

import com.glebworx.pomodoro.R;

public class AboutEmbeddedItem extends AboutItem {

    public AboutEmbeddedItem(String description) {
        super(description);
    }

    public AboutEmbeddedItem(String title, String description) {
        super(title, description);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_about_embedded;
    }

}
