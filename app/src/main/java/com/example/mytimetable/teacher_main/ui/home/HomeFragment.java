package com.example.mytimetable.teacher_main.ui.home;


import static android.app.Activity.RESULT_OK;
import static com.example.mytimetable.teacher_main.TeacherMainActivity.REQUEST_ADD_MODULE;
import static com.example.mytimetable.utils.ListUtils.getModulesFromFirestore;
import static com.example.mytimetable.utils.NavigationUtils.getAccountID;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytimetable.R;
import com.example.mytimetable.adapters.CalendarAdapter;
import com.example.mytimetable.module.ListModuleActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE,\nd MMMM", Locale.getDefault());
    private final SimpleDateFormat fireStoreDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final Calendar calendar = Calendar.getInstance();
    private FloatingActionButton fabAddModuleToTimetable;
    private TextView textViewDatePicker;
    private RecyclerView recyclerView;
    private CalendarAdapter adapter;
    private ImageButton buttonPreviousDate,
            buttonNextDate;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        initializeRecyclerView();
        attachFabAddModuleToTimetableListeners();
        attachTextViewDatePickerListeners();
        setTextViewCurrentDate(textViewDatePicker);
        setTextViewPreviousDate(textViewDatePicker);
        setTextViewNextDate(textViewDatePicker);
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeRecyclerView();
    }

    //Method for initializing the XML components
    private void initializeViews(View view) {
        fabAddModuleToTimetable = view.findViewById(R.id.fab_teacher);
        textViewDatePicker = view.findViewById(R.id.textView_date_picker);
        buttonPreviousDate = view.findViewById(R.id.button_previous_date);
        buttonNextDate = view.findViewById(R.id.button_next_date);
        recyclerView = view.findViewById(R.id.calendar_recycler);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.VERTICAL,
                        false));
    }

    //Method for initializing the Recycler View
    private void initializeRecyclerView() {
        getModulesFromFirestore(getContext(), recyclerView, adapter,
                fireStoreDateFormat.format(calendar.getTime()),
                null, getAccountID(), true);
    }

    //Listener that opens the List Module Page and waits for completion
    private void attachFabAddModuleToTimetableListeners() {
        fabAddModuleToTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ListModuleActivity.class);
                startActivityForResult(intent, REQUEST_ADD_MODULE);
            }
        });
    }

    //Listener that shows the date picker dialog
    private void attachTextViewDatePickerListeners() {
        textViewDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showViewDatePickerDialog();
            }
        });
    }

    //The date picker dialog
    private void showViewDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    //On selecting a date, will display the selected date
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        textViewDatePicker.setText(simpleDateFormat.format(calendar.getTime()));

        initializeRecyclerView();
    }

    //Setting the TextView with the current date
    private void setTextViewCurrentDate(TextView textViewDatePicker) {
        textViewDatePicker.setText(simpleDateFormat.format(calendar.getTime()));
    }

    //On pressing the back arrow button, it shows the previous date
    private void setTextViewPreviousDate(TextView textViewDatePicker) {
        buttonPreviousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DATE, -1);
                textViewDatePicker.setText(simpleDateFormat.format(calendar.getTime()));

                initializeRecyclerView();
            }
        });
    }

    //On pressing the forward arrow button, it shows the next date
    private void setTextViewNextDate(TextView textViewDatePicker) {
        buttonNextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DATE, 1);
                textViewDatePicker.setText(simpleDateFormat.format(calendar.getTime()));

                initializeRecyclerView();
            }
        });
    }

    //Method that shows a Snackbar message when a Module has been successfully added
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Snackbar.make(fabAddModuleToTimetable, "Module successfully inserted!", BaseTransientBottomBar.LENGTH_LONG)
                    .show();
            initializeRecyclerView();
        }
    }
}
