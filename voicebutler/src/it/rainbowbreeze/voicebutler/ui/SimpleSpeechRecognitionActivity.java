package it.rainbowbreeze.voicebutler.ui;

import it.rainbowbreeze.voicebutler.R;
import it.rainbowbreeze.voicebutler.common.AppEnv;
import it.rainbowbreeze.voicebutler.common.LogFacility;
import it.rainbowbreeze.voicebutler.logic.SpeechManager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * http://developer.android.com/reference/android/speech/RecognizerIntent.html
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class SimpleSpeechRecognitionActivity extends Activity {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = SimpleSpeechRecognitionActivity.class.getSimpleName();

    private SpeechManager mSpeechManager;
    private LogFacility mLogFacility;
    private Button mBtnWordsRecognition;
    private ListView mLstWords;
    private ArrayAdapter<String> mWordsListAdapter;

    private Button mBtnWebSearch;

    private Button mBtnWordsRecognitionEn;
 
    private static final int REQUEST_SPEECH_RECOGNITION = 100;
    private static final int REQUEST_WEB_SEARCH = 101;

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
        
        setContentView(R.layout.act_simple_speech_recognition);

        boolean isSpeechAvailable = mSpeechManager.isSpeechRecognitionAvailable(this);
        mLogFacility.v(LOG_HASH, "Speech recognition present: " + isSpeechAvailable);
        Toast.makeText(this, "Speech: " + isSpeechAvailable, Toast.LENGTH_SHORT).show();
        if (!isSpeechAvailable) {
            setComponentsEnableState(isSpeechAvailable);
            return;
        }

        mBtnWordsRecognition = (Button) findViewById(R.id.actSimpleSpeechRecognition_btnWordsRecognition);
        mBtnWordsRecognition.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSpeechManager.listenToSpeech(
                        SimpleSpeechRecognitionActivity.this,
                        getString(R.string.actSimpleRecognition_msgSaySomething),
                        REQUEST_SPEECH_RECOGNITION);
            }
        });
        
        mBtnWordsRecognitionEn = (Button) findViewById(R.id.actSimpleSpeechRecognition_btnWordsRecognitionEn);
        mBtnWordsRecognitionEn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSpeechManager.listenToSpeech(
                        SimpleSpeechRecognitionActivity.this,
                        getString(R.string.actSimpleRecognition_msgSaySomething),
                        "en-US",
                        REQUEST_SPEECH_RECOGNITION);
            }
        });
        
        mBtnWebSearch = (Button) findViewById(R.id.actSimpleSpeechRecognition_btnWebSearch);
        mBtnWebSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSpeechManager.openWebSpeechSearch(
                        SimpleSpeechRecognitionActivity.this,
                        REQUEST_WEB_SEARCH);
            }
        });
        
        mWordsListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mLstWords = (ListView) findViewById(R.id.actSimpleSpeechRecognition_lstWords);
        mLstWords.setAdapter(mWordsListAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_SPEECH_RECOGNITION:
            analyzeSpeechResult(resultCode, data);
            break;
            
        default:
            super.onActivityResult(requestCode, resultCode, data);
            break;
        }
    }
    

    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods

    private void analyzeSpeechResult(int resultCode, Intent data) {
        mLogFacility.v(LOG_HASH, "Returned from speech recognition with result: " + resultCode);
        if (RESULT_OK != resultCode) {
            Toast.makeText(this, R.string.common_msgListeningError, Toast.LENGTH_SHORT).show();
            return;
        }
        
        //stores the returned word list as an ArrayList
        ArrayList<String> suggestedWords = data.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS);
        float[] scores = data.getFloatArrayExtra(
                RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
        mWordsListAdapter.clear();
        for (int i=0; i<suggestedWords.size(); i++) {
            String word = suggestedWords.get(i);
            String finalString = word + " - " + scores[i];
            mLogFacility.v(LOG_HASH, finalString);
            mWordsListAdapter.add(finalString);
        }
        mWordsListAdapter.notifyDataSetChanged();
    }

    /**
     * Set components as enabled/disabled
     * @param enabled
     */
    private void setComponentsEnableState(boolean enabled) {
        mBtnWordsRecognition.setEnabled(enabled);
        mLstWords.setEnabled(enabled);
    }
    
    // ----------------------------------------- Private Classes
    
}
