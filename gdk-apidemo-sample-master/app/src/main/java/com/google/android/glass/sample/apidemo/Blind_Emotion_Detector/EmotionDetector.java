package com.google.android.glass.sample.apidemo.Blind_Emotion_Detector;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.EnumMap;
import java.util.Locale;
import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;
import android.os.Handler;
import com.google.android.glass.sample.apidemo.R;
import com.google.android.glass.touchpad.GestureDetector;
import java.lang.Thread;


import com.google.android.glass.sample.apidemo.R;
import com.google.android.glass.touchpad.GestureDetector;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;



import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Samaneh on 3/5/16.
 */

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Displays information about the continuous gestures reported by the gesture detector (i.e.,
 * scrolling events).
 */



/**
 * This is a very bare sample app to demonstrate the usage of the CameraDetector object from Affectiva.
 * It displays statistics on frames per second, percentage of time a face was detected, and the user's smile score.
 *
 * The app shows off the maneuverability of the SDK by allowing the user to start and stop the SDK and also hide the camera SurfaceView.
 *
 * For use with SDK 2.02
 */
public class EmotionDetector extends Activity implements Detector.ImageListener, CameraDetector.CameraEventListener, TextToSpeech.OnInitListener {

    final String LOG_TAG = "Affectiva";
    int rateOfCameraCalls = 4;
    EnumMap<Metrics,TextView> metricsTextViews = new EnumMap<>(Metrics.class);

    Button startSDKButton;
    Button surfaceViewVisibilityButton;
    TextView smileTextView;
    ToggleButton toggleButton;

    SurfaceView cameraPreview;

    //boolean isCameraBack = false;
   // boolean isSDKStarted = false;
    boolean isCameraBack = true;
    boolean isSDKStarted = true;
    RelativeLayout mainLayout;
    CameraDetector detector;

    int previewWidth = 0;
    int previewHeight = 0;

    TextToSpeech tts;
    boolean initialized = false;
    String queuedText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts = new TextToSpeech(this /* context ​*/, this /*​ listener */);


        setContentView(R.layout.emotion_detector);

        smileTextView = (TextView) findViewById(R.id.smile_textview);
/*
        toggleButton = (ToggleButton) findViewById(R.id.front_back_toggle_button);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCameraBack = isChecked;
                switchCamera(isCameraBack? CameraDetector.CameraType.CAMERA_BACK : CameraDetector.CameraType.CAMERA_FRONT);
            }
        });

        startSDKButton = (Button) findViewById(R.id.sdk_start_button);
        startSDKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSDKStarted) {
                    isSDKStarted = false;
                    stopDetector();
                    startSDKButton.setText("Start Camera");
                } else {
                    isSDKStarted = true;
                    startDetector();
                    startSDKButton.setText("Stop Camera");
                }
            }
        });
        startSDKButton.setText("Start Camera");
*/
        //We create a custom SurfaceView that resizes itself to match the aspect ratio of the incoming camera frames
        mainLayout = (RelativeLayout) findViewById(R.id.text_blind_emotion_detector);

      //  startDetector();
        cameraPreview = new SurfaceView(this) {
            @Override
            public void onMeasure(int widthSpec, int heightSpec) {
                int measureWidth = MeasureSpec.getSize(widthSpec);
                int measureHeight = MeasureSpec.getSize(heightSpec);
                int width;
                int height;
                if (previewHeight == 0 || previewWidth == 0) {
                    width = measureWidth;
                    height = measureHeight;
                } else {
                    float viewAspectRatio = (float)measureWidth/measureHeight;
                    float cameraPreviewAspectRatio = (float) previewWidth/previewHeight;

                    if (cameraPreviewAspectRatio > viewAspectRatio) {
                        width = measureWidth;
                        height =(int) (measureWidth / cameraPreviewAspectRatio);
                    } else {
                        width = (int) (measureHeight * cameraPreviewAspectRatio);
                        height = measureHeight;
                    }
                }
                setMeasuredDimension(width,height);
            }
        };
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        cameraPreview.setLayoutParams(params);
        mainLayout.addView(cameraPreview,0);

