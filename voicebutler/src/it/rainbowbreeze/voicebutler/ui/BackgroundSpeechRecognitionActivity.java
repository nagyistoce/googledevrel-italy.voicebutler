package it.rainbowbreeze.voicebutler.ui;

import it.rainbowbreeze.voicebutler.R;
import it.rainbowbreeze.voicebutler.common.AppEnv;
import it.rainbowbreeze.voicebutler.common.LogFacility;
import it.rainbowbreeze.voicebutler.logic.SpeechManager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Don't forget in the manifest
 *  <uses-permission android:name="android.permission.RECORD_AUDIO"/>
 *     
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class BackgroundSpeechRecognitionActivity extends Activity {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = BackgroundSpeechRecognitionActivity.class.getSimpleName();

    private LogFacility mLogFacility;
    private SpeechManager mSpeechManager;
    private SpeechRecognizer mSpeechRecognizer;
    private Button mBtnStartRecognition;
    private TextView mLblResults;

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties
    
    // -------------------------------------------------- Events

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AppEnv appEnv = AppEnv.i(getApplicationContext());
        mLogFacility = appEnv.getLogFacility();
        mLogFacility.logStartOfActivity(getClass(), savedInstanceState);
        mSpeechManager = appEnv.geSpeechManager();
        
        setContentView(R.layout.act_background_speech_recognition);

        boolean isSpeechAvailable = mSpeechManager.isSpeechRecognitionAvailable(this);
        mLogFacility.v(LOG_HASH, "Speech recognition present: " + isSpeechAvailable);
        Toast.makeText(this, "Speech: " + isSpeechAvailable, Toast.LENGTH_SHORT).show();
        if (!isSpeechAvailable) {
            setComponentsEnableState(isSpeechAvailable);
            return;
        }

        mLblResults = (TextView) findViewById(R.id.actBackgroundRecognition_lblResults);
        mBtnStartRecognition = (Button) findViewById(R.id.actBackgroundRecognition_btnStartRecognition);
        mBtnStartRecognition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startListening();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mSpeechRecognizer) {
            mSpeechRecognizer.destroy();
        }
    }
    

    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods

    /**
     * Set components as enabled/disabled
     * @param enabled
     */
    private void setComponentsEnableState(boolean enabled) {
        mBtnStartRecognition.setEnabled(enabled);
    }
    
    /**
     * Starts listening
     */
    protected void startListening() {
        Intent recognizerIntent = mSpeechManager.createSpeechRecognitionIntent(
                BackgroundSpeechRecognitionActivity.class.getName());
        
        if (null == mSpeechRecognizer) {
            mLogFacility.v(LOG_HASH, "Creating SpeechRecognizer...");
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
            mSpeechRecognizer.setRecognitionListener(new MySpeechRecognitionListener());
            mLogFacility.v(LOG_HASH, "SpeechRecognizer created");
        }
        
        mLogFacility.v(LOG_HASH, "Start listening to speech input");
        mSpeechRecognizer.startListening(recognizerIntent);
        setComponentsEnableState(false);
    }
    
    // ----------------------------------------- Private Classes
    
    private class MySpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
            mLogFacility.v(LOG_HASH, "onBeginningOfSpeech");
            mLblResults.setText("");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            mLogFacility.v(LOG_HASH, "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            mLogFacility.v(LOG_HASH, "onEndOfSpeech");
            setComponentsEnableState(true);
        }

        @Override
        public void onError(int error) {
            mLogFacility.v(LOG_HASH, "onError: " + error);
            switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
            case SpeechRecognizer.ERROR_CLIENT:
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
            case SpeechRecognizer.ERROR_NETWORK:
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
            case SpeechRecognizer.ERROR_NO_MATCH:
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
            case SpeechRecognizer.ERROR_SERVER:
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                mLogFacility.e(LOG_HASH, "Specific error");
                break;

            default:
                mLogFacility.e(LOG_HASH, "Unknown Recognizer error");
                break;
            }
            setComponentsEnableState(true);
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            mLogFacility.v(LOG_HASH, "onEvent: " + eventType);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            mLogFacility.v(LOG_HASH, "Received speech recognition partial result");
            
            ArrayList<String> sentences = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(String sentence : sentences) {
                mLogFacility.v(LOG_HASH, " PARTIAL: " + sentence);
            }
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            mLogFacility.v(LOG_HASH, "onReadyForSpeech");
        }

        @Override
        public void onResults(Bundle results) {
            mLogFacility.v(LOG_HASH, "Received speech recognition final result");
            
            //EXTRA is different
            ArrayList<String> suggestedWords = results.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION);
            //scores available, but only for API 14
            //float[] scores = results.getFloatArray(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
            for(String sentence : suggestedWords) {
                mLogFacility.v(LOG_HASH, " " + sentence);
                mLblResults.append(sentence + "\n");
            }
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            //mLogFacility.v(LOG_HASH, "onRmsChanged:" + rmsdB);
       }
        
    }
    
}
