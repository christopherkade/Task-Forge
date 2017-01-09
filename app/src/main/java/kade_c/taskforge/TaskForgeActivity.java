package kade_c.taskforge;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import kade_c.taskforge.fragments.AboutFragment;
import kade_c.taskforge.fragments.ToDoFragment;

public class TaskForgeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Our Navigation Drawer.
    DrawerLayout drawer;

    // Our toggler for the Navigation Drawer.
    ActionBarDrawerToggle toggle;

    private NavigationView navigationView;

    private String email;

    private String[] listNames = new String[] {
            "General",
            "Daily",
            "Groceries"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskforge);

        email = getIntent().getStringExtra("email");

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
    private void setUpNavDrawer() {
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
        displaySelectedScreen(R.id.nav_general);
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
        }
//        else if (id == android.R.id.home) { // Find actual id (cleaner) 16908332
//            finish();
//            TaskForgeActivity.this.overridePendingTransition(0, 0);
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks which item has been selected and instantiates the corresponding Fragment.
     * Home will not be instantiated twice in order to avoid useless API requests.
     * @param itemId id of the item selected.
     * @return the fragment to be displayed.
     */
    private Fragment prepareFragment(int itemId) {
        Fragment fragment = null;
        Bundle bundle = new Bundle();

        switch (itemId) {
            // GENERAL
            case R.id.nav_general:
                bundle.putString("name", listNames[0]);
                break;
            // DAILY
            case R.id.nav_daily:
                bundle.putString("name", listNames[1]);
                break;
            // GROCERIES
            case R.id.nav_groceries:
                bundle.putString("name", listNames[2]);
                break;
            // ABOUT
            case R.id.nav_about:
                fragment = new AboutFragment();
                bundle.putString("name", "About");
                break;
        }
        if (fragment == null) {
            fragment = new ToDoFragment();
        }

        bundle.putString("email", email);

        // Sets fragment argument
        fragment.setArguments(bundle);
        return fragment;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        displaySelectedScreen(item.getItemId());
        drawer.closeDrawer(GravityCompat.START);

        if (item.getItemId() != R.id.nav_general)
            navigationView.getMenu().findItem(R.id.nav_general).setChecked(false);
        return true;
    }

    /**
     * Checks the selected fragments state and launches it.
     */
    private void displaySelectedScreen(int itemId) {
        Fragment fragment;
        fragment = prepareFragment(itemId);

        if (fragment != null)
            this.replaceFragment(fragment);
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

}
