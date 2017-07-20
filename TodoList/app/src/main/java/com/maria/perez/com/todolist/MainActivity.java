package com.maria.perez.com.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.maria.perez.com.todolist.data.Contract;
import com.maria.perez.com.todolist.data.DBHelper;
import com.maria.perez.com.todolist.data.ToDoItem;

public class MainActivity extends AppCompatActivity implements AddToDoFragment.OnDialogCloseListener, UpdateToDoFragment.OnUpdateDialogCloseListener{

    private RecyclerView rv;
    private FloatingActionButton button;
    private DBHelper helper;
    private Cursor cursor;
    private SQLiteDatabase db;
    ToDoListAdapter adapter;
    private final String TAG = "mainactivity";

    // set default category to ALL
    private String selectedCategory = "all";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate called in main activity");
        button = (FloatingActionButton) findViewById(R.id.addToDo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddToDoFragment frag = new AddToDoFragment();
                frag.show(fm, "addtodofragment");
            }
        });
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }

    @Override
    protected void onStart() {
        super.onStart();

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();

        checkIfCategorySelected();

        adapter = new ToDoListAdapter(cursor, new ToDoListAdapter.ItemClickListener() {

            @Override
            public void onItemClick(int pos, String description, String duedate, long id, String category, boolean completed) {
                Log.d(TAG, "item click id: " + id);
                String[] dateInfo = duedate.split("-");
                int year = Integer.parseInt(dateInfo[0].replaceAll("\\s",""));
                int month = Integer.parseInt(dateInfo[1].replaceAll("\\s",""));
                int day = Integer.parseInt(dateInfo[2].replaceAll("\\s",""));

                FragmentManager fm = getSupportFragmentManager();

                UpdateToDoFragment frag = UpdateToDoFragment.newInstance(year, month, day, description, id, category, completed);
                frag.show(fm, "updatetodofragment");
            }
        });

        rv.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                Log.d(TAG, "passing id: " + id);
                removeToDo(db, id);
                adapter.swapCursor(getAllItems(db));
            }
        }).attachToRecyclerView(rv);
    }

    @Override
    public void closeDialog(int year, int month, int day, String description, String category, boolean completed) {
        addToDo(db, description, formatDate(year, month, day), category, completed);
        cursor = getAllItems(db);
        adapter.swapCursor(cursor);
    }

    public String formatDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }
    private Cursor getAllItems(SQLiteDatabase db) {
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }

    /**
     * If a category is selected, getCategoryItems method will get all items from that category
     * @param db
     * @return selectedCategory
     */
    private Cursor getCategoryItems(SQLiteDatabase db) {
        String all[] = new String[]{
                Contract.TABLE_TODO._ID,
                Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE,
                Contract.TABLE_TODO.COLUMN_NAME_CATEGORY,
                Contract.TABLE_TODO.COLUMN_NAME_COMPLETED,
        };

        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                all,
                Contract.TABLE_TODO.COLUMN_NAME_CATEGORY + "=?",
                new String[]{selectedCategory},
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }
/////////////////////////////////////////////////////////////////////////////
    private long addToDo(SQLiteDatabase db, String description, String duedate, String category, boolean completed) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);

        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_COMPLETED, completed);

        return db.insert(Contract.TABLE_TODO.TABLE_NAME, null, cv);
    }

    private boolean removeToDo(SQLiteDatabase db, long id) {
        Log.d(TAG, "deleting id: " + id);
        return db.delete(Contract.TABLE_TODO.TABLE_NAME, Contract.TABLE_TODO._ID + "=" + id, null) > 0;
    }


    private int updateToDo(SQLiteDatabase db, int year, int month, int day, String description, long id, String category, boolean completed){

        String duedate = formatDate(year, month - 1, day);

        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);

        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORY, category);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_COMPLETED, completed);

        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }

    @Override
    public void closeUpdateDialog(int year, int month, int day, String description, long id, String category, boolean completed) {
        updateToDo(db, year, month, day, description, id, category, completed);
        checkIfCategorySelected();
        adapter.swapCursor(cursor);
    }

    /**
     * createMenu method
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.categories, menu);
        return true;
    }

    /**
     * What happens when a category is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int clickedItemId = item.getItemId();

        // display category_all
        if(clickedItemId == R.id.category_all){
            selectedCategory = "all";
            adapter.swapCursor(getAllItems(db));
        }

        // display category_school
        if(clickedItemId == R.id.category_school){
            selectedCategory = "school";
            adapter.swapCursor(getCategoryItems(db));
        }

        // display category_work
        if(clickedItemId == R.id.category_work){
            selectedCategory = "work";
            adapter.swapCursor(getCategoryItems(db));
        }

        // display category_personal
        if(clickedItemId == R.id.category_personal){
            selectedCategory = "personal";
            adapter.swapCursor(getCategoryItems(db));
        }

        // display category_other
        if(clickedItemId == R.id.category_other){
            selectedCategory = "other";
            adapter.swapCursor(getCategoryItems(db));
        }

        return onOptionsItemSelected(item);
    }


    /**
     * Checks if a category is selected
     */
    public void checkIfCategorySelected(){
        if(selectedCategory.equals("all")){
            cursor = getAllItems(db);
        }
        else{
            cursor = getCategoryItems(db);
        }
    }

}
