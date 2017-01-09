package kade_c.taskforge;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.todo_list_layout, parent, false);

        String currentTodo = values.get(position);
        String[] todoArray = currentTodo.split(" \\| ");
        String title = todoArray[0];
        String content = todoArray[1];
        String date = todoArray[2];

        TextView titleTextView = (TextView) rowView.findViewById(R.id.title);
        titleTextView.setText(title);

        TextView dateTextView = (TextView) rowView.findViewById(R.id.date);
        dateTextView.setText(date);

        TextView contentTextView = (TextView) rowView.findViewById(R.id.content);
        contentTextView.setText(content);

        return rowView;
    }
}
