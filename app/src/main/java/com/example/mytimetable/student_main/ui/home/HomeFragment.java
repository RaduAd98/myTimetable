package com.example.mytimetable.student_main.ui.home;

import static com.example.mytimetable.utils.ListUtils.getModulesFromFirestore;
import static com.example.mytimetable.utils.NavigationUtils.getAccountID;

import android.app.DatePickerDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private TextView textViewDatePicker;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE,\nd MMMM", Locale.getDefault());
    private final SimpleDateFormat fireStoreDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private String formattedDate;
    private ImageButton buttonPreviousDate,
            buttonNextDate;
    private RecyclerView recyclerView;
    private CalendarAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        initializeRecyclerView();
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
        getStudentGroupNameForRecycler();
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
                HomeFragment.this,
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
        formattedDate = simpleDateFormat.format(calendar.getTime());
        textViewDatePicker.setText(formattedDate);

        initializeRecyclerView();
    }

    //Setting the TextView with the current date
    private void setTextViewCurrentDate(TextView textViewDatePicker) {
        formattedDate = simpleDateFormat.format(calendar.getTime());
        textViewDatePicker.setText(formattedDate);
    }

    //On pressing the back arrow button, it shows the previous date
    private void setTextViewPreviousDate(TextView textViewDatePicker) {
        buttonPreviousDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DATE, -1);
                formattedDate = simpleDateFormat.format(calendar.getTime());
                textViewDatePicker.setText(formattedDate);

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
                formattedDate = simpleDateFormat.format(calendar.getTime());
                textViewDatePicker.setText(formattedDate);

                initializeRecyclerView();
            }
        });
    }

    //Method that gets the Group Name of the connected Student and shows the enrolled modules
    private void getStudentGroupNameForRecycler() {
        FirebaseFirestore.getInstance()
                .collection("Groups")
                .whereArrayContains("studentsID", getAccountID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                String groupName = (String) queryDocumentSnapshot.getData().get("groupName");
                                getModulesFromFirestore(getContext(), recyclerView, adapter,
                                        fireStoreDateFormat.format(calendar.getTime()), groupName, null, false);
                            }
                        }
                    }
                });
    }
}