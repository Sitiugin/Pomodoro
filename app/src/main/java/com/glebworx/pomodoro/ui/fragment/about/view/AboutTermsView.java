package com.glebworx.pomodoro.ui.fragment.about.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutTermsView;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import java.util.List;

public class AboutTermsView extends RecyclerView implements IAboutTermsView {

    private Context context;

    public AboutTermsView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public AboutTermsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AboutTermsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @Override
    public void onInitView(List<AboutItem> items) {
        this.setLayoutManager(new LinearLayoutManager(context));
        this.setHasFixedSize(true);
        this.setItemAnimator(new AlphaCrossFadeAnimator());
        ItemAdapter<AboutItem> adapter = new ItemAdapter<>();
        adapter.add(items);
        FastAdapter<AboutItem> fastAdapter = new FastAdapter<>();
        fastAdapter.addAdapter(0, adapter);
        fastAdapter.setHasStableIds(true);
        this.setAdapter(fastAdapter);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AboutTermsView,
                defStyleAttr,
                0);

        boolean isEmbedded;
        try {
            isEmbedded = a.getBoolean(R.styleable.AboutTermsView_embedded, false);
        } finally {
            a.recycle();
        }

        this.context = context;
        AboutTermsViewPresenter presenter = new AboutTermsViewPresenter(this);
        presenter.init(context, isEmbedded);
    }

}