       // surfaceViewVisibilityButton = (Button) findViewById(R.id.surfaceview_visibility_button);
       // surfaceViewVisibilityButton.setText("HIDE SURFACE VIEW");
      /*  surfaceViewVisibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraPreview.getVisibility() == View.VISIBLE) {
                    cameraPreview.setVisibility(View.INVISIBLE);
                    surfaceViewVisibilityButton.setText("SHOW SURFACE VIEW");
                } else {
                    cameraPreview.setVisibility(View.VISIBLE);
                    surfaceViewVisibilityButton.setText("HIDE SURFACE VIEW");
                }
            }
        });
        */

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_BACK, cameraPreview, 1, Detector.FaceDetectorMode.LARGE_FACES);
        detector.setLicensePath("Affdex.license");
        detector.setDetectAllAppearance(true);
        detector.setDetectAllEmotions(true);
        detector.setDetectAllEmojis(true);
        detector.setDetectAllExpressions(true);


        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
       // detector.setMaxProcessRate((float) 1);
        //switchCamera(isCameraBack ? CameraDetector.CameraType.CAMERA_BACK : CameraDetector.CameraType.CAMERA_FRONT);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isSDKStarted) {
            startDetector();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopDetector();

    }

    void startDetector() {
        if (!detector.isRunning()) {
            detector.start();
        }
    }

    void stopDetector() {
        if (detector.isRunning()) {
            detector.stop();
        }
    }

    void switchCamera(CameraDetector.CameraType type) {
        detector.setCameraType(type);
    }

    @Override
    public void onImageResults(List<Face> list, Frame frame, float v) {

        if (list == null)
            return;;
        if (list.size() == 0) {
            smileTextView.setText("NO FACE");
        } else {
            Face face = list.get(0);
            //smileTextView.setText(String.format("SMILE\n%.2f",face.expressions.getSmile()));
            /*if (face.expressions.getSmile() > 0.0) {
                speak ("smile");
            }*/
            if (rateOfCameraCalls != 4)
            {
                rateOfCameraCalls++;
                return;
            }
            rateOfCameraCalls = 0;

            Metrics detectedEmotion = Metrics.NO_EMOTION;
            float maxScore = 20;
            for (Metrics metric : Metrics.values()) {
                float scoreOfMetric = getScore(metric, face);
                if (scoreOfMetric > maxScore)
                {
                    detectedEmotion = metric;
                }
            }
            speak(detectedEmotion.name());

            //for (int i=0; i)

            /*float[] metricScore = new float[4];
            metricScore[0] = face.emotions.getJoy();
            metricScore[1] = face.emotions.getSurprise();
            metricScore[2] = face.emotions.getAnger();
            metricScore[3] = face.emotions.getSadness();

            int maxIndex = 0;
            float max = metricScore[0];
            for (int i=1; i<4; i++)
            {
                if (metricScore[i] > max)
                {
                    maxIndex = i;
                    max = metricScore[i];
                }
            }

            switch (maxIndex)
            {
                case 0:
                    speak("Joy");
                    break;
                case 1:
                    speak("Surprise");
                    break;
                case 2:
                    speak("Anger");
                    break;
                case 3:
                    speak("Sadness");
                    break;
                default:
                    break;
            }*/


           /* try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            initialized = true;
            tts.setLanguage(Locale.ENGLISH);

            if (queuedText != null) {
                speak(queuedText);
            }
        }
    }

    public void speak(String text) {
        // If not yet initialized, queue up the text.
        if (!initialized) {
            queuedText = text;
            return;
        }
        queuedText = null;
        // Before speaking the current text, stop any ongoing speech.
        tts.stop();
        // Speak the text.
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    float getScore(Metrics metric, Face face) {

        float score;

        switch (metric) {
            case ANGER:
                score = face.emotions.getAnger();
                break;
            case CONTEMPT:
                score = face.emotions.getContempt();
                break;
            case DISGUST:
                score = face.emotions.getDisgust();
                break;
            case FEAR:
                score = face.emotions.getFear();
                break;
            case JOY:
                score = face.emotions.getJoy();
                break;
            case SADNESS:
                score = face.emotions.getSadness();
                break;
            case SURPRISE:
                score = face.emotions.getSurprise();
                break;
            case ATTENTION:
                score = face.expressions.getAttention();
                break;
            case BROW_FURROW:
                score = face.expressions.getBrowFurrow();
                break;
            case BROW_RAISE:
                score = face.expressions.getBrowRaise();
                break;
            case CHIN_RAISER:
                score = face.expressions.getChinRaise();
                break;
            case ENGAGEMENT:
                score = face.emotions.getEngagement();
                break;
            case EYE_CLOSURE:
                score = face.expressions.getEyeClosure();
                break;
            case INNER_BROW_RAISER:
                score = face.expressions.getInnerBrowRaise();
                break;
            case LIP_DEPRESSOR:
                score = face.expressions.getLipCornerDepressor();
                break;
            case LIP_PRESS:
                score = face.expressions.getLipPress();
                break;
            case LIP_PUCKER:
                score = face.expressions.getLipPucker();
                break;
            case LIP_SUCK:
                score = face.expressions.getLipSuck();
                break;
            case MOUTH_OPEN:
                score = face.expressions.getMouthOpen();
                break;
            case NOSE_WRINKLER:
                score = face.expressions.getNoseWrinkle();
                break;
            case SMILE:
                score = face.expressions.getSmile();
                break;
            case SMIRK:
                score = face.expressions.getSmirk();
                break;
            case UPPER_LIP_RAISER:
                score = face.expressions.getUpperLipRaise();
                break;
            case VALENCE:
                score = face.emotions.getValence();
                break;
            case YAW:
                score = face.measurements.orientation.getYaw();
                break;
            case ROLL:
                score = face.measurements.orientation.getRoll();
                break;
            case PITCH:
                score = face.measurements.orientation.getPitch();
                break;
            case INTER_OCULAR_DISTANCE:
                score = face.measurements.getInterocularDistance();
                break;
            default:
                score = Float.NaN;
                break;
        }
        return score;
    }

    @Override
    public void onCameraSizeSelected(int width, int height, Frame.ROTATE rotate) {
        if (rotate == Frame.ROTATE.BY_90_CCW || rotate == Frame.ROTATE.BY_90_CW) {
            previewWidth = height;
            previewHeight = width;
        } else {
            previewHeight = height;
            previewWidth = width;
        }
        cameraPreview.requestLayout();
    }
}
