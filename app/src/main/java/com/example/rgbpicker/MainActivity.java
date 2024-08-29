package com.example.rgbpicker;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.firebase.FirebaseApp;


import com.example.rgbpicker.Fragments.ImageHandlerFragment;
import com.example.rgbpicker.Fragments.SmartphoneFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize Firebase
        FirebaseApp.initializeApp(this);



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SmartphoneFragment())
                    .commit();
        }
    }
}
