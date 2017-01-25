package kade_c.taskforge.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import kade_c.taskforge.utils.DialogHandler;
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

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_todo, container, false);

        // Save name of the tab selected.
        if (!getArguments().isEmpty()) {
            tabSelected = getArguments().getString("name");
        }

        refreshList();
        handleListViewClick();
        handleFAB();

        ((TaskForgeActivity)getActivity()).setPreviousTabName(tabSelected);

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
                new DialogHandler(getActivity(), ToDoFragment.this, tabSelected).editTODO(index);
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
                new DialogHandler(getActivity(), ToDoFragment.this, tabSelected).createTODO();
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

                ((TaskForgeActivity)getActivity()).replaceFragment(fragment, false);
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
     * Get lines in internal files and set them into the ListView
     */
    public void refreshList() {
        ListView mListView = (ListView) view.findViewById(R.id.list);
        ArrayList<String> lines;
        IFM = new InternalFilesManager(getContext(), getActivity(), tabSelected);

        // Get lines to display in current tab
        lines = IFM.readListFile();

        final ToDoArrayAdapter adapter = new ToDoArrayAdapter(getActivity(), lines, IFM, this);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        mListView.setAdapter(adapter);
    }
}
