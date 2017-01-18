package kade_c.taskforge.fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;

import kade_c.taskforge.R;
import kade_c.taskforge.TaskForgeActivity;

/**
 * Settings Fragment, handles basic settings for our To do list app
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_PREF_DEL_ON_CHECK = "pref_delOnCheck";
    public static final String KEY_PREF_MOVE_ON_CHECK = "pref_moveOnCheck";

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
        getActivity().setTitle(getResources().getString(R.string.settings_title));
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setBackgroundColor(Color.WHITE);
    }
}