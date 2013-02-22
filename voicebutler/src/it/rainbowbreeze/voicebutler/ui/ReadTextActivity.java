package it.rainbowbreeze.voicebutler.ui;

import java.util.Locale;

import it.rainbowbreeze.voicebutler.R;
import it.rainbowbreeze.voicebutler.common.AppEnv;
import it.rainbowbreeze.voicebutler.common.LogFacility;
import android.app.Activity;
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

    private LogFacility mLogFacility;
    private TextToSpeech mTextToSpeech;
    private EditText mTxtInput;
    private Button mBtnRead;

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties
    
    // -------------------------------------------------- Events

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AppEnv appEnv = AppEnv.i(getApplicationContext());
        mLogFacility = appEnv.getLogFacility();
        mLogFacility.logStartOfActivity(getClass(), savedInstanceState);
        
        setContentView(R.layout.act_read_text);
        
        mTxtInput = (EditText) findViewById(R.id.actReadText_txtInput);
        mBtnRead = (Button) findViewById(R.id.actReadText_btnRead);
 
        mBtnRead.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saySomething(mTxtInput.getText().toString());
            }
        });

        //disables all components. They will be enabled only if TTS engine and language are available.
        setComponentsEnableState(false);
        
        createTTSEngine();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mTextToSpeech) {
            mTextToSpeech.shutdown();
        }
    }
    

    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods

    /**
     * Set components as enabled/disabled
     * @param enabled
     */
    private void setComponentsEnableState(boolean enabled) {
        mTxtInput.setEnabled(enabled);
        mBtnRead.setEnabled(enabled);
    }
    
    /**
     * Creates the TTS Engine
     */
    private void createTTSEngine() {
        //launches TTS engine initialization
        mLogFacility.v(LOG_HASH, "Creating TTS engine");
        mTextToSpeech = new TextToSpeech(
                getApplicationContext(),
                mTextToSpeechListener);
    }

    private void saySomething(String message) {
        if (!TextUtils.isEmpty(message)) {
            mLogFacility.v(LOG_HASH, "Reading: " + message);
            mTextToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            Toast.makeText(ReadTextActivity.this, R.string.actReadText_msgEmptyMessage, Toast.LENGTH_SHORT).show();
        }
    }
    

    // ----------------------------------------- Private Classes
    private OnInitListener mTextToSpeechListener = new TextToSpeech.OnInitListener() {
        public void onInit(int status) {
            if (TextToSpeech.SUCCESS != status) {
                mLogFacility.v(LOG_HASH, "TTS engine initialization has failed with status " + status);
                Toast.makeText(ReadTextActivity.this, R.string.actReadText_msgLanguageInitializationFailed, Toast.LENGTH_SHORT).show();
                return;
            }
            
            //searches for "it_IT" language (language and country) 
            Locale itaLangAvailable = null;
            int itaCheckResult = mTextToSpeech.isLanguageAvailable(Locale.ITALY);
            
            switch (itaCheckResult) {
            case TextToSpeech.LANG_COUNTRY_AVAILABLE:
            case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                itaLangAvailable = Locale.ITALY; //"it_IT" language and country
                break;
                    
            case TextToSpeech.LANG_AVAILABLE:
                itaLangAvailable = Locale.ITALIAN; //"it" language only
                break;
                
            case TextToSpeech.LANG_MISSING_DATA:
            case TextToSpeech.LANG_NOT_SUPPORTED:                
            default:
                mLogFacility.v(LOG_HASH, "TTS engine doesn't have / support Italian language: " + itaCheckResult);
                Toast.makeText(ReadTextActivity.this, R.string.actReadText_msgLanguageInitializationFailed, Toast.LENGTH_SHORT).show();
                return;
            }
            
            int itaInitResult = mTextToSpeech.setLanguage(itaLangAvailable);
            if (itaInitResult >= TextToSpeech.LANG_AVAILABLE) {
                mLogFacility.v(LOG_HASH, "Language have been inizialized, ready to use TTS engine");
                setComponentsEnableState(true);
            } else {
                mLogFacility.i(LOG_HASH, "Set language result has failed with code " + itaInitResult);
            }
        }
    };
        
    // ----------------------------------------- Private Classes
}
