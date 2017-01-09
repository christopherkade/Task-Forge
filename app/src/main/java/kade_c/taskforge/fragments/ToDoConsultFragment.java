package kade_c.taskforge.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kade_c.taskforge.R;
import kade_c.taskforge.TaskForgeActivity;

/**
 * Called when user clicks on a To do item.
 * Displays details
 */
public class ToDoConsultFragment extends Fragment {

    private View view;
    private String title;
    private String content;
    private String date;
    private String tab;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_consult_todo, container, false);

        // TODO: Set back arrow navigation

        title = getArguments().getString("title");
        content = getArguments().getString("content");
        date = getArguments().getString("date");
        tab = getArguments().getString("tab");

        setConsultDetails();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(title);
    }

    private void setConsultDetails() {
        TextView dateTv = (TextView) view.findViewById(R.id.consult_date);
        TextView contentTv = (TextView) view.findViewById(R.id.consult_content);

        dateTv.setText(date);
        contentTv.setText(content);
    }

}
