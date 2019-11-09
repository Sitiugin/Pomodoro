package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutView;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import java.util.List;

public class AboutLicensesView extends RecyclerView implements IAboutView {

    private Context context;
    private AboutAppViewPresenter presenter;

    public AboutLicensesView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public AboutLicensesView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AboutLicensesView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    public void onInitView(List<AboutItem> items) {
        this.setLayoutManager(new LinearLayoutManager(context));
        this.setItemAnimator(new AlphaCrossFadeAnimator());
        ItemAdapter<AboutItem> adapter = new ItemAdapter<>();
        adapter.add(items);
        FastAdapter<AboutItem> fastAdapter = new FastAdapter<>();
        fastAdapter.addAdapter(0, adapter);
        fastAdapter.setHasStableIds(true);
        this.setAdapter(fastAdapter);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        View rootView = inflate(context, R.layout.view_about_licenses, this);
        this.context = context;
        presenter = new AboutAppViewPresenter(this);
    }

}
