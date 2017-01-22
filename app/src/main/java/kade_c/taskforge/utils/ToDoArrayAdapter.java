package kade_c.taskforge.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import kade_c.taskforge.R;
import kade_c.taskforge.fragments.SettingsFragment;
import kade_c.taskforge.fragments.ToDoFragment;

/**
 * Adapter for our To Do ListView
 */
public class ToDoArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final Fragment fContext;
    private final ArrayList<String> values;
    private InternalFilesManager IFM;

    public ToDoArrayAdapter(Context context, ArrayList<String> values, InternalFilesManager IFM, Fragment fContext) {
        super(context, -1, values);
        this.context = context;
        this.fContext = fContext;
        this.values = values;
        this.IFM = IFM;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View rowView = inflater.inflate(R.layout.todo_list_layout, parent, false);

        String currentTodo = values.get(position);
        String[] todoArray = currentTodo.split(" \\| ");
        String title = todoArray[0];
        final String content = todoArray[1];
        String date = todoArray[2];
        String time = todoArray[3];
        String checked = todoArray[4];

        TextView titleTextView = (TextView) rowView.findViewById(R.id.title);
        titleTextView.setText(title);

        TextView dateTextView = (TextView) rowView.findViewById(R.id.date);
        dateTextView.setText(date);

        TextView timeTextView = (TextView) rowView.findViewById(R.id.time);
        timeTextView.setText(time);

        TextView contentTextView = (TextView) rowView.findViewById(R.id.content);
        contentTextView.setText(content);

        handleCheckBox(rowView, checked, position);

        return rowView;
    }

    private void handleCheckBox(View rowView, String checked, final int position) {
        final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);

        /**
         * When user clicks on CheckBox, change state in file
         */
        checkBox.setOnClickListener(new View.OnClickListener() {

            /**
             * User clicks on checkbox, change state in file
             */
            @Override
            public void onClick(View arg0) {
                final boolean isChecked = checkBox.isChecked();
                IFM.changeCheckBoxState(position, isChecked);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                Boolean deleteOnCheckPref = sharedPref.getBoolean(SettingsFragment.KEY_PREF_DEL_ON_CHECK, false);
                Boolean moveOnCheckPref = sharedPref.getBoolean(SettingsFragment.KEY_PREF_MOVE_ON_CHECK, false);

                // If Delete on check is set, delete item and refresh list
                if (deleteOnCheckPref && isChecked) {
                    IFM.deleteItem(position);
                    ((ToDoFragment)fContext).refreshList();
                } else if (moveOnCheckPref && isChecked) {
                    IFM.moveItemToEnd(position);
                    ((ToDoFragment)fContext).refreshList();
                }
            }
        });

        if (checked.equals("true\n")) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }
    }
}
