package kade_c.taskforge;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import kade_c.taskforge.fragments.ToDoFragment;

/**
 * Activity that handles the display of List Fragments
 */
public class TaskForgeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    InternalFilesManager IFM;

    int CONTENT_LAYOUT_ID = R.id.content_frame;

    // Our Navigation Drawer.
    DrawerLayout drawer;

    // Our toggler for the Navigation Drawer.
    ActionBarDrawerToggle toggle;

    protected NavigationView navigationView;

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskforge);

        email = getIntent().getStringExtra("email");

        IFM = new InternalFilesManager(this, this);

        displayTabs();

        setUpNavDrawer();
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
     * Sets up our Navigation Drawer
     */
    public void setUpNavDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        displaySelectedScreen(R.id.nav_general, "General");
    }

    /**
     * Inflates our menu, adds items to the action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * ActionBar click handler
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_log_out) {
            promptSignOut();
            return true;
        } else if (id == R.id.action_add_list) {
            // TODO: Put add list icon in Nav Drawer
            promptListName();
        } else if (id == android.R.id.home) {
            finish();
            TaskForgeActivity.this.overridePendingTransition(0, 0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            setDrawerState(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts the settings Fragment
     */
    private void settings() {
        TaskForgeActivity.this.overridePendingTransition(0, 0);
        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(i);
    }

    /**
     * Checks which item has been selected and instantiates the corresponding Fragment.
     * @param itemId id of the item selected.
     * @return the fragment to be displayed.
     */
    private Fragment prepareFragment(int itemId, String title) {
        Fragment fragment;
        Bundle bundle = new Bundle();

        bundle.putString("name", title);

//        switch (itemId) {
//            case R.id.nav_about:
//                fragment = new AboutFragment();
//                break;
//            default:
//                break;
//        }

        fragment = new ToDoFragment();
        bundle.putString("email", email);

        // Sets fragment argument
        fragment.setArguments(bundle);
        return fragment;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        item.setCheckable(true);
        if (displaySelectedScreen(item.getItemId(), item.getTitle().toString())) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.closeDrawer(GravityCompat.START);
            if (item.getItemId() != R.id.nav_general)
                navigationView.getMenu().findItem(R.id.nav_general).setChecked(false);
        }
        return true;
    }

    /**
     * Checks the selected fragments state and launches it.
     */
    private boolean displaySelectedScreen(int itemId, String title) {
        // Launches Settings if selected
//        if (itemId == R.id.nav_settings) {
//            settings();
//            return true;
//        }

        Fragment fragment;
        fragment = prepareFragment(itemId, title);

        this.replaceFragment(fragment);

        return false;
    }


    /**
     * Prompts user before sign out.
     */
    private void promptSignOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(TaskForgeActivity.this);
        alert.setMessage("Sign out?");
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOut();
                TaskForgeActivity.this.overridePendingTransition(0, 0);

                dialog.dismiss();
            }
        });

        alert.show();
    }

    /**
     * Calls authentication Activity and signs out.
     */
    private void signOut() {
        Intent i = new Intent(getApplicationContext(), AuthenticationActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("SignOut", true);
        i.putExtras(b);
        startActivity(i);
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
                    onSupportNavigateUp();
                }
            });
            toggle.syncState();
        }
    }

    /**
     * Displays list name prompt
     */
    private void promptListName() {
        LayoutInflater li = LayoutInflater.from(this);

        // Inflate dialog view
        final View promptsView = li.inflate(R.layout.dialog_list_name_layout, null);

        // Dialog builder
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setView(promptsView);

        final EditText titleInput = (EditText) promptsView
                .findViewById(R.id.title_input);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            public void onClick(DialogInterface dialog, int id) {

                                String tab = titleInput.getText().toString();

                                // Add tab to file
                                addNewTab(tab);

                                // Add tab to menu
//                                addTab(tab);

                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void onDrawerOpened(View drawerView) {
        displayTabs();
        // Stop telling the user, he know how it works
    }

    /**
     * Adds tab given as parameter to Navigation Drawer
     * @param tabName
     */
    // Read tab file and display them
    private void addNewTab(String tabName) {
        InternalFilesManager IFM = new InternalFilesManager(this, this);

        // Add tab to file, but check for duplicated first
        if (!IFM.writeTabFile(tabName)) {
            Toast.makeText(this, "Already exists",
                    Toast.LENGTH_LONG).show();
            return;
        }

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();

        MenuItem menuItem = menu.add(tabName);
        menuItem.setIcon(R.mipmap.ic_launcher);
    }

    public static boolean hasDuplicates(ArrayList<String> list, String name)
    {
        int numCount = 0;

        for (String str : list) {
            if (str.equals(name)) numCount++;
        }

        return numCount > 1;
    }

    // TODO: Make tabs deletable (long click or icon)
    private void displayTabs() {
        // Check if already displayed
        ArrayList<String> tabs = IFM.readTabFile();

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();

        for (String tab : tabs) {
            MenuItem menuItem = menu.add(tab);
            menuItem.setIcon(R.mipmap.ic_launcher);
        }
    }
}
