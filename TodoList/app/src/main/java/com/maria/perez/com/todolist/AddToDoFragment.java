package com.maria.perez.com.todolist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.Calendar;

import static android.R.attr.category;
import static android.R.attr.id;

public class AddToDoFragment extends DialogFragment{

    private EditText toDo;
    private DatePicker dp;
    private Button add;
    private final String TAG = "addtodofragment";
    private Spinner spinner;

    public AddToDoFragment() {
    }

    //To have a way for the activity to get the data from the dialog
    public interface OnDialogCloseListener {
        void closeDialog(int year, int month, int day, String description, String category, boolean completed);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);

        // Add category spinner
        spinner = (Spinner) view.findViewById(R.id.category_spinner);

        //fill the created spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.picked_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);



        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dp.updateDate(year, month, day);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnDialogCloseListener activity = (OnDialogCloseListener) getActivity();
                Log.d(TAG, "id: " + id);
                activity.closeDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(), spinner.getSelectedItem().toString(), false);
                AddToDoFragment.this.dismiss();
            }
        });

        return view;
    }
}



