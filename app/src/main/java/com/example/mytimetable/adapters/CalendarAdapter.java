package com.example.mytimetable.adapters;

import static com.example.mytimetable.teacher_main.TeacherMainActivity.REQUEST_EDIT_MODULE_MEETING;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytimetable.R;
import com.example.mytimetable.models.BaseItemListModel;
import com.example.mytimetable.models.ModuleInfoModel;
import com.example.mytimetable.models.TimeIntervalModel;
import com.example.mytimetable.module.ModuleMeetingEditActivity;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_HOUR_INTERVAL = 0;
    private static final int ITEM_TYPE_MODULE_INFO = 1;
    public static final String EXTRA_MODULE_NAME = "extra_module_name";
    public static final String EXTRA_DATE = "extra_date";
    public static final String EXTRA_HOUR = "extra_hour";
    public static final String EXTRA_ROOM = "extra_room_name";
    public static final String EXTRA_DOCUMENT_ID = "extra_document_id";
    public static final String EXTRA_SEMESTER = "extra_semester";
    public static final String EXTRA_GROUP_NAME = "extra_group_name";

    private final Context context;
    private final List<BaseItemListModel> itemList;
    private final boolean isTeacher;

    public CalendarAdapter(Context context, List<BaseItemListModel> itemList, boolean isTeacher) {
        this.context = context;
        this.itemList = itemList;
        this.isTeacher = isTeacher;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(
                (viewType == ITEM_TYPE_HOUR_INTERVAL ? R.layout.item_time_interval : R.layout.item_module_info),
                parent, false);
        return (viewType == ITEM_TYPE_HOUR_INTERVAL ? new TimeIntervalViewHolder(view) : new ModuleInfoViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (ITEM_TYPE_HOUR_INTERVAL == getItemViewType(position)) {

            TimeIntervalModel item = (TimeIntervalModel) itemList.get(position);
            TimeIntervalViewHolder viewHolder = (TimeIntervalViewHolder) holder;

            viewHolder.timeInterval.setText(item.getTimeInterval());

        } else if (ITEM_TYPE_MODULE_INFO == getItemViewType(position)) {

            ModuleInfoModel item = (ModuleInfoModel) itemList.get(position);
            ModuleInfoViewHolder viewHolder = (ModuleInfoViewHolder) holder;

            viewHolder.moduleName.setText(item.getModuleName());
            viewHolder.room.setText(item.getRoomName());
            viewHolder.participants.setText(item.getParticipant());

            if (isTeacher) {
                viewHolder.btnEdit.setVisibility(View.VISIBLE);
                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(context, ModuleMeetingEditActivity.class);
                        intent.putExtra(EXTRA_DOCUMENT_ID, item.getDocumentId());
                        intent.putExtra(EXTRA_MODULE_NAME, item.getModuleName());
                        intent.putExtra(EXTRA_DATE, item.getDate());
                        intent.putExtra(EXTRA_HOUR, item.getTimeInterval());
                        intent.putExtra(EXTRA_ROOM, item.getRoomName());
                        intent.putExtra(EXTRA_GROUP_NAME, item.getGroupName());
                        intent.putExtra(EXTRA_SEMESTER, item.getSemester());

                        ((Activity) context).startActivityForResult(
                                intent,
                                REQUEST_EDIT_MODULE_MEETING);

                    }
                });
            } else {
                viewHolder.btnEdit.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (itemList.get(position) instanceof TimeIntervalModel) {
            return ITEM_TYPE_HOUR_INTERVAL;
        } else if (itemList.get(position) instanceof ModuleInfoModel) {
            return ITEM_TYPE_MODULE_INFO;
        }

        return super.getItemViewType(position);
    }

    static class TimeIntervalViewHolder extends RecyclerView.ViewHolder {

        TextView timeInterval;

        public TimeIntervalViewHolder(@NonNull View itemView) {
            super(itemView);
            timeInterval = itemView.findViewById(R.id.item_time_interval);
        }
    }

    static class ModuleInfoViewHolder extends RecyclerView.ViewHolder {

        TextView moduleName, room, participants;
        ImageView btnEdit;

        public ModuleInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            moduleName = itemView.findViewById(R.id.item_module_name);
            room = itemView.findViewById(R.id.item_room);
            participants = itemView.findViewById(R.id.item_participants);
            btnEdit = itemView.findViewById(R.id.item_edit);
        }
    }
}