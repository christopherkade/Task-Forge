package kade_c.taskforge.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import kade_c.taskforge.R;
import kade_c.taskforge.utils.DialogHandler;
import kade_c.taskforge.utils.Notifications;
import kade_c.taskforge.utils.Tutorial;
import kade_c.taskforge.fragments.AboutFragment;
import kade_c.taskforge.fragments.SettingsFragment;
import kade_c.taskforge.fragments.ToDoFragment;
import kade_c.taskforge.utils.InternalFilesManager;

/**
 * Activity that handles the display of List Fragments
 */
public class TaskForgeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Our File manager
    private InternalFilesManager IFM;

    // Our Navigation Drawer items
    public DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    protected NavigationView navigationView;
    private Menu menu;
    private Toolbar toolbar;

    private String fname = "";
    private String previousTabName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskforge);

        // On app launch, sets the right language
        setLanguage();

        // Setup file manager
        IFM = new InternalFilesManager(this, this);

        // Add existing tabs to the navigation drawer
        refreshTabs();

        // Setup our navigation drawer
        setUpNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setDrawerState(true);
    }

    /**
     * On app launch, set language to the one defined in the preferences.
     */
    private void setLanguage() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Locale locale;

        String language = sharedPref.getString(SettingsFragment.KEY_PREF_LANGUAGE, "English");

        switch (language) {
            case "English":
                locale = new Locale("en");
                break;
            case "French":
                locale = new Locale("fr");
                break;
            default:
                locale = new Locale("en");
                break;
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getApplicationContext().getResources().updateConfiguration(config, null);

    }

    /**
     * Handles back navigation
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fname.equals("SettingsFragment")) {
            displaySelectedScreen(previousTabName);
            getFragmentManager().popBackStackImmediate();
            setDrawerState(true);
            displayMenu(true);
            super.onBackPressed();
        } else if (!fname.equals("ToDoFragment"))  {
            fname = "ToDoFragment";
            setDrawerState(true);
            displayMenu(true);
            super.onBackPressed();
        } else if (fname.equals("ToDoFragment")) {
            finish();
        }
    }

    /**
     * Inflates our menu, adds items to the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    /**
     * ActionBar click handler
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        DialogHandler prompt = new DialogHandler(this, null, "");

        if (id == R.id.action_add_list) {
            prompt.addList();
        } else if (id == R.id.action_delete_list) {
            prompt.listDeletion();
        } else if (id == R.id.action_settings) {
            fname = "SettingsFragment";
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment())
                    .addToBackStack(fname)
                    .commit();
        } else if (id == R.id.action_tutorial) {
            new Tutorial(getWindow().getDecorView().getRootView(), this, toolbar);
        } else if (id == R.id.action_about) {
            this.replaceFragment(new AboutFragment(), false);
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        item.setCheckable(true);
        displaySelectedScreen(item.getTitle().toString().replace("\n", ""));
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Shows or hides items in our Menu
     */
    public void displayMenu(boolean showMenu){
        if (menu == null)
            return;
        menu.setGroupVisible(R.id.main_menu_group, showMenu);
    }

    /**
     * Sets up our Navigation Drawer
     */
    public void setUpNavDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Sets up the Navigation Drawer.
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // If we must go back to a previously selected page, do so here.
        String lastPage = getIntent().getStringExtra("previousTab");
        if (lastPage != null) {
            displaySelectedScreen(lastPage);
            setSelectedList(lastPage);
        } else {
            String currentListDisplay = "General";
            navigationView.getMenu().findItem(R.id.nav_general).setChecked(true);
            displaySelectedScreen(currentListDisplay);
        }
    }

    /**
     * Instantiates the ToDoFragment and sets its name as parameter
     * @return the fragment to be displayed.
     */
    private Fragment getFragment(String title) {
        Bundle bundle = new Bundle();

        bundle.putString("name", title);

        Fragment fragment = new ToDoFragment();

        // Sets fragment argument
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Checks the selected fragments state and launches it.
     */
    private void displaySelectedScreen(String title) {
        Fragment fragment = getFragment(title);
        this.replaceFragment(fragment, true);
    }

    /**
     * Replaces the current fragment and closes the Drawer.
     * @param fragmentR fragment used for replacement
     */
    public void replaceFragment(final Fragment fragmentR, boolean drawerOpen) {
        fname = fragmentR.getClass().getSimpleName();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragmentR, fragmentR.getClass().getSimpleName())
                .addToBackStack(fname)
                .commit();

        // If our drawer is open, close it
        if (drawerOpen) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Is called in Fragments that should hide the button to access the Nav. Drawer.
     */
    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TaskForgeActivity.this.overridePendingTransition(0, 0);
                    finish();
                    onSupportNavigateUp();
                }
            });
            toggle.syncState();
        }
    }

    /**
     * Adds tab given as parameter to Navigation Drawer
     * @param tabName tab to be written in our tab file
     */
    public void addNewTab(String tabName) {
        InternalFilesManager IFM = new InternalFilesManager(this, this);

        // Add tab to file, but check for duplicated first
        if (!IFM.writeTabFile(tabName)) {
            Toast.makeText(this, getResources().getString(R.string.toast_already_exists),
                    Toast.LENGTH_LONG).show();
            return;
        }

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();

        MenuItem menuItem = menu.add(tabName);
        menuItem.setIcon(R.mipmap.ic_launcher);
    }

    /**
     * Adds tabs on our menu
     */
    private void refreshTabs() {
        // Check if already displayed
        ArrayList<String> tabs = IFM.readTabFile();

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();

        for (String tab : tabs) {
            MenuItem menuItem = menu.add(tab);
            menuItem.setIcon(R.mipmap.ic_launcher);
        }
    }

    /**
     * Schedules a notification at the given delay
     */
    public void scheduleNotification(Notification notification, long delay) {
        Intent notificationIntent = new Intent(this, Notifications.class);
        notificationIntent.putExtra(Notifications.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(Notifications.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    /**
     * Builds a notification with the given title and content
     */
    public Notification getNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);

        Intent resultIntent = new Intent(this, TaskForgeActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TaskForgeActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        return builder.build();
    }

    public void setPreviousTabName(String previousTabName) {
        this.previousTabName = previousTabName;
    }

    /**
     * Sets the right item to be checked in our Navigation Drawer menu
     * @param listName
     */
    private void setSelectedList(String listName) {
        ArrayList<String> tabs = IFM.readTabFile();
        int i = 0;
        boolean checked = false;

        for (String tab : tabs) {
            i++;
            if (listName.equals(tab.replace("\n", ""))) {
                navigationView.getMenu().getItem(i).setCheckable(true);
                navigationView.getMenu().getItem(i).setChecked(true);
                checked = true;
            }
        }
        if (!checked) {
            navigationView.getMenu().findItem(R.id.nav_general).setChecked(true);
        }
    }

    public void restartFragment() {
        getFragmentManager().popBackStackImmediate();

        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .addToBackStack("SettingsFragment")
                .commit();
    }
}
