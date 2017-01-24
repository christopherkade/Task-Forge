package kade_c.taskforge.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kade_c.taskforge.R;
import kade_c.taskforge.activities.TaskForgeActivity;
import kade_c.taskforge.fragments.SettingsFragment;
import kade_c.taskforge.fragments.ToDoFragment;

/**
 * Handles all prompts dialog creation
 * List creation, deletion
 * Signing out
 * Event creation, edition
 */
public class DialogHandler {

    private Activity activity;
    private Fragment fragment;
    private InternalFilesManager IFM;
    private boolean inputError;
    private ArrayList<String> input;

    public DialogHandler(Activity activity, Fragment fragment, String tabSelected) {
        this.activity = activity;
        this.fragment = fragment;
        input = new ArrayList<>();

        IFM = new InternalFilesManager(activity, activity, tabSelected);
    }

    /**
     * Dialog containing tabs available for deletion
     * Clicking one will delete it
     */
    public void listDeletion() {
        ArrayList<String> tabs = IFM.readTabFile();

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

        final AlertDialog alertDialog = builderSingle.create();

        setDialogCancel(alertDialog);
        builderSingle.show();
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

        setDialogCancel(alertDialog);
    }

    public void createTODO() {
        LayoutInflater li = LayoutInflater.from(activity);

        // Inflate dialog view
        final View promptsView = li.inflate(R.layout.dialog_todo_layout, null);

        // Dialog builder
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);

        alertDialogBuilder.setView(promptsView);

        // Set up the Calendar
        final ImageView calendar = (ImageView) promptsView.findViewById(R.id.calendar_image);
        final TextView dateSelected = (TextView) promptsView.findViewById(R.id.date_text);
        final Calendar c = Calendar.getInstance();

        // Set current date by default
        dateSelected.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.MONTH) + 1 + "/" + c.get(Calendar.YEAR));

        // Set Date chooser listener
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
                        dateSelected.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        // Builds dialog
        buildDialog(alertDialogBuilder, promptsView, "create", 0);
    }

    /**
     * Handles edition dialog
     */
    public void editTODO(final int position) {
        input = new ArrayList<>();
        ListView mListView = (ListView) fragment.getView().findViewById(R.id.list);

        // Selected row View.
        View editedView = ((ToDoFragment)fragment).getViewByPosition(position, mListView);

        // Dialog TextViews
        TextView titleTv = (TextView) editedView.findViewById(R.id.title);
        TextView dateTv = (TextView) editedView.findViewById(R.id.date);
        TextView contentTv = (TextView) editedView.findViewById(R.id.content);
        TextView timeTv = (TextView) editedView.findViewById(R.id.time);

        // Get the selected item's info
        String title = titleTv.getText().toString();
        String date = dateTv.getText().toString();
        String time = timeTv.getText().toString();
        String content = contentTv.getText().toString();

        LayoutInflater li = LayoutInflater.from(activity);
        final View promptsView = li.inflate(R.layout.dialog_todo_layout, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);

        alertDialogBuilder.setView(promptsView);

        final EditText titleInput = (EditText) promptsView
                .findViewById(R.id.title_input);
        final EditText contentInput = (EditText) promptsView
                .findViewById(R.id.content_input);

        // Sets hour
        final TimePicker timeInput = (TimePicker) promptsView.findViewById(R.id.time);
        String[] timeArray = time.split(":");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timeInput.setHour(Integer.parseInt(timeArray[0]));
            timeInput.setMinute(Integer.parseInt(timeArray[1]));
        } else {
            timeInput.setCurrentHour(Integer.parseInt(timeArray[0]));
            timeInput.setCurrentMinute(Integer.parseInt(timeArray[1]));

        }

        final ImageView calendar = (ImageView) promptsView.findViewById(R.id.calendar_image);
        final TextView dateSelected = (TextView) promptsView.findViewById(R.id.date_text);
        final Calendar c = Calendar.getInstance();

        // Sets existing values
        titleInput.setText(title);
        contentInput.setText(content);
        dateSelected.setText(date);

        // Set Date chooser listener
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateSelected.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialog.show();
            }
        });

        // Builds dialog
        buildDialog(alertDialogBuilder, promptsView, "edit", position);
    }


    /**
     * Builds and handles the Edition / Creation dialog
     */
    private void buildDialog(AlertDialog.Builder alertDialogBuilder, View selectedView,
                             final String type, final int position) {
        final TextView title = (TextView) selectedView.findViewById(R.id.title_text);
        title.setText(activity.getResources().getString(R.string.dialog_title));

        final TextView contenu = (TextView) selectedView.findViewById(R.id.content_text);
        contenu.setText(activity.getResources().getString(R.string.dialog_content));


        final EditText titleInput = (EditText) selectedView
                .findViewById(R.id.title_input);
        final EditText contentInput = (EditText) selectedView
                .findViewById(R.id.content_input);
        final TimePicker timeInput = (TimePicker) selectedView.findViewById(R.id.time);
        final TextView dateSelected = (TextView) selectedView.findViewById(R.id.date_text);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.button_add),
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            public void onClick(DialogInterface dialog, int id) {
                                inputError = checkInput(titleInput, dateSelected);
                                if (!inputError) {
                                    String title = titleInput.getText().toString();
                                    String content = contentInput.getText().toString();
                                    String date = dateSelected.getText().toString();

                                    input.add(title);
                                    input.add(content);
                                    input.add(date);

                                    int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                                    String time;
                                    if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
                                        time = timeInput.getHour() + ":" + timeInput.getMinute();
                                    } else {
                                        time = timeInput.getCurrentHour() + ":" + timeInput.getCurrentMinute();
                                    }

                                    input.add(time);

                                    if (type.equals("create")) {
                                        setNotification(title, content, date, time);
                                        IFM.writeListFile(input.get(0), input.get(1), input.get(2), input.get(3));
                                    } else if (type.equals("edit")) {
                                        IFM.replaceItem(position, input.get(0), input.get(1), input.get(2), input.get(3));
                                    }

                                    // refresh to do list
                                    ((ToDoFragment)fragment).refreshList();
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

        setDialogCancel(alertDialog);
    }

    /**
     * Makes back arrow close the dialog
     */
    private void setDialogCancel(final AlertDialog alertDialog) {
        alertDialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    alertDialog.dismiss();
                }
                return true;
            }
        });
    }

    /**
     * Checks input validity
     */
    private boolean checkInput(EditText titleInput, TextView dateSelected) {
        boolean wrong = false;

        if (titleInput.getText().toString().trim().equals("")) {
            titleInput.setError("Required");
            wrong = true;
        } else {
            titleInput.setError(null);
        }
        if (dateSelected.getText().toString().trim().equals("")) {
            dateSelected.setError("Required");
            wrong = true;
        } else {
            dateSelected.setError(null);
        }

        return wrong;
    }

    /**
     * Sets a notification to be displayed at the given time and date.
     */
    private void setNotification(String title, String content, String date, String time) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

            boolean allowNotifictions = sharedPref.getBoolean(SettingsFragment.KEY_PREF_NOTIFICATIONS, true);

            if (allowNotifictions) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
                Date fullDate = sdf.parse(date + " " + time);

                long ms = fullDate.getTime() - System.currentTimeMillis();

                ((TaskForgeActivity) activity).scheduleNotification(((TaskForgeActivity) activity).getNotification(title, content), ms);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
