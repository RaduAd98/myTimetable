package com.example.mytimetable.utils;

import static com.example.mytimetable.utils.CalendarUtils.getAvailableHours;
import static com.example.mytimetable.utils.NavigationUtils.getAccountID;
import static com.example.mytimetable.utils.NavigationUtils.initializeSpinner;
import static java.util.Calendar.YEAR;

import android.content.Context;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SpinnerUtils {
    /**
     * This method uses the Firestore instance to retrieve the unavailable slots, as follows:
     * Firstly, it retrieves the list of unavailable hours of the Teacher
     * Secondly, it retrieves the list of unavailable hours of the Students Group and adds them to the first list
     * And thirdly, it retrieves the list of unavailable hours of the selected room and adds them to the first list.
     * <p>
     * The next step is to remove these items from a local instance
     * of the list containing all the possible hours, which are stored as an array resource.
     */
    public static void setSpinnerHours(Context context, Spinner spinner,
                                       String semesterName,
                                       String groupName, String roomName, boolean isEditMode,
                                       String initialDate, String updatedDate, String initialHour) {
        List<String> occupiedHours = new ArrayList<>();
        //First query - requesting the busy hours of the Teacher
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Modules-Participants")
                .whereEqualTo("teacherID", getAccountID())
                .whereEqualTo("semesterName", semesterName)
                .whereEqualTo("moduleDate", updatedDate)
                .whereEqualTo("year", String.valueOf(Calendar.getInstance().get(YEAR)))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (DocumentSnapshot snapshot : task.getResult()) {
                                occupiedHours.add(snapshot.getString("occupiedHour"));
                            }

                            //Second query - requesting the busy hours of the Group
                            firebaseFirestore.collection("Modules-Participants")
                                    .whereEqualTo("groupName", groupName)
                                    .whereEqualTo("moduleDate", updatedDate)
                                    .whereEqualTo("semesterName", semesterName)
                                    .whereEqualTo("year", String.valueOf(Calendar.getInstance().get(YEAR)))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot snapshot : task.getResult()) {
                                                    String hour = snapshot.getString("occupiedHour");
                                                    if (!occupiedHours.contains(hour)) {
                                                        occupiedHours.add(hour);
                                                    }
                                                }

                                                if (roomName != null && !roomName.equalsIgnoreCase("online")) {
                                                    //Third query - requesting the busy hours of the Room
                                                    firebaseFirestore.collection("Modules-Participants")
                                                            .whereEqualTo("roomName", roomName)
                                                            .whereEqualTo("moduleDate", updatedDate)
                                                            .whereEqualTo("semesterName", semesterName)
                                                            .whereEqualTo("year", String.valueOf(Calendar.getInstance().get(YEAR)))
                                                            .get()
                                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                    if (task.isSuccessful()) {
                                                                        for (DocumentSnapshot snapshot : task.getResult()) {
                                                                            String hour = snapshot.getString("occupiedHour");
                                                                            if (!occupiedHours.contains(hour)) {
                                                                                occupiedHours.add(hour);
                                                                            }
                                                                        }
                                                                        if (isEditMode && initialDate.equals(updatedDate)) {
                                                                            occupiedHours.remove(initialHour);
                                                                            initializeSpinner(
                                                                                    context, spinner,
                                                                                    getAvailableHours(context, occupiedHours),
                                                                                    true, initialHour);
                                                                        }
                                                                        initializeSpinner(
                                                                                context, spinner,
                                                                                getAvailableHours(context, occupiedHours));
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    initializeSpinner(context, spinner, getAvailableHours(context, occupiedHours));
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }
}
