package com.example.rgbpicker;

import com.example.rgbpicker.Firebase.Concentration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {

    private DatabaseReference databaseReference;

    public DatabaseHelper() {
        // Initialize the database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void storeConcentrationData(String smartphone, String camera, String userItem, Concentration concentration) {
        DatabaseReference ref = databaseReference.child(smartphone).child(camera).child(userItem);
        ref.push().setValue(concentration);
    }

    public void getConcentrations(DatabaseReference ref, final ConcentrationCallback callback) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Concentration> concentrations = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Concentration concentration = snapshot.getValue(Concentration.class);
                    concentrations.add(concentration);
                }
                callback.onCallback(concentrations);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    // Define the ConcentrationCallback interface
    public interface ConcentrationCallback {
        void onCallback(List<Concentration> concentrations);
    }

    // Define the DataCallback interface
    public interface DataCallback {
        void onCallback(int[] result);
    }
}
