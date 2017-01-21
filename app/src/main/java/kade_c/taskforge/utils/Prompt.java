package kade_c.taskforge.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import kade_c.taskforge.R;
import kade_c.taskforge.activities.TaskForgeActivity;

/**
 * Handles app prompts (event creation, list creation, deletion etc.)
 */
public class Prompt {

    private Activity activity;
    private ArrayList<String> tabs;
    private InternalFilesManager IFM;
    private boolean inputError;

    public Prompt(Activity activity) {
        this.activity = activity;

        IFM = new InternalFilesManager(activity, activity);
    }

    /**
     * Displays a list of current available tabs to be deleted
     */
    public void listDeletion() {
        tabs = IFM.readTabFile();

        // Check if there are lists to delete
        if (tabs.size() == 0) {
            Toast.makeText(activity, activity.getResources().getString(R.string.toast_no_list_found),
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Build dialog
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(activity);
        builderSingle.setIcon(R.mipmap.delete_icon);
        builderSingle.setTitle(activity.getResources().getString(R.string.action_delete_list));

        // Set ArrayAdapter to contain tab list
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.select_dialog_singlechoice);
        for (String tab : tabs) {
            arrayAdapter.add(tab);
        }

        builderSingle
                .setNegativeButton(activity.getResources().getString(R.string.button_cancel),
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

                Toast.makeText(activity, tabName + " " + activity.getResources().getString(R.string.deleted_text),
                        Toast.LENGTH_LONG).show();

                // Delete internal file related to tab deleted
                IFM.deleteFile(tabName);

                // Delete tab in tab file
                IFM.deleteTab(which);

                // Refresh activity
                activity.finish();
                activity.startActivity(activity.getIntent());
            }
        });
        builderSingle.show();
    }

    /**
     * Prompts user before sign out.
     */
    public void signOut() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(activity.getResources().getString(R.string.action_log_out) + "?");
        alert.setNegativeButton(activity.getResources().getString(R.string.button_no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.setPositiveButton(activity.getResources().getString(R.string.button_yes), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((TaskForgeActivity)activity).signOut();
                activity.overridePendingTransition(0, 0);

                dialog.dismiss();
            }
        });

        alert.show();
    }

    /**
     * Displays list name prompt
     */
    public void addList() {
        LayoutInflater li = LayoutInflater.from(activity);

        // Inflate dialog view
        final View promptsView = li.inflate(R.layout.dialog_list_name_layout, null);

        // Dialog builder
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);

        alertDialogBuilder.setIcon(R.mipmap.add_icon);
        alertDialogBuilder.setTitle(activity.getResources().getString(R.string.dialog_list_name));

        alertDialogBuilder.setView(promptsView);

        final EditText titleInput = (EditText) promptsView
                .findViewById(R.id.title_input);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.button_add),
                        new DialogInterface.OnClickListener() {

                            @RequiresApi(api = Build.VERSION_CODES.M)
                            public void onClick(DialogInterface dialog, int id) {

                                String tab = titleInput.getText().toString();

                                if (tab.equals("")) {
                                    titleInput.setError(activity.getResources().getString(R.string.text_required));
                                    inputError = true;
                                } else {
                                    titleInput.setError(null);

                                    // Add tab to file and to list
                                    ((TaskForgeActivity)activity).addNewTab(tab);

                                    Toast.makeText(activity, tab + " " + activity.getResources().getString(R.string.created_text),
                                            Toast.LENGTH_LONG).show();

                                    ((TaskForgeActivity)activity).drawer.openDrawer(GravityCompat.START);
                                }
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(activity.getResources().getString(R.string.button_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                inputError = false;
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

}
