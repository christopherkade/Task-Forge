package kade_c.taskforge.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kade_c.taskforge.utils.InternalFilesManager;
import kade_c.taskforge.R;
import kade_c.taskforge.activities.TaskForgeActivity;
import kade_c.taskforge.utils.ToDoArrayAdapter;


/**
 * Fragment that handles the display of the To Do list for the selected tab
 */
public class ToDoFragment extends Fragment {

    private View view;

    private String tabSelected;

    private InternalFilesManager IFM;

    private ArrayList<String> input;

    private boolean inputError;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_todo, container, false);

        // Save name of the tab selected.
        if (!getArguments().isEmpty()) {
            tabSelected = getArguments().getString("name");
        }

        refreshList();
        handleFAB();
        handleListViewClick();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(tabSelected);
    }

    /**
     * Inflates the context menu
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.list) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.todo_context_menu, menu);
        }
    }

    /**
     * Called when context menu item is clicked
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;

        switch (item.getItemId()) {
            case R.id.edit:
                editTODO(index);
                return true;
            case R.id.delete:
                deleteTODO(index);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Sets Floating Action Button listener
     */
    private void handleFAB() {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                input = new ArrayList<>();
                createTODODialog();
            }
        });
    }

    /**
     * Catches clicks on the To do list
     */
    private void handleListViewClick() {
        final ListView list = (ListView) view.findViewById(R.id.list);

        registerForContextMenu(list);

        // Handles click on ListView
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View clickedItemView = getViewByPosition(i, list);

                Fragment fragment = new ToDoConsultFragment();

                // TextViews in the selected row.
                TextView clickedTitle = (TextView) clickedItemView.findViewById(R.id.title);
                TextView clickedContent = (TextView) clickedItemView.findViewById(R.id.content);
                TextView clickedDate = (TextView) clickedItemView.findViewById(R.id.date);
                TextView clickedTime = (TextView) clickedItemView.findViewById(R.id.time);

                // Bundle containing data in the row selected.
                Bundle bundle = new Bundle();
                bundle.putString("title", clickedTitle.getText().toString());
                bundle.putString("content", clickedContent.getText().toString());
                bundle.putString("date", clickedDate.getText().toString());
                bundle.putString("time", clickedTime.getText().toString());
                bundle.putString("tab", tabSelected);

                fragment.setArguments(bundle);

                ((TaskForgeActivity)getActivity()).replaceFragment(fragment);
            }
        });
    }

    /**
     * Deletes the item at position
     */
    private void deleteTODO(int position) {
        IFM.deleteItem(position);
        refreshList();
    }

    /**
     * Returns the view at the given position on the ListView
     */
    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    /**
     * Handles edition (Dialog...)
     */
    private void editTODO(final int position) {
        input = new ArrayList<>();
        ListView mListView = (ListView) view.findViewById(R.id.list);

        // Selected row View.
        View editedView = getViewByPosition(position, mListView);

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

        LayoutInflater li = LayoutInflater.from(getContext());
        final View promptsView = li.inflate(R.layout.dialog_todo_layout, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
     * Handles the TO DO Dialog
     */
    private void createTODODialog() {
        LayoutInflater li = LayoutInflater.from(getContext());

        // Inflate dialog view
        final View promptsView = li.inflate(R.layout.dialog_todo_layout, null);

        // Dialog builder
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
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
     * Builds and handles the Edition / Creation dialog
     */
    private void buildDialog(AlertDialog.Builder alertDialogBuilder, View selectedView,
                             final String type, final int position) {
        final TextView title = (TextView) selectedView.findViewById(R.id.title_text);
        title.setText(getResources().getString(R.string.dialog_title));

        final TextView contenu = (TextView) selectedView.findViewById(R.id.content_text);
        contenu.setText(getResources().getString(R.string.dialog_content));


        final EditText titleInput = (EditText) selectedView
                .findViewById(R.id.title_input);
        final EditText contentInput = (EditText) selectedView
                .findViewById(R.id.content_input);
        final TimePicker timeInput = (TimePicker) selectedView.findViewById(R.id.time);
        final TextView dateSelected = (TextView) selectedView.findViewById(R.id.date_text);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.button_add),
                        new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            public void onClick(DialogInterface dialog, int id) {
                                inputError = checkInput(titleInput, dateSelected);
                                if (!inputError) {
                                    input.add(titleInput.getText().toString());
                                    input.add(contentInput.getText().toString());
                                    input.add(dateSelected.getText().toString());

                                    int currentApiVersion = android.os.Build.VERSION.SDK_INT;
                                    String time;
                                    if (currentApiVersion > android.os.Build.VERSION_CODES.LOLLIPOP_MR1){
                                        time = timeInput.getHour() + ":" + timeInput.getMinute();
                                    } else {
                                        time = timeInput.getCurrentHour() + ":" + timeInput.getCurrentMinute();
                                    }

                                    input.add(time);

                                    if (type.equals("create")) {
                                        // write input in file
                                        IFM.writeListFile(input.get(0), input.get(1), input.get(2), input.get(3));
                                    } else if (type.equals("edit")) {
                                        IFM.replaceItem(position, input.get(0), input.get(1), input.get(2), input.get(3));
                                    }

                                    // refresh to do list
                                    refreshList();
                                }
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.button_cancel),
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
     * Get lines in internal files and set them into the ListView
     */
    public void refreshList() {
        ListView mListView = (ListView) view.findViewById(R.id.list);
        ArrayList<String> lines;
        IFM = new InternalFilesManager(getContext(), getActivity(), tabSelected);

        // Get lines to display in current tab
        lines = IFM.readListFile();

        final ToDoArrayAdapter adapter = new ToDoArrayAdapter(getActivity(), lines, IFM, this);
        mListView.setAdapter(adapter);
    }
}
