package kade_c.taskforge.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;

import kade_c.taskforge.R;
import kade_c.taskforge.TaskForgeActivity;

/**
 * Settings activity
 * Is an activity in order to handle the theme change
 */
//  TODO: Settings
public class SettingsFragment extends PreferenceFragment { //extends AppCompatActivity

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TaskForgeActivity) getActivity()).setDrawerState(false);
        ((TaskForgeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((TaskForgeActivity)getActivity()).displayMenu(false);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Settings");
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setBackgroundColor(Color.WHITE);
    }
}