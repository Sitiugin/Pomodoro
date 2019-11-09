package com.glebworx.pomodoro.ui.fragment.about.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.about.view.interfaces.IAboutLicensesView;
import com.glebworx.pomodoro.ui.fragment.about.view.item.AboutLicenseItem;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;

import java.util.List;

public class AboutLicensesView extends RecyclerView implements IAboutLicensesView {

    private Context context;
    private AboutLicensesViewPresenter presenter;

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
    public void onInitView(List<AboutLicenseItem> items) {
        this.setLayoutManager(new LinearLayoutManager(context));
        this.setHasFixedSize(true);
        this.setItemAnimator(new AlphaCrossFadeAnimator());
        ItemAdapter<AboutLicenseItem> adapter = new ItemAdapter<>();
        adapter.add(items);
        FastAdapter<AboutLicenseItem> fastAdapter = new FastAdapter<>();
        fastAdapter.addAdapter(0, adapter);
        fastAdapter.setHasStableIds(true);
        fastAdapter.withOnClickListener((v, adapter1, item, position) -> {
            if (item.hasUri()) {
                showUriDialog(item.getUri());
                return true;
            }
            return false;
        });
        this.setAdapter(fastAdapter);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.context = context;
        presenter = new AboutLicensesViewPresenter(this, context);
    }

    private void showUriDialog(String uri) {

        if (context instanceof Activity) {
            DialogManager.showGenericDialog(
                    (Activity) context,
                    R.id.container_main,
                    R.string.about_licenses_title_open_in_browser,
                    Html.fromHtml(context.getString(R.string.about_licenses_text_open_in_browser, uri), 0),
                    R.string.core_open,
                    () -> {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(browserIntent);
                    });
        }

    }

}
