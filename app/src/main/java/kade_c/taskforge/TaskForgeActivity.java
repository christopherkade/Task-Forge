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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import kade_c.taskforge.fragments.AboutFragment;
import kade_c.taskforge.fragments.SettingsFragment;
import kade_c.taskforge.fragments.ToDoFragment;

/**
 * Activity that handles the display of List Fragments
 */
public class TaskForgeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Our File manager
    private InternalFilesManager IFM;

    // Our Navigation Drawer.
    private DrawerLayout drawer;

    // Our toggler for the Navigation Drawer.
    private ActionBarDrawerToggle toggle;

    protected NavigationView navigationView;

    private String email;

    private ArrayList<String> tabs;

    private boolean inputError;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskforge);

        email = getIntent().getStringExtra("email");

        IFM = new InternalFilesManager(this, this);

        displayTabs();

        setUpNavDrawer();

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
     * Shows or hides items in our Menu
     */
    public void displayMenu(boolean showMenu){
        if (menu == null)
            return;
        menu.setGroupVisible(R.id.main_menu_group, showMenu);
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
            promptAddList();
            return true;
        } else if (id == R.id.action_delete_list) {
            promptListDeletion();
            return true;
        } else if (id == R.id.action_settings) {
            settings();
            return true;
        } else if (id == R.id.action_about) {
            about();
            return true;
        } else if (id == android.R.id.home) {
            finish();
            TaskForgeActivity.this.overridePendingTransition(0, 0);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            setDrawerState(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    private void signOut() {
        Intent i = new Intent(getApplicationContext(), AuthenticationActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("SignOut", true);
        i.putExtras(b);
        startActivity(i);
    }

    /**
     * Displays a list of current available tabs to be deleted
     */
    private void promptListDeletion() {
        tabs = IFM.readTabFile();

        // Check if there are lists to delete
        if (tabs.size() == 0) {
            Toast.makeText(TaskForgeActivity.this, "No lists found",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Build dialog
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TaskForgeActivity.this);
        builderSingle.setIcon(R.mipmap.delete_icon);
        builderSingle.setTitle("Delete list");

        // Set ArrayAdapter to contain tab list
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TaskForgeActivity.this, android.R.layout.select_dialog_singlechoice);
        for (String tab : tabs) {
            arrayAdapter.add(tab);
        }

        builderSingle
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Get name of the tab to delete
                String tabName = arrayAdapter.getItem(which);
                tabName = tabName.trim().replace("\n", "");

                Toast.makeText(TaskForgeActivity.this, tabName + " deleted",
                        Toast.LENGTH_LONG).show();

                // Delete internal file related to tab deleted
                IFM.deleteFile(tabName);

                // Delete tab in tab file
                IFM.deleteTab(which);

                // Refresh activity
                finish();
                startActivity(getIntent());
            }
        });
        builderSingle.show();
    }

    /**
     * Prompts user before sign out.
     */
    private void promptSignOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(TaskForgeActivity.this);
        alert.setTitle("Sign out?");
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
     * Displays list name prompt
     */
    private void promptAddList() {
        LayoutInflater li = LayoutInflater.from(this);

        // Inflate dialog view
        final View promptsView = li.inflate(R.layout.dialog_list_name_layout, null);

        // Dialog builder
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        alertDialogBuilder.setIcon(R.mipmap.add_icon);
        alertDialogBuilder.setTitle("List name:");

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

                                if (tab.equals("")) {
                                    titleInput.setError("Required");
                                    inputError = true;
                                } else {
                                    titleInput.setError(null);
                                    inputError = false;

                                    // Add tab to file and to list
                                    addNewTab(tab);

                                    Toast.makeText(TaskForgeActivity.this, tab + " created",
                                            Toast.LENGTH_LONG).show();

                                    drawer.openDrawer(GravityCompat.START);
                                }
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

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                //If the error flag was set to true then show the dialog again
                if (inputError) {
                    alertDialog.show();
                }
            }
        });
    }

    /**
     * Checks which item has been selected and instantiates the corresponding Fragment.
     * @return the fragment to be displayed.
     */
    private Fragment prepareFragment(String title) {
        Fragment fragment;
        Bundle bundle = new Bundle();

        bundle.putString("name", title);
        bundle.putString("email", email);

        fragment = new ToDoFragment();

        // Sets fragment argument
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Checks the selected fragments state and launches it.
     */
    private boolean displaySelectedScreen(int itemId, String title) {
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

    /**
     * Adds tabs on our menu
     */
    private void displayTabs() {
        // Check if already displayed
        tabs = IFM.readTabFile();

        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navView.getMenu();

        for (String tab : tabs) {
            MenuItem menuItem = menu.add(tab);
            menuItem.setIcon(R.mipmap.ic_launcher);
        }
    }
}
