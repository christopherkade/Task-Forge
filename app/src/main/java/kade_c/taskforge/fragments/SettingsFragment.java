package kade_c.taskforge.fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.Locale;

import kade_c.taskforge.R;
import kade_c.taskforge.activities.TaskForgeActivity;

/**
 * Settings Fragment, handles basic settings for our To do list app
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String KEY_PREF_DEL_ON_CHECK = "pref_delOnCheck";
    public static final String KEY_PREF_MOVE_ON_CHECK = "pref_moveOnCheck";
    public static final String KEY_PREF_LANGUAGE = "pref_language";
    public static final String KEY_PREF_NOTIFICATIONS = "pref_notifications";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        ((TaskForgeActivity) getActivity()).setDrawerState(false);
        ((TaskForgeActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TaskForgeActivity)getActivity()).displayMenu(false);

        handleLanguage();
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

    /**
     * Handles the language setting
     * Reloads UI on change with the appropriate language
     */
    // TODO: Change ListPreference summary language on language change
    private void handleLanguage() {
        final ListPreference listPreference = (ListPreference) findPreference(KEY_PREF_LANGUAGE);
        String currentValue = listPreference.getValue();
        listPreference.setSummary(currentValue);

        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String selectedLanguage = newValue.toString();
                preference.setSummary(selectedLanguage);

                // Change language
                Locale locale;
                if (selectedLanguage.equals("French") || selectedLanguage.equals("Français")) {
                    locale = new Locale("fr");
                } else {
                    locale = new Locale("en");
                }
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getActivity().getApplicationContext().getResources().updateConfiguration(config, null);

                // Refresh view
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, new SettingsFragment())
                        .commit();

                return true;
            }
        });
    }
}