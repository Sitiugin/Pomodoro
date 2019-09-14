package com.glebworx.pomodoro.ui.main.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.glebworx.pomodoro.R;
import com.glebworx.pomodoro.api.ProjectApi;
import com.glebworx.pomodoro.model.ProjectModel;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_ALIZARIN_HEX;
import static com.glebworx.pomodoro.util.constants.ColorConstants.COLOR_ALIZARIN_INT;


public class AddProjectFragment extends Fragment {


    //                                                                                       BINDING

    @BindView(R.id.button_close) AppCompatImageButton closeButton;
    @BindView(R.id.button_save) AppCompatButton saveButton;


    //                                                                                    ATTRIBUTES

    private OnAddProjectFragmentInteractionListener fragmentListener;
    private ProjectModel projectModel;


    //                                                                                     LIFECYCLE

    public AddProjectFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_project, container, false);
        ButterKnife.bind(this, rootView);

        projectModel = new ProjectModel();

        initClickEvents();

        return rootView;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragmentListener = (OnAddProjectFragmentInteractionListener) context;
    }

    @Override
    public void onDetach() {
        fragmentListener = null;
        super.onDetach();
    }



    private void initClickEvents() {
        View.OnClickListener onClickListener = view -> {
            switch (view.getId()) {
                case R.id.button_save: // TODO retrieve data from fields
                    Context context = getContext();
                    projectModel.setName("Test Project");
                    projectModel.setDueDate(new Date());
                    projectModel.setColor(COLOR_ALIZARIN_HEX);
                    if (projectModel.isValid()) {
                        saveProject(context);
                    } else if (context != null){
                        Toast.makeText(context, R.string.add_project_toast_add_failed, Toast.LENGTH_LONG).show();
                    }

                    break;
                case R.id.button_close:
                    fragmentListener.onCloseFragment();
                    break;
            }
        };
        saveButton.setOnClickListener(onClickListener);
        closeButton.setOnClickListener(onClickListener);
    }

    private void saveProject(Context context) {
        ProjectApi.saveModel(projectModel, task -> {
            if (context == null) {
                return;
            }
            if (task.isSuccessful()) {
                Toast.makeText(context, R.string.add_project_toast_add_success, Toast.LENGTH_SHORT).show();
                fragmentListener.onCloseFragment();
            } else {
                Toast.makeText(context, R.string.add_project_toast_add_failed, Toast.LENGTH_LONG).show();
            }
        });
    }

    public interface OnAddProjectFragmentInteractionListener {
        void onCloseFragment();
    }

}
