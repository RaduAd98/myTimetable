package com.example.mytimetable.module;

import static com.example.mytimetable.utils.CalendarUtils.generateSemesterDates;
import static com.example.mytimetable.utils.CalendarUtils.getFirstWeek;
import static com.example.mytimetable.utils.NavigationUtils.getAccountID;
import static com.example.mytimetable.utils.NavigationUtils.initializeSpinner;
import static com.example.mytimetable.utils.SpinnerUtils.setSpinnerHours;
import static java.util.Calendar.YEAR;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mytimetable.R;
import com.example.mytimetable.utils.CalendarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListModuleActivity extends AppCompatActivity {

    private final List<String> listModuleNames = new ArrayList<>(),
            listModuleIDs = new ArrayList<>(),
            listRooms = new ArrayList<>(),
            listGroups = new ArrayList<>(),
            listGroupsID = new ArrayList<>();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private List<String> listStudents = new ArrayList<>();
    private ImageButton btnViewList;
    private Button btnAddModule;
    private String selectedModuleID,
            selectedGroupID,
            teacherName,
            groupName,
            roomName,
            dayName,
            semesterName;
    private Spinner spinnerModules,
            spinnerRooms,
            spinnerHours,
            spinnerGroups,
            spinnerDays,
            spinnerSemesters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_module);

        initializeViews();

        setTitle("List Module to Timetable");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeSpinner(ListModuleActivity.this, spinnerDays, R.array.days);
        initializeSpinner(ListModuleActivity.this, spinnerSemesters, R.array.semesters);

        getModulesForSpinner();
        getRoomsForSpinner();
        getGroupsForSpinner();

        getTeacherName();

        attachSpinnerModulesListeners();
        attachSpinnerGroupsListeners();
        attachSpinnerDaysListeners();
        attachSpinnerRoomsListeners();
        attachSpinnerSemestersListeners();

        attachBtnAddModuleListeners();
        attachBtnViewListListeners();
    }

    //Method that offers an Action Bar back button
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //Listener for the Spinner that has the Modules as data
    private void attachSpinnerModulesListeners() {
        spinnerModules.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedModuleID = listModuleIDs.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Listener for the Spinner that has the Semesters as data
    private void attachSpinnerSemestersListeners() {
        spinnerSemesters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                semesterName = spinnerSemesters.getSelectedItem().toString();
                setSpinnerHours(ListModuleActivity.this, spinnerHours,
                        semesterName, groupName, roomName, false, null,
                        getFirstWeek(Integer.parseInt(semesterName), spinnerDays.getSelectedItemPosition()),
                        null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //Listener for the Spinner that has the Rooms as data
    private void attachSpinnerRoomsListeners() {
        spinnerRooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                roomName = spinnerRooms.getSelectedItem().toString();
                setSpinnerHours(ListModuleActivity.this, spinnerHours,
                        semesterName, groupName, roomName, false, null,
                        getFirstWeek(Integer.parseInt(semesterName), spinnerDays.getSelectedItemPosition()),
                        null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    //Listener for the "Update Timetable" button
    private void attachBtnAddModuleListeners() {
        btnAddModule.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                int semester;
                int dayOfWeekSpinnerPosition = spinnerDays.getSelectedItemPosition();
                if (spinnerSemesters.getSelectedItemPosition() == 0) {
                    semester = CalendarUtils.FIRST_SEMESTER;
                } else {
                    semester = CalendarUtils.SECOND_SEMESTER;
                }

                List<String> finalDatesList = generateSemesterDates(semester, dayOfWeekSpinnerPosition);

                String moduleName = spinnerModules.getSelectedItem().toString();
                String occupiedHours = spinnerHours.getSelectedItem().toString();
                roomName = spinnerRooms.getSelectedItem().toString();
                groupName = spinnerGroups.getSelectedItem().toString();
                dayName = spinnerDays.getSelectedItem().toString();
                semesterName = spinnerSemesters.getSelectedItem().toString();

                for (String date : finalDatesList) {

                    //Modules-Participants
                    CollectionReference collectionReferenceModules =
                            firebaseFirestore.collection("Modules-Participants");

                    Map<String, Object> modules = new HashMap<>();
                    modules.put("teacherID", getAccountID());
                    modules.put("moduleName", moduleName);
                    modules.put("teacherName", teacherName);
                    modules.put("groupName", groupName);
                    modules.put("groupID", selectedGroupID);
                    modules.put("moduleDate", date);
                    modules.put("moduleDay", dayName);
                    modules.put("occupiedHour", occupiedHours);
                    modules.put("semesterName", semesterName);
                    modules.put("roomName", roomName);
                    modules.put("year", String.valueOf(Calendar.getInstance().get(YEAR)));

                    collectionReferenceModules.add(modules);

                }

                //Modules
                firebaseFirestore
                        .collection("Modules")
                        .document(selectedModuleID)
                        .update("isUsed", true);

                setResult(RESULT_OK);
                finish();
            }
        });
    }

    //Show the list of Students in the selected Group
    private void attachBtnViewListListeners() {
        btnViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.view_students_list, null);
                ListView listView = view.findViewById(R.id.listView_students);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ListModuleActivity.this, android.R.layout.simple_list_item_1, listStudents);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                new MaterialAlertDialogBuilder(ListModuleActivity.this)
                        .setView(view).create().show();
            }
        });
    }

    //Get the Group ID then the Students in order to populate the Spinner
    private void attachSpinnerGroupsListeners() {
        spinnerGroups.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                groupName = spinnerGroups.getSelectedItem().toString();
                setSpinnerHours(ListModuleActivity.this, spinnerHours,
                        semesterName, groupName, roomName, false, null,
                        getFirstWeek(Integer.parseInt(semesterName), spinnerDays.getSelectedItemPosition()),
                        null);
                firebaseFirestore.collection("Groups")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    selectedGroupID = listGroupsID.get(position);
                                    firebaseFirestore.collection("Groups")
                                            .document(selectedGroupID).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    listStudents = (List<String>) task.getResult().get("students");
                                                }
                                            });
                                } else {
                                    Toast.makeText(ListModuleActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //Listener for the Spinner that has the Days as data
    private void attachSpinnerDaysListeners() {
        spinnerDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                dayName = spinnerDays.getSelectedItem().toString();
                setSpinnerHours(ListModuleActivity.this, spinnerHours,
                        semesterName, groupName, roomName, false, null,
                        getFirstWeek(Integer.parseInt(semesterName), spinnerDays.getSelectedItemPosition()),
                        null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //Getting the list of Groups and their IDs to be used for Spinners
    private void getGroupsForSpinner() {
        firebaseFirestore.collection("Groups")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String result = (String) document.get("groupName");
                                listGroups.add(result);
                                listGroupsID.add(document.getId());
                            }
                            initializeSpinner(ListModuleActivity.this, spinnerGroups, listGroups);
                        } else {
                            Toast.makeText(ListModuleActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Getting the list of existing Rooms
    private void getRoomsForSpinner() {
        firebaseFirestore.collection("Rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String result = (String) document.get("roomName");
                                listRooms.add(result);
                            }
                            initializeSpinner(ListModuleActivity.this, spinnerRooms, listRooms);
                        } else {
                            Toast.makeText(ListModuleActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Getting the list of Modules taught by the Teacher
    private void getModulesForSpinner() {
        firebaseFirestore.collection("Modules")
                .whereEqualTo("teacherID", getAccountID())
                .whereEqualTo("isUsed", false).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listModuleNames.clear();
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                listModuleNames.add(snapshot.getString("moduleName"));
                                listModuleIDs.add(snapshot.getId());
                            }
                            if (listModuleIDs.isEmpty()) {
                                new MaterialAlertDialogBuilder(ListModuleActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setCancelable(false)
                                        .setTitle("Error!")
                                        .setMessage("No Modules listed for this account!\nPlease list at least one!")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ListModuleActivity.super.onBackPressed();
                                            }
                                        })
                                        .show();
                            } else {
                                initializeSpinner(ListModuleActivity.this, spinnerModules, listModuleNames);
                            }
                        }
                    }
                });
    }

    //Method that gets the Teacher name from the "Accounts" Firestore Collection
    private void getTeacherName() {
        firebaseFirestore
                .collection("Accounts")
                .document(getAccountID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        teacherName = documentSnapshot.getString("fullName");
                    }
                });
    }

    //Method for initializing the XML components
    private void initializeViews() {
        spinnerModules = findViewById(R.id.spinner_modules);
        spinnerRooms = findViewById(R.id.spinner_rooms);
        spinnerHours = findViewById(R.id.spinner_hours);
        spinnerGroups = findViewById(R.id.spinner_groups);
        spinnerDays = findViewById(R.id.spinner_days);
        spinnerSemesters = findViewById(R.id.spinner_semesters);
        btnAddModule = findViewById(R.id.button_add_module);
        btnViewList = findViewById(R.id.button_listView_students);
    }
}