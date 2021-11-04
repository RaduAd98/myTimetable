package com.example.mytimetable.teacher_main.ui.add_module;

import static com.example.mytimetable.utils.NavigationUtils.attachSpinnerCloseKeyboardListeners;
import static com.example.mytimetable.utils.NavigationUtils.closeKeyboard;
import static com.example.mytimetable.utils.NavigationUtils.getAccountID;
import static com.example.mytimetable.utils.NavigationUtils.initializeSpinner;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mytimetable.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddModuleFragment extends Fragment {

    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private final List<String> listCourseName = new ArrayList<>();
    private TextInputLayout moduleName;
    private String teacherName;
    private Button btnAddModule;
    private Spinner spinnerCourseName,
            spinnerSemesterName,
            spinnerCreditsNumber,
            spinnerCompulsoryValue;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_module_teacher, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        getTeacherName();
        setSpinnerCourses(getActivity(), spinnerCourseName);
        initializeSpinner(getActivity(), spinnerSemesterName, R.array.semesters);
        initializeSpinner(getActivity(), spinnerCreditsNumber, R.array.credits);
        initializeSpinner(getActivity(), spinnerCompulsoryValue, R.array.compulsory);
        attachBtnAddModuleListeners();
        attachSpinnerCloseKeyboardListeners(spinnerCourseName, getActivity(), view);
        attachSpinnerCloseKeyboardListeners(spinnerSemesterName, getActivity(), view);
        attachSpinnerCloseKeyboardListeners(spinnerCreditsNumber, getActivity(), view);
        attachSpinnerCloseKeyboardListeners(spinnerCompulsoryValue, getActivity(), view);
    }

    //Method for initializing the XML components
    private void initializeViews(View view) {
        moduleName = view.findViewById(R.id.layout_module_name_add_page);
        spinnerCourseName = view.findViewById(R.id.spinner_course_name_add_page);
        spinnerSemesterName = view.findViewById(R.id.spinner_semester_name_add_page);
        spinnerCreditsNumber = view.findViewById(R.id.spinner_credits_number_add_page);
        spinnerCompulsoryValue = view.findViewById(R.id.spinner_compulsory_add_page);
        btnAddModule = view.findViewById(R.id.button_module_add_page);
    }

    //Listener for the Button that completes the action
    private void attachBtnAddModuleListeners() {
        btnAddModule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(getActivity(), v);
                String nameModule = moduleName.getEditText().getText().toString();
                if (!nameModule.isEmpty()) {
                    String courseName = spinnerCourseName.getSelectedItem().toString();
                    String semesterName = spinnerSemesterName.getSelectedItem().toString();
                    String creditsNumber = spinnerCreditsNumber.getSelectedItem().toString();
                    CollectionReference collectionReferenceModules =
                            firebaseFirestore.collection("Modules");
                    Map<String, Object> modules = new HashMap<>();
                    modules.put("moduleName", nameModule);
                    modules.put("courseName", courseName);
                    modules.put("semesterName", semesterName);
                    modules.put("creditsNumber", creditsNumber);
                    modules.put("teacherID", getAccountID());
                    modules.put("teacherName", teacherName);
                    modules.put("isUsed", false);
                    if (spinnerCompulsoryValue.getSelectedItemPosition() == 0) {
                        modules.put("isCompulsory", true);
                    } else {
                        modules.put("isCompulsory", false);
                    }
                    collectionReferenceModules.add(modules);
                    Toast.makeText(getActivity(), "Module added successfully!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                } else {
                    moduleName.setError("Please insert the Module name!");
                }
            }
        });
    }

    //Getting the list of Courses available from the database
    public void setSpinnerCourses(Context context, Spinner spinner) {
        firebaseFirestore.collection("BSc-MSc-Courses")
                .orderBy("courseName")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                listCourseName.add(documentSnapshot.getString("courseName"));
                                initializeSpinner(context, spinner, listCourseName);
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
}
