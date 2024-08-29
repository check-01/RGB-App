package com.example.rgbpicker;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.rgbpicker.Firebase.Concentration;
import com.google.firebase.database.DatabaseReference;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class ImgCropActivity extends AppCompatActivity {

    String sourceUri, destinationUri, smartphone, camera, userItem;  // Variables to store the source and destination URIs and additional values
    Uri uri;   // Variable to store the parsed URI from the intent
    private static final String TAG = "UcActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_crop);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            sourceUri = intent.getStringExtra("SendImageData");
            smartphone = intent.getStringExtra("Smartphone");
            camera = intent.getStringExtra("Camera");
            userItem = intent.getStringExtra("UserItem");
            uri = Uri.parse(sourceUri);
        }

        destinationUri = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        // Load the original image to get its dimensions
        Bitmap originalBitmap = loadBitmapFromUri(uri);
        if (originalBitmap != null) {
            int originalWidth = originalBitmap.getWidth();
            int originalHeight = originalBitmap.getHeight();

            // Calculate the aspect ratio
            float aspectRatio = (float) originalWidth / (float) originalHeight;

            UCrop.Options options = new UCrop.Options();
            options.setCircleDimmedLayer(true);
            options.setFreeStyleCropEnabled(true); // Enable free style crop
            options.withAspectRatio(originalWidth, originalHeight); // Set the aspect ratio

            // Customize UCrop behavior to preserve image size
            options.setShowCropFrame(true); // Hide the default crop frame
            options.setShowCropGrid(false); // Hide the default crop grid

            // Start the UCrop activity to crop the image
            UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationUri)))
                    .withOptions(options)
                    .withMaxResultSize(originalWidth, originalHeight) // Set the maximum result size for the cropped image
                    .start(ImgCropActivity.this);
        } else {
            Toast.makeText(this, "Failed to load original image.", Toast.LENGTH_SHORT).show();
            // Handle error loading original image
        }
    }

    // Method to handle the result from the UCrop activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);

            if (resultUri != null) {
                Log.d(TAG, "Crop successful. Result URI: " + resultUri.toString());
                Bitmap croppedBitmap = loadBitmapFromUri(resultUri);
                if (croppedBitmap != null) {
                    Log.d(TAG, "Cropped bitmap loaded successfully.");
                    Bitmap circularBitmap = getCircularBitmap(croppedBitmap);
                    int[] averageRGB = calculateAverageRGB(circularBitmap);

                    Intent intent = new Intent();
                    intent.putExtra("CROP", resultUri.toString());
                    intent.putExtra("AVG_RED", averageRGB[0]);
                    intent.putExtra("AVG_GREEN", averageRGB[1]);
                    intent.putExtra("AVG_BLUE", averageRGB[2]);

                    // Call findClosestConcentration and handle the callback
                    findClosestConcentration(smartphone, camera, userItem, averageRGB, new DatabaseHelper.DataCallback() {
                        @Override
                        public void onCallback(int[] closestData) {
                            if (closestData != null) {
                                intent.putExtra("CLOSEST_CONCENTRATION", closestData[0]);
                                intent.putExtra("CLOSEST_RED", closestData[1]);
                                intent.putExtra("CLOSEST_GREEN", closestData[2]);
                                intent.putExtra("CLOSEST_BLUE", closestData[3]);
                            } else {
                                // Error processing image, show toast and navigate back to main activity
                                runOnUiThread(() -> {
                                    Toast.makeText(ImgCropActivity.this, "Error processing image. Please try another image.", Toast.LENGTH_SHORT).show();
                                    Intent mainActivityIntent = new Intent(ImgCropActivity.this, MainActivity.class);
                                    mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(mainActivityIntent);
                                });
                                return;
                            }

                            setResult(101, intent);
                            finish();
                        }
                    });

                } else {
                    Log.e(TAG, "Cropped bitmap is null.");
                }
            } else {
                Log.e(TAG, "Result URI is null.");
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(this, "Crop error:" + cropError, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Crop error: " + cropError);
        }
    }


    // Helper method to load a bitmap from a URI
    private Bitmap loadBitmapFromUri(Uri uri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to apply a circular mask to the bitmap
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, size, size);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // Method to calculate the average RGB values from the circular bitmap
    private int[] calculateAverageRGB(Bitmap bitmap) {
        int totalPixels = 0;
        int totalRed = 0, totalGreen = 0, totalBlue = 0;

        int radius = bitmap.getWidth() / 2;
        int centerX = radius;
        int centerY = radius;

        for (int y = 0; y < bitmap.getHeight(); y++) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                if (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2)) {
                    int pixel = bitmap.getPixel(x, y);
                    totalRed += Color.red(pixel);
                    totalGreen += Color.green(pixel);
                    totalBlue += Color.blue(pixel);
                    totalPixels++;
                }
            }
        }

        int avgRed = totalRed / totalPixels;
        int avgGreen = totalGreen / totalPixels;
        int avgBlue = totalBlue / totalPixels;

        Log.d(TAG, "Average RGB: " + avgRed + ", " + avgGreen + ", " + avgBlue);

        return new int[] { avgRed, avgGreen, avgBlue };
    }

    private double calculateWeightedDistance(int[] rgb1, int[] rgb2, double[] weights) {
        return Math.sqrt(weights[0] * Math.pow(rgb1[0] - rgb2[0], 2) +
                weights[1] * Math.pow(rgb1[1] - rgb2[1], 2) +
                weights[2] * Math.pow(rgb1[2] - rgb2[2], 2));
    }

    public void findClosestConcentration(String smartphone, String camera, String userItem, int[] averageRGB, DatabaseHelper.DataCallback callback) {
        DatabaseHelper dbHelper = new DatabaseHelper();
        DatabaseReference ref = dbHelper.getDatabaseReference().child(smartphone).child(camera).child(userItem);

        dbHelper.getConcentrations(ref, new DatabaseHelper.ConcentrationCallback() {
            @Override
            public void onCallback(List<Concentration> concentrations) {
                int closestConcentration = -1;
                double minDistance = Double.MAX_VALUE;
                int[] closestRGB = new int[3]; // Store closest RGB values
                double threshold = 50.0; // Define a threshold to filter out non-close matches
                double[] weights = {1.0, 1.0, 1.0}; // Weights for R, G, B channels

                for (Concentration concentration : concentrations) {
                    int[] dbRGB = {concentration.getRed(), concentration.getGreen(), concentration.getBlue()};
                    double distance = calculateWeightedDistance(averageRGB, dbRGB, weights);

                    if (distance < minDistance && distance <= threshold) {
                        minDistance = distance;
                        closestConcentration = (int) concentration.getConcentration();
                        closestRGB[0] = concentration.getRed();
                        closestRGB[1] = concentration.getGreen();
                        closestRGB[2] = concentration.getBlue();
                    }
                }

                // Use closestConcentration and closestRGB as needed
                if (minDistance <= threshold) {
                    callback.onCallback(new int[]{closestConcentration, closestRGB[0], closestRGB[1], closestRGB[2]});
                } else {
                    callback.onCallback(null);
                }
            }
        });
    }


}
