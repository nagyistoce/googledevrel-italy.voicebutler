package it.rainbowbreeze.voicebutler.ui;

import it.rainbowbreeze.voicebutler.R;
import it.rainbowbreeze.voicebutler.common.AppEnv;
import it.rainbowbreeze.voicebutler.common.LogFacility;
import it.rainbowbreeze.voicebutler.logic.SpeechManager;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * http://www.androidadb.com/source/eyes-free-read-only/tts/src/com/google/tts/CheckVoiceData.java.html
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class ReadTextActivity extends Activity {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = ReadTextActivity.class.getSimpleName();

    private SpeechManager mSpeechManager;
    private LogFacility mLogFacility;
    private TextToSpeech mTextToSpeech;
    private EditText mTxtInput;
    private Button mBtnRead;

    private static final int REQUEST_TTS_CHECK = 100;

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
        
        setContentView(R.layout.act_read_text);
        
        mTxtInput = (EditText) findViewById(R.id.actReadText_txtInput);
        mBtnRead = (Button) findViewById(R.id.actReadText_btnRead);
 
        mBtnRead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = mTxtInput.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    saySomething(message);
                } else {
                    Toast.makeText(ReadTextActivity.this, R.string.actReadText_msgEmptyMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //disables all components. They will be enabled only if TTS engine and language will complete correctly.
        setComponentsEnableState(false);
        
        //launches TTS engine initialization
        mSpeechManager.initTTSEngine(this, REQUEST_TTS_CHECK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_TTS_CHECK:
            analyzeTTSResult(resultCode, data);
            break;
            
        default:
            super.onActivityResult(requestCode, resultCode, data);
            break;
        }
        
    }
    

    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods

    private void analyzeTTSResult(int resultCode, Intent data) {
        mLogFacility.v(LOG_HASH, "Returned from TTS engine resource checking with result: " + resultCode);

        switch (resultCode) {
        case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS: //optimum, all languages are installed
        case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA: //only some languages are installed
            mLogFacility.v(LOG_HASH, "TTS engine is present");
            isItalianLanguageAvailable(data);
            break;
        
        default:
            mLogFacility.i(LOG_HASH, "No TTS engine found");
            Toast.makeText(this, R.string.actReadText_msgNoTTSEngine, Toast.LENGTH_SHORT).show();
            break;
        }
    }

    /**
     * After the initialization of the TTS engine, checks if a particular language
     * file is available and, in case, loads it inside the engine
     * 
     * @param data
     */
    private void isItalianLanguageAvailable(Intent data) {
        mLogFacility.v(LOG_HASH, "Checking for italian language file");
        ArrayList<String> availableVoices = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES);
        boolean isItalianAvailable = false;
        for (String lang : availableVoices) {
            if ("ita-ita".equalsIgnoreCase(lang)) {
                isItalianAvailable = true;
                break;
            }
        }
        
        if (isItalianAvailable) {
            //OK, ready to use the TTS!
            mLogFacility.v(LOG_HASH, "Ok, italian language installed, proceed to TTS inizialization");
            mTextToSpeech = mSpeechManager.createTextToSpeech(getApplicationContext(), mTextToSpeechListener);
            //now wait for listener call
        } else {
            mLogFacility.i(LOG_HASH, "Cannot find italian language installed");
            Toast.makeText(this, R.string.actReadText_msgNoLanguageAvailable, Toast.LENGTH_SHORT).show();
        }
    }

    private OnInitListener mTextToSpeechListener = new TextToSpeech.OnInitListener() {
        public void onInit(int status) {
            if (TextToSpeech.SUCCESS != status) {
                mLogFacility.v(LOG_HASH, "TTS engine initialization has failed with status " + status);
                Toast.makeText(ReadTextActivity.this, R.string.actReadText_msgLanguageInitializationFailed, Toast.LENGTH_SHORT).show();
                return;
            }
            
            int result = mTextToSpeech.setLanguage(Locale.ITALY);
            if (result >= TextToSpeech.LANG_AVAILABLE) {
                mLogFacility.v(LOG_HASH, "Language have been inizialized, ready to use TTS engine");
                setComponentsEnableState(true);
            } else {
                mLogFacility.i(LOG_HASH, "Set language result has failed with code " + result);
            }
        }
    };
    
    /**
     * Set components as enabled/disabled
     * @param enabled
     */
    private void setComponentsEnableState(boolean enabled) {
        mTxtInput.setEnabled(enabled);
        mBtnRead.setEnabled(enabled);
    }
    
    private void saySomething(String message) {
        mTextToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    }
    

    // ----------------------------------------- Private Classes
    
}
