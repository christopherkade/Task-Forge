package kade_c.taskforge.fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kade_c.taskforge.InternalFilesManager;
import kade_c.taskforge.R;
import kade_c.taskforge.ToDoArrayAdapter;


/**
 * Fragment that handles the display of the To Do list for the selected tab
 */
// TODO: Add edition + consultation
public class ToDoFragment extends Fragment {
    private View view;

    private String tabSelected;

    private String email;

    private InternalFilesManager IFM;

    private ArrayList<String> input;

    private boolean inputError;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_todo, container, false);

        // Save name of the tab selected.
        tabSelected = getArguments().getString("name");
        email = getArguments().getString("email");

        refreshList();

        // Listener for the FAB
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                input = new ArrayList<>();
                createTODODialog();
            }
        });

        handleListViewActions();

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
                // your first action code
                return true;
            case R.id.delete:
                deleteTODO(index);
                // your second action code
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Handles the listeners for our ListView (long click and click)
     */
    private void handleListViewActions() {
        ListView list = (ListView) view.findViewById(R.id.list);

        registerForContextMenu(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "Item click",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteTODO(int position) {
        IFM.deleteItem(position);
        refreshList();
    }

//    public View getViewByPosition(int pos, ListView listView) {
//        final int firstListItemPosition = listView.getFirstVisiblePosition();
//        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
//
//        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
//            return listView.getAdapter().getView(pos, null, listView);
//        } else {
//            final int childIndex = pos - firstListItemPosition;
//            return listView.getChildAt(childIndex);
//        }
//    }
//
//    private void editTODO(int position) {
//        ListView mListView = (ListView) view.findViewById(R.id.list);
//
//        View editedView = getViewByPosition(position, mListView);
//
//    }

    /**
     * Handles the TO DO Dialog
     */
    // TODO: Cleanup this method
    private void createTODODialog() {
        LayoutInflater li = LayoutInflater.from(getContext());
        final View promptsView = li.inflate(R.layout.dialog_todo_layout, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        alertDialogBuilder.setView(promptsView);

        final EditText titleInput = (EditText) promptsView
                .findViewById(R.id.title_input);
        final EditText contentInput = (EditText) promptsView
                .findViewById(R.id.content_input);

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

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                inputError = checkInput(titleInput, dateSelected);
                                if (!inputError) {
                                    input.add(titleInput.getText().toString());
                                    input.add(contentInput.getText().toString());
                                    input.add(dateSelected.getText().toString());

                                    // write input in file
                                    IFM.writeListFile(input.get(0), input.get(1), input.get(2));

                                    // refresh to do list
                                    refreshList();
                                }
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
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
    private void refreshList() {
        ListView mListView = (ListView) view.findViewById(R.id.list);
        ArrayList<String> lines;
        IFM = new InternalFilesManager(getContext(), getActivity(), tabSelected, email);

        // Get lines to display in current tab
        lines = IFM.readListFile();

        // TODO: Only display files for good e-mail

        // Remove two first lines
        if (lines.size() > 0) {
            if (lines.get(0).equals(email + "\n"))
                lines.remove(0);
            if (lines.get(0).equals(tabSelected + "\n"))
                lines.remove(0);
        }

        final ToDoArrayAdapter adapter = new ToDoArrayAdapter(getActivity(), lines);
        mListView.setAdapter(adapter);
    }
}
