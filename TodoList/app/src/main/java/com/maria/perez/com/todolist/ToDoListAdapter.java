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
import com.maria.perez.com.todolist.MainActivity;

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
        void onCheckClick(int pos, boolean status, long id);
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
        private TextView descriptionTextView;
        private TextView dueTextView;
        private TextView categoryTextView;

        private String duedate;
        private String description;
        private String category;

        private long id;

        private boolean completed;
        private CheckBox checkBox;

        // Create ItemHolder object
        ItemHolder(View view) {
            super(view);
            descriptionTextView = (TextView) view.findViewById(R.id.description);
            dueTextView = (TextView) view.findViewById(R.id.dueDate);

            categoryTextView = (TextView) view.findViewById(R.id.category);
            checkBox = (CheckBox) view.findViewById(R.id.checked);

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

            // Added binding data for category and completed
            category = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY));
            completed = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_COMPLETED)));

            descriptionTextView.setText(description);
            dueTextView.setText(duedate);

            checkBox.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(((CheckBox) v).isChecked()){
                        listener.onCheckClick(getAdapterPosition(), true, id);
                    } else{
                        listener.onCheckClick(getAdapterPosition(), false, id);
                    }
                }
            });
            holder.itemView.setTag(id);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onItemClick(pos, description, duedate, id, category, completed);
        }
    }

}
