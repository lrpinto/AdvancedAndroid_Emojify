package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


// COMPLETED (1): Create a Java class called Emojifier
public class Emojifier {

    // Log TAG
    private static final String LOG_TAG = "Emojifier";

    // COMPLETED (2): Create a static method in the Emojifier class called detectFaces() which detects and logs the number of faces in a given bitmap.
    public static void detectFaces(Context context, Bitmap bitmap) {

        // Detect the number of faces in the given bitmap
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<Face> faces = detector.detect(frame);

        // Log the number of faces detected
        Log.d(LOG_TAG, "Detected " + faces.size() + " faces.");

        // If no faces were detected, display a toast message
        if ( faces.size() == 0 ) {
            Toast.makeText(context, "The given image has no faces. Please try another image.", Toast.LENGTH_SHORT).show();
        }

        // Release the face detector
        detector.release();
    }
}
