package kade_c.taskforge.activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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

import io.fabric.sdk.android.services.concurrency.Task;
import kade_c.taskforge.R;
import kade_c.taskforge.utils.Notifications;
import kade_c.taskforge.utils.Tutorial;
import kade_c.taskforge.fragments.AboutFragment;
import kade_c.taskforge.fragments.SettingsFragment;
import kade_c.taskforge.fragments.ToDoFragment;
import kade_c.taskforge.utils.InternalFilesManager;
import kade_c.taskforge.utils.Prompt;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskforge);

        // Setup our navigation drawer
        setUpNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setDrawerState(true);

        // Setup file manager
        IFM = new InternalFilesManager(this, this);

        // Add existing tabs to the navigation drawer
        refreshTabs();
    }

    /**
     * Deactivated 'back' button press when drawer is closed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        Prompt prompt = new Prompt(this, null, "");

        if (id == R.id.action_log_out) {
            prompt.signOut();
        } else if (id == R.id.action_add_list) {
            prompt.addList();
        } else if (id == R.id.action_delete_list) {
            prompt.listDeletion();
        } else if (id == R.id.action_settings) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new SettingsFragment()).commit();
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
        displaySelectedScreen(item.getTitle().toString());
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

        // Sets the 'General' tab as selected by default.
        navigationView.getMenu().findItem(R.id.nav_general).setChecked(true);
        displaySelectedScreen(getResources().getString(R.string.generalTab));
    }

    /**
     * Instantiates the ToDoFragment and sets its name as parameter
     * @return the fragment to be displayed.
     */
    private Fragment getFragment(String title) {
        Fragment fragment;
        Bundle bundle = new Bundle();

        bundle.putString("name", title);

        fragment = new ToDoFragment();

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
     * @param fragment fragment used for replacement
     */
    public void replaceFragment(final Fragment fragment, boolean drawerOpen) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName())
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
     * Calls authentication Activity and signs out.
     */
    public void signOut() {
        Intent i = new Intent(getApplicationContext(), AuthenticationActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("SignOut", true);
        i.putExtras(b);
        startActivity(i);
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
    // TODO: Handle back button bug after notification opened
    public Notification getNotification(String title, String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent resultIntent = new Intent(this, TaskForgeActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(TaskForgeActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        return builder.build();
    }
}
