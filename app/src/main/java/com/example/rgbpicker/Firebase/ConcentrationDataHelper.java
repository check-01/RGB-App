package com.example.rgbpicker.Firebase;

import com.example.rgbpicker.DatabaseHelper;

public class ConcentrationDataHelper {
    private DatabaseHelper dbHelper;

    public ConcentrationDataHelper() {
        dbHelper = new DatabaseHelper();
    }

    public void storeConcentrationData() {
        Concentration concentration = new Concentration(193, 219, 177, 0.0);
        dbHelper.storeConcentrationData("Lava Blaze", "8MP", "Fluoride", concentration);

        Concentration concentration1 = new Concentration(199, 222, 177, 1.0);
        dbHelper.storeConcentrationData("Lava Blaze", "8MP", "Fluoride", concentration1);

        Concentration concentration2 = new Concentration(209, 234, 187, 2.0);
        dbHelper.storeConcentrationData("Lava Blaze", "8MP", "Fluoride", concentration2);

        Concentration concentration3 = new Concentration(199, 225, 171, 3.0);
        dbHelper.storeConcentrationData("Lava Blaze", "8MP", "Fluoride", concentration3);

        Concentration concentration4 = new Concentration(191, 219, 170, 4.0);
        dbHelper.storeConcentrationData("Lava Blaze", "8MP", "Fluoride", concentration4);

        Concentration concentration5 = new Concentration(191, 221, 158, 5.0);
        dbHelper.storeConcentrationData("Lava Blaze", "8MP", "Fluoride", concentration5);

        Concentration concentration6 = new Concentration(182, 215, 150, 6.0);
        dbHelper.storeConcentrationData("Lava Blaze", "8MP", "Fluoride", concentration6);

        Concentration concentration7 = new Concentration(177, 215, 147, 8.0);
        dbHelper.storeConcentrationData("Lava Blaze", "8MP", "Fluoride", concentration7);
    }
}

