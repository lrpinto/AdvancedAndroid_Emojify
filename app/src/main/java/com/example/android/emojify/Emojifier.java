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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();


    private static final float EMOJI_SCALE_FACTOR = .9f;

    // COMPLETED (3): Create threshold constants for a person smiling, and and eye being open by taking pictures of yourself and your friends and noting the logs.
    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    /**
     * Method for detecting faces in a bitmap.
     *  @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    public static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap picture) {

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
        Log.d(LOG_TAG, "detectFacesAndOverlayEmoji: number of faces = " + faces.size());

        // Initialise result bitmap to original picture
        Bitmap resultBitmap = picture;

        // If there are no faces detected, show a Toast message
        if(faces.size() == 0){
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);

                // COMPLETED (6): Change the call to getClassifications to whichEmoji() to log the appropriate emoji for the facial expression.
                Emoji emoji = whichEmoji(face);

                // Initialize an empty drawable
                Bitmap emojiBitmap;

                switch (emoji){
                    case SMILING:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.smile );
                        break;
                    case FROWNING:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.frown );
                        break;
                    case LEFT_WINK:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.leftwink );
                        break;
                    case RIGHT_WINK:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.rightwink );
                        break;
                    case CLOSED_EYE_SMILING:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.closed_smile );
                        break;
                    case LEFT_WINK_FROWNING:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.leftwinkfrown );
                        break;
                    case CLOSED_EYE_FROWNING:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.closed_frown );
                        break;
                    case RIGHT_WINK_FROWNING:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.rightwinkfrown );
                        break;
                    default:
                        emojiBitmap = null;
                        Toast.makeText(context, R.string.no_emoji, Toast.LENGTH_SHORT).show();
                }
                
                resultBitmap = addBitmapToFace(picture, emojiBitmap, face);
            }

        }


        // Release the detector
        detector.release();

        return resultBitmap;

    }

    /**
     * Combines the original picture with the emoji bitmaps
     *
     * @param backgroundBitmap The original picture
     * @param emojiBitmap      The chosen emoji
     * @param face             The detected face
     * @return The final bitmap, including the emojis over the faces
     */
    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }



    /**
     * Method for logging the whichEmoji probabilities.
     *
     * @param face The face to get the whichEmoji probabilities.
     */
    private static Emoji whichEmoji(Face face){
        // COMPLETED (2): Change the name of the getClassifications() method to whichEmoji() (also change the log statements)

        // Log all the probabilities
        Log.d(LOG_TAG, "whichEmoji: smilingProb = " + face.getIsSmilingProbability());
        Log.d(LOG_TAG, "whichEmoji: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "whichEmoji: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());

        // COMPLETED (4): Create 3 boolean variables to track the state of the facial expression based on the thresholds you set in the previous step: smiling, left eye closed, right eye closed.
        boolean isSmiling = SMILING_PROB_THRESHOLD < face.getIsSmilingProbability();
        boolean isLeftEyeOpen = EYE_OPEN_PROB_THRESHOLD < face.getIsLeftEyeOpenProbability();
        boolean isRightEyeOpen = EYE_OPEN_PROB_THRESHOLD < face.getIsRightEyeOpenProbability();

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

        return emoji;
    }



}
