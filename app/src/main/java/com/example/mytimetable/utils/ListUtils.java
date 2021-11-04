package com.example.mytimetable.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytimetable.adapters.CalendarAdapter;
import com.example.mytimetable.models.BaseItemListModel;
import com.example.mytimetable.models.ModuleInfoModel;
import com.example.mytimetable.models.TimeIntervalModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    //Method that retrieves data from Firestore according to the account type
    public static void getModulesFromFirestore(Context context,
                                               RecyclerView recyclerView,
                                               CalendarAdapter adapter,
                                               String date, String groupName,
                                               String teacherId,
                                               boolean isTeacher) {

        List<BaseItemListModel> list = new ArrayList<>();

        String fieldName = teacherId == null ? "groupName" : "teacherID";
        String fieldValue = teacherId == null ? groupName : teacherId;

        FirebaseFirestore.getInstance()
                .collection("Modules-Participants")
                .whereEqualTo("moduleDate", date)
                .whereEqualTo(fieldName, fieldValue)
                .orderBy("occupiedHour")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {

                                String documentId = snapshot.getId();
                                String room = snapshot.getString("roomName");
                                String timeInterval = snapshot.getString("occupiedHour");
                                String fieldValue = snapshot.getString(teacherId == null ? "teacherName" : "groupName");
                                String moduleName = snapshot.getString("moduleName");
                                String moduleDate = snapshot.getString("moduleDate");
                                String semester = snapshot.getString("semesterName");
                                String groupName = snapshot.getString("groupName");

                                ModuleInfoModel moduleInfo =
                                        new ModuleInfoModel(moduleName, room, fieldValue, timeInterval,
                                                moduleDate, semester, groupName, documentId);
                                list.add(moduleInfo);
                            }
                            getItemListForRecycler(context, recyclerView, adapter, list, isTeacher);
                        }
                    }
                });
    }

    //Method that populates each time interval of the Recycler View with the module info that correctly matches the time interval, date and account
    public static void getItemListForRecycler(Context context, RecyclerView recyclerView,
                                              CalendarAdapter adapter,
                                              List<BaseItemListModel> moduleInfoList,
                                              boolean isTeacher) {

        List<BaseItemListModel> itemListModels = new ArrayList<>();

        List<TimeIntervalModel> timeIntervals = getTimeIntervalList();

        for (TimeIntervalModel interval : timeIntervals) {

            itemListModels.add(interval);

            if (!moduleInfoList.isEmpty() && ((ModuleInfoModel) moduleInfoList.get(0)).getTimeInterval().equals(interval.getTimeInterval())) {
                itemListModels.add(moduleInfoList.get(0));
                moduleInfoList.remove(0);
            }
        }
        if (adapter == null) {
            adapter = new CalendarAdapter(context, itemListModels, isTeacher);
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();
    }

    //Method that generates the basic time intervals
    public static List<TimeIntervalModel> getTimeIntervalList() {

        List<TimeIntervalModel> list = new ArrayList<>();

        list.add(new TimeIntervalModel("09:00 – 11:00"));
        list.add(new TimeIntervalModel("11:00 – 13:00"));
        list.add(new TimeIntervalModel("13:00 – 15:00"));
        list.add(new TimeIntervalModel("15:00 – 17:00"));
        list.add(new TimeIntervalModel("17:00 – 19:00"));
        list.add(new TimeIntervalModel("19:00 – 21:00"));

        return list;
    }
}
