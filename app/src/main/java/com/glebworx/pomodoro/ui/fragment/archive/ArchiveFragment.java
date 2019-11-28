package com.glebworx.pomodoro.ui.fragment.archive;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragment;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentInteractionListener;
import com.glebworx.pomodoro.ui.fragment.archive.item.ArchiveHeaderItem;
import com.glebworx.pomodoro.ui.fragment.archive.item.ArchivedProjectItem;
import com.glebworx.pomodoro.util.ZeroStateDecoration;
import com.glebworx.pomodoro.util.manager.DialogManager;
import com.google.android.material.textfield.TextInputEditText;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemFilter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.UndoHelper;
import com.mikepenz.fastadapter_extensions.swipe.SimpleSwipeCallback;
import com.mikepenz.itemanimators.SlideInOutLeftAnimator;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.widget.Toast.LENGTH_LONG;
import static com.glebworx.pomodoro.util.constants.Constants.LENGTH_SNACK_BAR;


public class ArchiveFragment extends Fragment implements IArchiveFragment {


    //                                                                                       BINDING

    @BindView(R.id.button_delete_all)
    AppCompatImageButton deleteAllButton;
    @BindView(R.id.button_close)
    AppCompatImageButton closeButton;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    //                                                                                    ATTRIBUTES

    private Context context;
    private ItemAdapter<ArchivedProjectItem> projectAdapter;
    private FastAdapter<AbstractItem> fastAdapter;
    private UndoHelper<AbstractItem> undoDeleteHelper;
    private UndoHelper<AbstractItem> undoRestoreHelper;
    private IArchiveFragmentInteractionListener fragmentListener;
    private Unbinder unbinder;
    private ArchiveFragmentPresenter presenter;


    //                                                                                  CONSTRUCTORS

    public ArchiveFragment() {
    }

    public static ArchiveFragment newInstance() {
        return new ArchiveFragment();
    }


