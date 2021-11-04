package com.example.mytimetable.teacher_main.ui.create_group;

import static com.example.mytimetable.utils.NavigationUtils.attachSpinnerCloseKeyboardListeners;
import static com.example.mytimetable.utils.NavigationUtils.closeKeyboard;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mytimetable.R;
import com.example.mytimetable.models.StudentModel;
import com.example.mytimetable.teacher_main.ui.add_module.AddModuleFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupFragment extends Fragment {

    private final AddModuleFragment addModuleFragment = new AddModuleFragment();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final List<StudentModel> studentList = new ArrayList<>();
    private TextInputLayout groupName;
    private TextView txtViewStudentsList;
    private Spinner spinnerCourseName;
    private ColorStateList standardColor;
    private Button btnSelectStudents,
            btnCreateGroup;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_group_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        addModuleFragment.setSpinnerCourses(getActivity(), spinnerCourseName);
        getStudentList();
        attachBtnSelectStudentsListeners();
        attachBtnCreateGroupListeners();
        attachSpinnerCloseKeyboardListeners(spinnerCourseName, getActivity(), view);
    }

    //Method for initializing the XML components
    private void initializeViews(View view) {
        groupName = view.findViewById(R.id.layout_group_name_create_group_page);
        spinnerCourseName = view.findViewById(R.id.spinner_course_name_create_group_page);
        btnSelectStudents = view.findViewById(R.id.button_select_students_create_group_page);
        txtViewStudentsList = view.findViewById(R.id.textView_students_list_create_group_page);
        standardColor = txtViewStudentsList.getTextColors();
        btnCreateGroup = view.findViewById(R.id.button_save_create_group_page);
    }


    //Listener for the Select Students Spinner
    private void attachBtnSelectStudentsListeners() {
        btnSelectStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(getActivity(), v);
                new MaterialAlertDialogBuilder(getActivity())
                        .setTitle("Select the Students")
                        .setMultiChoiceItems(getStudentNames(), getSelectedPositions(),
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        studentList.get(which).setSelected(isChecked);
                                    }
                                })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setSavedSelections();
                                if (selectedItemsCount() != 0) {
                                    txtViewStudentsList
                                            .setText(getSelectedStudentsToString());
                                    txtViewStudentsList.setTextColor(standardColor);
                                } else {
                                    txtViewStudentsList
                                            .setText(getResources().getString(R.string.hint_students_list));
                                    txtViewStudentsList.setTextColor(Color.RED);
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create().show();
            }
        });
    }

    //Listener for the Create Group Button
    private void attachBtnCreateGroupListeners() {
        btnCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(getActivity(), v);
                String nameGroup = groupName.getEditText().getText().toString();
                String nameCourse = spinnerCourseName.getSelectedItem().toString();
                if (nameGroup.isEmpty()) {
                    groupName.setError("Please insert a Group name!");
                } else {
                    groupName.setError(null);
                }
                if (selectedItemsCount() == 0) {
                    txtViewStudentsList.setTextColor(Color.RED);
                } else {
                    txtViewStudentsList.setTextColor(standardColor);
                }
                if (!nameGroup.isEmpty() && selectedItemsCount() != 0) {
                    for (String id : getSelectedStudentIds()) {
                        firebaseFirestore
                                .collection("Accounts")
                                .document(id)
                                .update("hasGroup", true);
                    }
                    CollectionReference collectionReference = firebaseFirestore.collection("Groups");
                    Map<String, Object> group = new HashMap<>();
                    group.put("groupName", nameGroup);
                    group.put("courseName", nameCourse);
                    group.put("students", getSelectedStudentNames());
                    group.put("studentsID", getSelectedStudentIds());
                    collectionReference.add(group);
                    Toast.makeText(getActivity(), "Group created successfully!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
            }
        });
    }

    //Method that fetches the list of Students that do not have a Group already
    private void getStudentList() {
        if (!studentList.isEmpty()) {
            studentList.clear();
        }
        firebaseFirestore.collection("Accounts")
                .whereEqualTo("isAdmin", false)
                .whereEqualTo("hasGroup", false)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot documentSnapshot : task.getResult()) {
                        studentList.add(new StudentModel(
                                documentSnapshot.getId(),
                                documentSnapshot.getString("fullName"),
                                false));
                    }
                    //Sanity check to ensure there's always Students to be added to a new Group
                    //If all Students are assigned to a Group, Toast a message and go back to the previous screen
                    if (studentList.isEmpty()) {
                        Toast.makeText(getContext(), "All students are assigned to a group!", Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                }
            }
        });
    }

    //Utility methods for Students List
    private String[] getStudentNames() {
        String[] names = new String[studentList.size()];
        int index = 0;
        for (StudentModel s : studentList) {
            names[index++] = s.getStudentName();
        }
        return names;
    }

    private boolean[] getSelectedPositions() {
        boolean[] selections = new boolean[studentList.size()];
        int index = 0;
        for (StudentModel s : studentList) {
            selections[index++] = s.isSelectedAndSaved();
        }
        return selections;
    }

    private void setSavedSelections() {
        for (StudentModel s : studentList) {
            s.setSelectedAndSaved(s.isSelected());
        }
    }

    private String getSelectedStudentsToString() {
        StringBuilder message = new StringBuilder();
        for (StudentModel s : studentList) {
            if (s.isSelectedAndSaved()) {
                message.append(s.getStudentName()).append('\n');
            }
        }
        return message.toString();
    }

    private List<String> getSelectedStudentNames() {
        List<String> students = new ArrayList<>();
        for (StudentModel s : studentList) {
            if (s.isSelectedAndSaved()) {
                students.add(s.getStudentName());
            }
        }
        return students;
    }

    private List<String> getSelectedStudentIds() {
        List<String> studentIds = new ArrayList<>();
        for (StudentModel s : studentList) {
            if (s.isSelectedAndSaved()) {
                studentIds.add(s.getStudentId());
            }
        }
        return studentIds;
    }

    private int selectedItemsCount() {
        int counter = 0;
        for (StudentModel s : studentList) {
            if (s.isSelectedAndSaved()) {
                counter++;
            }
        }
        return counter;
    }
}