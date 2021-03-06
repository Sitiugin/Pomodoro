package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutAppView;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutAppHeaderItem;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import java.util.List;

public class AboutAppView extends RecyclerView implements IAboutAppView {

    private Context context;

    public AboutAppView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public AboutAppView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AboutAppView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    public void onInitView(AboutAppHeaderItem headerItem, List<AboutItem> items) {
        this.setLayoutManager(new LinearLayoutManager(context));
        this.setHasFixedSize(true);
        this.setItemAnimator(new AlphaCrossFadeAnimator());
        ItemAdapter<AboutAppHeaderItem> headerAdapter = new ItemAdapter<>();
        headerAdapter.add(headerItem);
        ItemAdapter<AboutItem> adapter = new ItemAdapter<>();
        adapter.add(items);
        FastAdapter fastAdapter = new FastAdapter<>(); // TODO use generics
        fastAdapter.addAdapter(0, headerAdapter);
        fastAdapter.addAdapter(1, adapter);
        fastAdapter.setHasStableIds(true);
        this.setAdapter(fastAdapter);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        AboutAppViewPresenter presenter = new AboutAppViewPresenter(this);
        presenter.init(context);
    }

}
