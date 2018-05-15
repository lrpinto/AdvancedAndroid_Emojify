/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    static void detectFaces(Context context, Bitmap picture) {

        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        // Log the number of faces
        Log.d(LOG_TAG, "detectFaces: number of faces = " + faces.size());

        // If there are no faces detected, show a Toast message
        if(faces.size() == 0){
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);

                // Log the classification probabilities for each face.
                whichEmoji(face);
                // TODO (6): Change the call to getClassifications to whichEmoji() to log the appropriate emoji for the facial expression.
            }

        }


        // Release the detector
        detector.release();
    }


    /**
     * Method for logging the whichEmoji probabilities.
     *
     * @param face The face to get the whichEmoji probabilities.
     */
    private static void whichEmoji(Face face){
        // COMPLETED (2): Change the name of the getClassifications() method to whichEmoji() (also change the log statements)

        // Log all the probabilities
        Log.d(LOG_TAG, "whichEmoji: smilingProb = " + face.getIsSmilingProbability());
        Log.d(LOG_TAG, "whichEmoji: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "whichEmoji: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());

        // COMPLETED (3): Create threshold constants for a person smiling, and and eye being open by taking pictures of yourself and your friends and noting the logs.
        final float SMILING_THRESHOLD = 0.45f;
        final float LEFT_EYE_OPEN_THRESHOLD = 0.45f;
        final float c = 4.5f;

        // COMPLETED (4): Create 3 boolean variables to track the state of the facial expression based on the thresholds you set in the previous step: smiling, left eye closed, right eye closed.
        boolean isSmiling = SMILING_THRESHOLD < face.getIsSmilingProbability();
        boolean isLeftEyeOpen = LEFT_EYE_OPEN_THRESHOLD < face.getIsLeftEyeOpenProbability();
        boolean isRightEyeOpen = LEFT_EYE_OPEN_THRESHOLD < face.getIsRightEyeOpenProbability();

        // COMPLETED (5): Create an if/else system that selects the appropriate emoji based on the above booleans and log the result.
        Emoji emoji = null;

        if (isSmiling && isLeftEyeOpen && isRightEyeOpen) {
            emoji = Emoji.SMILING;
        } else if ( !isSmiling && isLeftEyeOpen && isRightEyeOpen ) {
            emoji = Emoji.FROWNING;
        }  else if ( !isSmiling && !isLeftEyeOpen && !isRightEyeOpen ) {
            emoji = Emoji.CLOSED_EYE_FROWNING;
        }  else if ( isSmiling && !isLeftEyeOpen && !isRightEyeOpen ) {
            emoji = Emoji.CLOSED_EYE_SMILING;
        }  else if ( isSmiling && !isLeftEyeOpen && isRightEyeOpen ) {
            emoji = Emoji.LEFT_WINK;
        }  else if ( !isSmiling && !isLeftEyeOpen && isRightEyeOpen ) {
            emoji = Emoji.LEFT_WINK_FROWNING;
        }  else if ( isSmiling && isLeftEyeOpen && !isRightEyeOpen ) {
            emoji = Emoji.RIGHT_WINK;
        }  else if ( !isSmiling && isLeftEyeOpen && !isRightEyeOpen ) {
            emoji = Emoji.RIGHT_WINK_FROWNING;
        }

        Log.d(LOG_TAG, "Selected emoji: " + emoji.toString());
    }



}
