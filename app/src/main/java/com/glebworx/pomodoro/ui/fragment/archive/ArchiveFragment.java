package com.glebworx.pomodoro.ui.fragment.archive;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragment;
import com.glebworx.pomodoro.ui.fragment.archive.interfaces.IArchiveFragmentInteractionListener;
import com.glebworx.pomodoro.ui.item.ProjectItem;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IItemAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter_extensions.UndoHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.widget.Toast.LENGTH_LONG;


public class ArchiveFragment extends Fragment implements IArchiveFragment {


    //                                                                                       BINDING

    @BindView(R.id.button_close)
    AppCompatImageButton closeButton;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    //                                                                                    ATTRIBUTES

    private Context context;
    private ItemAdapter<ProjectItem> projectAdapter;
    private FastAdapter<AbstractItem> fastAdapter;
    private UndoHelper<AbstractItem> undoHelper;
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
    public void onInitView(IItemAdapter.Predicate<ProjectItem> predicate) {

        projectAdapter = new ItemAdapter<>();
        fastAdapter = new FastAdapter<>();
        undoHelper = new UndoHelper<>(fastAdapter, (positions, removed) -> {
            for (FastAdapter.RelativeInfo<AbstractItem> relativeInfo : removed) {
                presenter.deleteProject((ProjectItem) relativeInfo.item, relativeInfo.position);
            }
        });

        /*initRecyclerView(fastAdapter);
        initSearchView(predicate);
        initClickEvents(fastAdapter);*/

    }

    @Override
    public void onItemAdded(ProjectItem item) {

    }

    @Override
    public void onItemModified(ProjectItem item) {

    }

    @Override
    public void onItemDeleted(ProjectItem item) {

    }

    @Override
    public void onDeleteProjectFailed(int position) {
        synchronized (this) {
            fastAdapter.notifyAdapterItemChanged(position);
        }
        Toast.makeText(context, R.string.archive_toast_project_delete_failed, LENGTH_LONG).show();
    }

}
