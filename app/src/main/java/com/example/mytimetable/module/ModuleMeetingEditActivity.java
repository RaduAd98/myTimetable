package com.example.mytimetable.module;

import static com.example.mytimetable.adapters.CalendarAdapter.EXTRA_DATE;
import static com.example.mytimetable.adapters.CalendarAdapter.EXTRA_DOCUMENT_ID;
import static com.example.mytimetable.adapters.CalendarAdapter.EXTRA_GROUP_NAME;
import static com.example.mytimetable.adapters.CalendarAdapter.EXTRA_HOUR;
import static com.example.mytimetable.adapters.CalendarAdapter.EXTRA_MODULE_NAME;
import static com.example.mytimetable.adapters.CalendarAdapter.EXTRA_ROOM;
import static com.example.mytimetable.adapters.CalendarAdapter.EXTRA_SEMESTER;
import static com.example.mytimetable.utils.CalendarUtils.getCalendarFromString;
import static com.example.mytimetable.utils.CalendarUtils.getDayOfWeek;
import static com.example.mytimetable.utils.CalendarUtils.getStringDate;
import static com.example.mytimetable.utils.NavigationUtils.initializeSpinner;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytimetable.R;
import com.example.mytimetable.utils.SpinnerUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ModuleMeetingEditActivity extends AppCompatActivity {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DatePicker datePicker;
    private Button btnUpdateTimetable;
    private Calendar calendar;
    private String roomName,
            selectedHour,
            moduleDay;
    private Spinner hoursSpinner,
            roomsSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_meeting_edit);

        initializeViews();

        setTitle("Edit " + getIntent().getStringExtra(EXTRA_MODULE_NAME));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getRoomsForSpinner();
        setSpinnerHours();

        attachDatePickerListeners();

    }

    //Method for initializing the XML components
    private void initializeViews() {
        datePicker = findViewById(R.id.date_picker);
        hoursSpinner = findViewById(R.id.spinner_hours);
        roomsSpinner = findViewById(R.id.spinner_rooms);
        btnUpdateTimetable = findViewById(R.id.button_edit_module_meeting);
        calendar = getCalendarFromString(getIntent().getStringExtra(EXTRA_DATE));
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        moduleDay = getDayOfWeek(calendar);
    }

    //Method that populates the Spinner with the available Hours
    private void setSpinnerHours() {
        SpinnerUtils.setSpinnerHours(ModuleMeetingEditActivity.this,
                hoursSpinner,
                getIntent().getStringExtra(EXTRA_SEMESTER),
                getIntent().getStringExtra(EXTRA_GROUP_NAME),
                getIntent().getStringExtra(EXTRA_ROOM),
                true, getIntent().getStringExtra(EXTRA_DATE),
                getStringDate(calendar),
                getIntent().getStringExtra(EXTRA_HOUR));
    }

    //Method that offers an Action Bar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Listener for the calendar
    private void attachDatePickerListeners() {
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                moduleDay = getDayOfWeek(calendar);
                setSpinnerHours();
            }
        });

        //Listener for the Spinner that has the Rooms listed
        roomsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                roomName = roomsSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Listener for the Spinner that has the Hours listed
        hoursSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedHour = hoursSpinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Listener for the Update Timetable button
        btnUpdateTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateModulesParticipants();
                Toast.makeText(ModuleMeetingEditActivity.this, "Module edited successfully!", Toast.LENGTH_SHORT).show();
                ModuleMeetingEditActivity.this.onBackPressed();
            }
        });
    }

    //Method that gets the list of Rooms
    private void getRoomsForSpinner() {
        firebaseFirestore.collection("Rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listRooms = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String nameRoom = document.getString("roomName");
                                listRooms.add(nameRoom);
                            }

                            initializeSpinner(ModuleMeetingEditActivity.this, roomsSpinner, listRooms);
                            roomsSpinner.setSelection(listRooms.indexOf(getIntent().getStringExtra(EXTRA_ROOM)));

                        } else {
                            Toast.makeText(ModuleMeetingEditActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Method that updates the Modules-Participants Firestore Collection
    private void updateModulesParticipants() {
        firebaseFirestore.collection("Modules-Participants")
                .document(getIntent().getStringExtra(EXTRA_DOCUMENT_ID))
                .update("moduleDate", getStringDate(calendar),
                        "moduleDay", moduleDay,
                        "roomName", roomName,
                        "occupiedHour", selectedHour);
    }
}