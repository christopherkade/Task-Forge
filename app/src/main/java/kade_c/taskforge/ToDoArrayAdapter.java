package kade_c.taskforge;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Adapter for our To Do ListView
 */
public class ToDoArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> values;

    public ToDoArrayAdapter(Context context, ArrayList<String> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.todo_list_layout, parent, false);

        String currentTodo = values.get(position);
        String[] todoArray = currentTodo.split(" \\| ");
        String title = todoArray[0];
        String content = todoArray[1];
        String date = todoArray[2];
        String checked = todoArray[3];

        TextView titleTextView = (TextView) rowView.findViewById(R.id.title);
        titleTextView.setText(title);

        TextView dateTextView = (TextView) rowView.findViewById(R.id.date);
        dateTextView.setText(date);

        TextView contentTextView = (TextView) rowView.findViewById(R.id.content);
        contentTextView.setText(content);

        final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkbox);

        // Catches the click of a checkbox
        checkBox.setOnClickListener(new View.OnClickListener() {

            /**
             * User clicks on checkbox, change state in file
             * @param arg0
             */
            @Override
            public void onClick(View arg0) {
                final boolean isChecked = checkBox.isChecked();
                // TODO: Change state in file when box clicked (to conserve state between fragments)
//                InternalFilesManager IFM = new InternalFilesManager(getContext(), new TaskForgeActivity());
//
//                IFM.changeCheckBoxState(position, isChecked);
            }
        });

        if (checked.equals("true")) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        return rowView;
    }
}