    //                                                                                     LIFECYCLE

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_archive, container, false);
        context = getContext();
        if (context == null) {
            fragmentListener.onCloseFragment();
        }
        unbinder = ButterKnife.bind(this, rootView);
        presenter = new ArchiveFragmentPresenter(this, fragmentListener);
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
        fragmentListener = (IArchiveFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }


    //                                                                                     INTERFACE

    @Override
    public void onInitView(IItemAdapter.Predicate<ArchivedProjectItem> predicate) {

        projectAdapter = new ItemAdapter<>();
        fastAdapter = new FastAdapter<>();
        undoDeleteHelper = new UndoHelper<>(fastAdapter, (positions, removed) -> {
            for (FastAdapter.RelativeInfo<AbstractItem> relativeInfo : removed) {
                presenter.deleteProject((ArchivedProjectItem) relativeInfo.item, relativeInfo.position);
            }
        });
        undoRestoreHelper = new UndoHelper<>(fastAdapter, (positions, removed) -> {
            for (FastAdapter.RelativeInfo<AbstractItem> relativeInfo : removed) {
                presenter.restoreProject((ArchivedProjectItem) relativeInfo.item, relativeInfo.position);
            }
        });

        initRecyclerView(fastAdapter);
        initSearchView(predicate);
        initClickEvents();

    }

    @Override
    public void onItemAdded(ArchivedProjectItem item) {
        synchronized (this) {
            projectAdapter.add(item);
        }
    }

    @Override
    public void onItemModified(ArchivedProjectItem item) {
        synchronized (this) {
            int index = getProjectItemIndex(item.getProjectName());
            if (index != -1) {
                projectAdapter.set(index + 1, item); // add 1 because of header
            }
        }
    }

    @Override
    public void onItemDeleted(ArchivedProjectItem item) {
        synchronized (this) {
            int index = getProjectItemIndex(item.getProjectName());
            if (index != -1) {
                projectAdapter.remove(index + 1); // add 1 because of header
            }
        }
    }

    @Override
    public void onDeleteProjectFailed(int position) {
        synchronized (this) {
            fastAdapter.notifyAdapterItemChanged(position);
        }
        Toast.makeText(context, R.string.archive_toast_project_delete_failed, LENGTH_LONG).show();
    }

    @Override
    public void onRestoreProjectFailed(int position) {
        synchronized (this) {
            fastAdapter.notifyAdapterItemChanged(position);
        }
        Toast.makeText(context, R.string.archive_toast_project_restore_failed, LENGTH_LONG).show();
    }

    @Override
    public void onDeleteAllFinished(boolean isSuccessful) {
        Toast.makeText(
                context,
                isSuccessful
                        ? R.string.archive_toast_delete_all_success
                        : R.string.archive_toast_delete_all_failed,
                LENGTH_LONG).show();
    }

    //                                                                                       HELPERS

    private void initRecyclerView(FastAdapter fastAdapter) {

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new ZeroStateDecoration(R.layout.view_empty_projects));
        recyclerView.setItemAnimator(new SlideInOutLeftAnimator(recyclerView));
        //OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

        ItemAdapter<ArchiveHeaderItem> headerAdapter = new ItemAdapter<>();
        headerAdapter.add(new ArchiveHeaderItem());
        fastAdapter.addAdapter(0, headerAdapter);
        fastAdapter.addAdapter(1, projectAdapter);

        fastAdapter.setHasStableIds(true);
        attachSwipeHelper(recyclerView);
        recyclerView.setAdapter(fastAdapter);

    }

    private void attachSwipeHelper(RecyclerView recyclerView) {
        SimpleSwipeCallback swipeCallback = new SimpleSwipeCallback(
                (position, direction) -> {

                    searchView.clearFocus();

                    if (direction == ItemTouchHelper.RIGHT) {
                        Set<Integer> positionSet = new HashSet<>();
                        positionSet.add(position);
                        searchView.setEnabled(false);
                        undoRestoreHelper.remove(
                                recyclerView,
                                getString(R.string.archive_toast_project_restore_success),
                                getString(R.string.core_undo),
                                LENGTH_SNACK_BAR,
                                positionSet);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        Set<Integer> positionSet = new HashSet<>();
                        positionSet.add(position);
                        searchView.setEnabled(false);
                        undoDeleteHelper.remove(
                                recyclerView,
                                getString(R.string.archive_toast_project_delete_success),
                                getString(R.string.core_undo),
                                LENGTH_SNACK_BAR,
                                positionSet);
                    }

                },
                context.getDrawable(R.drawable.ic_delete_red),
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT,
                context.getColor(android.R.color.transparent))
                .withLeaveBehindSwipeRight(context.getDrawable(R.drawable.ic_restore_black));
        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    private void initSearchView(IItemAdapter.Predicate<ArchivedProjectItem> predicate) {

        ItemFilter<ArchivedProjectItem, ArchivedProjectItem> itemFilter =
                new ItemFilter<ArchivedProjectItem, ArchivedProjectItem>(projectAdapter).withFilterPredicate(predicate);
        projectAdapter.withItemFilter(itemFilter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (undoDeleteHelper.getSnackBar() != null && undoDeleteHelper.getSnackBar().isShown()) {
                    undoDeleteHelper.getSnackBar().dismiss();
                }
                if (undoRestoreHelper.getSnackBar() != null && undoRestoreHelper.getSnackBar().isShown()) {
                    undoRestoreHelper.getSnackBar().dismiss();
                }
                projectAdapter.filter(newText);
                return true;
            }
        });

    }

    private void initClickEvents() {
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_delete_all:
                    showDeleteAllDialog();
                    break;
                case R.id.button_close:
                    fragmentListener.onCloseFragment();
                    break;
            }
        };
        deleteAllButton.setOnClickListener(onClickListener);
        closeButton.setOnClickListener(onClickListener);
    }

    private void showDeleteAllDialog() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        AlertDialog dialog = DialogManager.showDialog(
                activity,
                R.id.container_main,
                R.layout.dialog_delete_all);
        View.OnClickListener onClickListener = view -> {
            if (view.getId() == R.id.button_positive) {
                dialog.dismiss();
                presenter.deleteAll();
            } else if (view.getId() == R.id.button_negative) {
                dialog.dismiss();
            }
        };
        TextInputEditText editText = dialog.findViewById(R.id.edit_text_delete_confirmation);
        AppCompatButton positiveButton = dialog.findViewById(R.id.button_positive);
        editText.setHint(Html.fromHtml(getString(R.string.archive_hint_confirm_delete), 0));
        String deleteString = getString(R.string.archive_text_delete).toLowerCase();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveButton.setEnabled(s.toString().toLowerCase().equals(deleteString));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        positiveButton.setOnClickListener(onClickListener);
        dialog.findViewById(R.id.button_negative).setOnClickListener(onClickListener);
    }

    private int getProjectItemIndex(@NonNull String name) {
        return IntStream.range(0, projectAdapter.getAdapterItems().size())
                .filter(i -> name.equals(projectAdapter.getAdapterItems().get(i).getProjectName()))
                .findFirst().orElse(-1);
    }

}
