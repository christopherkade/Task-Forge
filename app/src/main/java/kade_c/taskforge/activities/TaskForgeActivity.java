package kade_c.taskforge.activities;

import android.content.Intent;
import android.os.Bundle;
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

import kade_c.taskforge.R;
import kade_c.taskforge.utils.Tutorial;
import kade_c.taskforge.fragments.AboutFragment;
import kade_c.taskforge.fragments.SettingsFragment;
import kade_c.taskforge.fragments.ToDoFragment;
import kade_c.taskforge.utils.InternalFilesManager;
import kade_c.taskforge.utils.Notifications;
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

        // Setup file manager
        IFM = new InternalFilesManager(this, this);
        // Setup our navigation drawer
        setUpNavDrawer();
        // Add existing tabs to the navigation drawer
        refreshTabs();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setDrawerState(true);
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
        Prompt prompt = new Prompt(this);

        if (id == R.id.action_log_out) {
            prompt.signOut();
            return true;
        } else if (id == R.id.action_add_list) {
            prompt.addList();
            return true;
        } else if (id == R.id.action_delete_list) {
            prompt.listDeletion();
            return true;
        } else if (id == R.id.action_settings) {
            settings();
            return true;
        } else if (id == R.id.action_tutorial) {
            new Tutorial(getWindow().getDecorView().getRootView(), this, toolbar);
            return true;
        } else if (id == R.id.action_about) {
            about();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        item.setCheckable(true);
        if (displaySelectedScreen(item.getTitle().toString())) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.closeDrawer(GravityCompat.START);
            if (item.getItemId() != R.id.nav_general)
                navigationView.getMenu().findItem(R.id.nav_general).setChecked(false);
        }
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
     * Checks which item has been selected and instantiates the corresponding Fragment.
     * @return the fragment to be displayed.
     */
    private Fragment prepareFragment(String title) {
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
    private boolean displaySelectedScreen(String title) {
        Fragment fragment;
        fragment = prepareFragment(title);

        this.replaceFragment(fragment);

        return false;
    }

    /**
     * Replaces the current fragment and closes the Drawer.
     * @param fragment fragment used for replacement
     */
    public void replaceFragment(final Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName())
                .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Close our Drawer since we have selected a valid item.
        drawer.closeDrawer(GravityCompat.START);
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
     * @param tabName
     */
    // Read tab file and display them
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
     * Starts the about Fragment
     */
    private void about() {
        this.replaceFragment(new AboutFragment());
    }

    /**
     * Starts the settings Fragment
     */
    private void settings() {
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
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
}
