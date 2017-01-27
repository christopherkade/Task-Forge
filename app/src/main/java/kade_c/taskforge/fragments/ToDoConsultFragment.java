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

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_consult_todo, container, false);

        ((TaskForgeActivity)getActivity()).setDrawerState(false);
        ((TaskForgeActivity)getActivity()).displayMenu(false);

        setDetails();

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
    private void setDetails() {
        TextView titleTv = (TextView) view.findViewById(R.id.consult_title);
        TextView dateTv = (TextView) view.findViewById(R.id.consult_date);
        TextView timeTv = (TextView) view.findViewById(R.id.consult_time);
        TextView contentTv = (TextView) view.findViewById(R.id.consult_content);

        // Get information to display
        String title = getArguments().getString("title");
        String content = getArguments().getString("content");
        String date = getArguments().getString("date");
        String time = getArguments().getString("time");

        // Sets it
        titleTv.setText(title);
        dateTv.setText(getResources().getString(R.string.dialog_the) + " " + date);
        timeTv.setText(getResources().getString(R.string.dialog_at) + " " + time);
        contentTv.setText(content);
    }
}
