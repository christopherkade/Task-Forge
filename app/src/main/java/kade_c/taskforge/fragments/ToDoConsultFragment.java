package kade_c.taskforge.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kade_c.taskforge.R;
import kade_c.taskforge.activities.TaskForgeActivity;

/**
 * Called when user clicks on a To do item.
 * Displays details
 */
public class ToDoConsultFragment extends Fragment {

    private View view;
    private String title;
    private String content;
    private String date;
    private String time;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_consult_todo, container, false);

        ((TaskForgeActivity)getActivity()).setDrawerState(false);
        ((TaskForgeActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Hide useless menu items
        ((TaskForgeActivity)getActivity()).displayMenu(false);

        title = getArguments().getString("title");
        content = getArguments().getString("content");
        date = getArguments().getString("date");
        time = getArguments().getString("time");

        setConsultDetails();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Details");
    }

    /**
     * Displays consulted item's details
     */
    private void setConsultDetails() {
        TextView titleTv = (TextView) view.findViewById(R.id.consult_title);
        TextView dateTv = (TextView) view.findViewById(R.id.consult_date);
        TextView timeTv = (TextView) view.findViewById(R.id.consult_time);
        TextView contentTv = (TextView) view.findViewById(R.id.consult_content);

        titleTv.setText(title);
        dateTv.setText(getResources().getString(R.string.dialog_the) + " " + date);
        timeTv.setText(getResources().getString(R.string.dialog_at) + " " + time);
        contentTv.setText(content);
    }
}
