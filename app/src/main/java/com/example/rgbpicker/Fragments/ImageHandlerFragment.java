package com.example.rgbpicker.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rgbpicker.DatabaseHelper;
import com.example.rgbpicker.Firebase.Concentration;
import com.example.rgbpicker.Firebase.ConcentrationDataHelper;
import com.example.rgbpicker.ImgCropActivity;
import com.example.rgbpicker.MainActivity;
import com.example.rgbpicker.PermissionUtils;
import com.example.rgbpicker.R;
import com.example.rgbpicker.databinding.FragmentImageHandlerBinding;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;

public class ImageHandlerFragment extends Fragment {

    private FragmentImageHandlerBinding binding;
    private ActivityResultLauncher<String> cropImage;
    private ActivityResultLauncher<Uri> captureImage;
    private Uri cameraImageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            Intent intent = new Intent(getContext(), ImgCropActivity.class);
            intent.putExtra("SendImageData", result.toString());
            if (getArguments() != null) {
                intent.putExtra("Smartphone", getArguments().getString("smartphone_name"));
                intent.putExtra("Camera", getArguments().getString("camera_value"));
                intent.putExtra("UserItem", getArguments().getString("user_selection"));
            }
            startActivityForResult(intent, 100);
        });

        captureImage = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result) {
                Intent intent = new Intent(getContext(), ImgCropActivity.class);
                intent.putExtra("SendImageData", cameraImageUri.toString());
                if (getArguments() != null) {
                    intent.putExtra("Smartphone", getArguments().getString("smartphone_name"));
                    intent.putExtra("Camera", getArguments().getString("camera_value"));
                    intent.putExtra("UserItem", getArguments().getString("user_selection"));
                }
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentImageHandlerBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        // Example usage of ConcentrationDataHelper to store data
//        ConcentrationDataHelper concentrationDataHelper = new ConcentrationDataHelper();
//        concentrationDataHelper.storeConcentrationData();


        TextView display_text_view = view.findViewById(R.id.display_text_view);

        if (getArguments() != null) {
            String smartphoneName = getArguments().getString("smartphone_name");
            String cameraValue = getArguments().getString("camera_value");
            String userSelection = getArguments().getString("user_selection");

            String displayText = "Smartphone: " + smartphoneName + "\nCamera: " + cameraValue + "\nElement: " + userSelection;
            display_text_view.setText(displayText);
        }

        binding.selectImageBtn.setOnClickListener(v ->
                PermissionUtils.requestImagePermission(getContext(), new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {
                        cropImage.launch("image/*");
                    }

                    @Override
                    public void onPermissionDenied() {
                        // Handle permission denied if needed
                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                })
        );

        binding.captureImageBtn.setOnClickListener(v ->
                PermissionUtils.requestCameraAndStoragePermission(getContext(), new PermissionUtils.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {
                        try {
                            File photoFile = createImageFile();
                            if (photoFile != null) {
                                cameraImageUri = FileProvider.getUriForFile(
                                        getContext(),
                                        "com.example.rgbpicker.provider", // Replace with your actual application ID
                                        photoFile);
                                captureImage.launch(cameraImageUri);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        // Handle permission denied if needed
                        Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                })
        );

        Button btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private File createImageFile() throws IOException {
        String imageFileName = "JPEG_" + System.currentTimeMillis() + "_";
        File storageDir = getActivity().getExternalFilesDir(null);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 101 && data != null) {
            Executors.newSingleThreadExecutor().execute(() -> {
                try {
                    String croppedImageUri = data.getStringExtra("CROP");
                    int avgRed = data.getIntExtra("AVG_RED", 0);
                    int avgGreen = data.getIntExtra("AVG_GREEN", 0);
                    int avgBlue = data.getIntExtra("AVG_BLUE", 0);
                    int closestConcentration = data.getIntExtra("CLOSEST_CONCENTRATION", -1);
                    int closestRed = data.getIntExtra("CLOSEST_RED", 0);
                    int closestGreen = data.getIntExtra("CLOSEST_GREEN", 0);
                    int closestBlue = data.getIntExtra("CLOSEST_BLUE", 0);

                    Uri finalUri = croppedImageUri != null ? Uri.parse(croppedImageUri) : null;

                    requireActivity().runOnUiThread(() -> {
                        if (finalUri != null) {
                            binding.imageview.setImageURI(finalUri);

                            if (closestRed == 0 && closestGreen == 0 && closestBlue == 0 && closestConcentration == 0) {
                                binding.rgbTextView.setText("");
                                binding.closestRgbTextView.setText("");
                                binding.concentrationTextView.setText("");

                                Toast.makeText(getContext(), "Values are not matched. Please try again.", Toast.LENGTH_SHORT).show();
                            } else {
                                binding.rgbTextView.setText("Average RGB: " + avgRed + ", " + avgGreen + ", " + avgBlue);
                                binding.closestRgbTextView.setText("Closest RGB: " + closestRed + ", " + closestGreen + ", " + closestBlue);
                                binding.concentrationTextView.setText("Concentration: " + closestConcentration);
                            }
                        } else {
                            Toast.makeText(getContext(), "Image Uri is null", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show());
                }
            });
        }
    }

}
