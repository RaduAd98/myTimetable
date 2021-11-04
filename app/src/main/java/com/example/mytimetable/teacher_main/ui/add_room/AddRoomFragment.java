package com.example.mytimetable.teacher_main.ui.add_room;

import static com.example.mytimetable.utils.NavigationUtils.closeKeyboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mytimetable.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddRoomFragment extends Fragment {

    private FirebaseFirestore firebaseFirestore;
    private TextInputLayout roomName,
            locationName,
            capacityNumber;
    private Button btnAddRoom;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_room_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        attachBtnRoomListeners();
    }

    private void initializeViews(View view) {
        roomName = view.findViewById(R.id.layout_name_room_page);
        locationName = view.findViewById(R.id.layout_location_room_page);
        capacityNumber = view.findViewById(R.id.layout_capacity_room_page);
        btnAddRoom = view.findViewById(R.id.button_add_room_page);
    }

    private void attachBtnRoomListeners() {
        btnAddRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(getActivity(), v);
                String nameRoom = roomName.getEditText().getText().toString();
                String nameLocation = locationName.getEditText().getText().toString();
                int numberCapacity;
                try {
                    numberCapacity = Integer.parseInt(capacityNumber.getEditText().getText().toString());
                } catch (Exception e) {
                    numberCapacity = 0;
                }
                if (nameRoom.isEmpty()) {
                    roomName.setError("Please insert the Room name!");
                } else {
                    roomName.setError(null);
                }
                if (nameLocation.isEmpty()) {
                    locationName.setError("Please insert the Room location!");
                } else {
                    locationName.setError(null);
                }
                if (numberCapacity <= 0) {
                    capacityNumber.setError("Please insert the Capacity number!");
                } else {
                    capacityNumber.setError(null);
                }
                if (!nameRoom.isEmpty() && !nameLocation.isEmpty() && numberCapacity > 0) {
                    CollectionReference collectionReferenceRooms =
                            firebaseFirestore.collection("Rooms");
                    Map<String, Object> rooms = new HashMap<>();
                    rooms.put("roomName", nameRoom);
                    rooms.put("locationName", nameLocation);
                    rooms.put("capacityNumber", numberCapacity);
                    collectionReferenceRooms.add(rooms);
                    Toast.makeText(getActivity(), "Room added successfully!", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }
            }
        });
    }
}