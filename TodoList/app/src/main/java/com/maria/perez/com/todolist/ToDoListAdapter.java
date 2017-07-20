package com.maria.perez.com.todolist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.maria.perez.com.todolist.data.Contract;
import com.maria.perez.com.todolist.data.ToDoItem;

import java.util.ArrayList;


public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ItemHolder> {

    private Cursor cursor;
    private ItemClickListener listener;
    private String TAG = "todolistadapter";

    /**
     * onCreateViewHolder creates the viewholders that will hold the various pieces of data
     * @param parent
     * @param viewType
     * @return ItemHolder holder
     */
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.item, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    /**
     * onBindViewHolder binds the info to the viewholder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(holder, position);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    /**
     * ItemClickListener - Is an interface that is used in the MainActivity for when things are picked
     */
    public interface ItemClickListener {
        void onItemClick(int pos, String description, String duedate, long id, String category, boolean completed);
    }

    /**
     * ToDoListAdapter
     * This method creates the Adapter object
     * @param cursor
     * @param listener
     */
    public ToDoListAdapter(Cursor cursor, ItemClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }

    public void swapCursor(Cursor newCursor){
        if (cursor != null) cursor.close();
        cursor = newCursor;
        if (newCursor != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView descr;
        private TextView due;
        private String duedate;
        private String description;
        private  String category;
        private boolean completed;
        private long id;
        private CheckBox checkBox;



        // Create ItemHolder object
        ItemHolder(View view) {
            super(view);
            descr = (TextView) view.findViewById(R.id.description);
            due = (TextView) view.findViewById(R.id.dueDate);

            checkBox = (CheckBox) view.findViewById(R.id.checkbox);

            view.setOnClickListener(this);
        }

        /**
         * bindItems - This method binds the data to the itemholder. 
         * @param holder
         * @param pos
         */
        public void bind(ItemHolder holder, int pos) {
            cursor.moveToPosition(pos);
            id = cursor.getLong(cursor.getColumnIndex(Contract.TABLE_TODO._ID));
            Log.d(TAG, "deleting id: " + id);

            duedate = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE));
            description = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION));
            category = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY));

            descr.setText(description);
            due.setText(duedate);
            holder.itemView.setTag(id);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onItemClick(pos, description, duedate, id, category, completed);
        }
    }

}
