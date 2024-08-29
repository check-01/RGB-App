package com.example.rgbpicker.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rgbpicker.Adapter.CameraAdapter;
import com.example.rgbpicker.Adapter.SmartphoneAdapter;
import com.example.rgbpicker.Adapter.UserSelectionAdapter;
import com.example.rgbpicker.Model.Smartphone;
import com.example.rgbpicker.Model.UserSelection;
import com.example.rgbpicker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


    public class SmartphoneFragment extends Fragment {

        private RecyclerView recyclerViewSmartphones;
        private RecyclerView recyclerViewCameras;
        private RecyclerView recyclerViewUserSelection;
        private SmartphoneAdapter smartphoneAdapter;
        private CameraAdapter cameraAdapter;
        private UserSelectionAdapter userSelectionAdapter;
        private List<Smartphone> smartphoneList;
        private List<String> cameraList;
        private List<UserSelection> userSelectionList;
        private String selectedSmartphoneName;
        private String selectedCameraValue;
        private String selectedUserElement;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_smartphone, container, false);

            recyclerViewSmartphones = view.findViewById(R.id.smartphone_recycler_view);
            recyclerViewCameras = view.findViewById(R.id.recycler_view_cameras);
            recyclerViewUserSelection = view.findViewById(R.id.recycler_view_user_selection);
            Button btnNext = view.findViewById(R.id.btn_next);

            recyclerViewSmartphones.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewCameras.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewUserSelection.setLayoutManager(new LinearLayoutManager(getContext()));

            smartphoneList = new ArrayList<>();
            smartphoneList.add(new Smartphone("Lava Blaze", Arrays.asList("13MP", "8MP")));
            smartphoneList.add(new Smartphone("Samsung Galaxy S21", Arrays.asList("108MP", "12MP", "10MP")));
            smartphoneList.add(new Smartphone("Google Pixel 6", Arrays.asList("50MP", "12MP")));

            smartphoneAdapter = new SmartphoneAdapter(smartphoneList, this::onSmartphoneSelected);
            recyclerViewSmartphones.setAdapter(smartphoneAdapter);

            cameraList = new ArrayList<>();
            cameraAdapter = new CameraAdapter(cameraList, this::onCameraSelected);
            recyclerViewCameras.setAdapter(cameraAdapter);

            userSelectionList = new ArrayList<>();
            userSelectionList.add(new UserSelection("Fluoride"));
            userSelectionList.add(new UserSelection("Arsenic"));
            userSelectionList.add(new UserSelection("Iron"));
            userSelectionAdapter = new UserSelectionAdapter(userSelectionList, this::onUserSelectionSelected);
            recyclerViewUserSelection.setAdapter(userSelectionAdapter);

            btnNext.setOnClickListener(v -> openDisplayFragment());

            return view;
        }

        private void onSmartphoneSelected(Smartphone smartphone) {
            selectedSmartphoneName = smartphone.getName();
            cameraList.clear();
            cameraList.addAll(smartphone.getCameraValues());
            cameraAdapter.notifyDataSetChanged();
            recyclerViewCameras.setVisibility(View.VISIBLE); // Show the camera RecyclerView
        }

        private void onCameraSelected(String cameraValue) {
            selectedCameraValue = cameraValue;
        }

        private void onUserSelectionSelected(UserSelection userSelection) {
            selectedUserElement = userSelection.getSelection();
        }

        private void openDisplayFragment() {
            Bundle bundle = new Bundle();
            bundle.putString("smartphone_name", selectedSmartphoneName);
            bundle.putString("camera_value", selectedCameraValue);
            bundle.putString("user_selection", selectedUserElement);

            ImageHandlerFragment imageHandlerFragment = new ImageHandlerFragment();
            imageHandlerFragment.setArguments(bundle);

            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, imageHandlerFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }