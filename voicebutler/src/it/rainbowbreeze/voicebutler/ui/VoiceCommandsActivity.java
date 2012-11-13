package it.rainbowbreeze.voicebutler.ui;

import it.rainbowbreeze.voicebutler.R;
import it.rainbowbreeze.voicebutler.common.AppEnv;
import it.rainbowbreeze.voicebutler.common.LogFacility;
import it.rainbowbreeze.voicebutler.logic.SpeechManager;

import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class VoiceCommandsActivity extends Activity {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = VoiceCommandsActivity.class.getSimpleName();

    private SpeechManager mSpeechManager;
    private LogFacility mLogFacility;
    private TextToSpeech mTextToSpeech;
    private Button mBtnListenCommands;
    private TextView mTxtCommand;

    private static final int REQUEST_TTS_CHECK = 100;
    private static final int REQUEST_VOICE_COMMANDS = 101;

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
        
        setContentView(R.layout.act_voice_commands);
        
        mBtnListenCommands = (Button) findViewById(R.id.actVoiceCommands_btnListenCommand);
        mBtnListenCommands.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saySomething(R.string.actVoiceCommands_msgWaitForCommands);
                startListeningCommands(false);
            }
        });
        
        mTxtCommand = (TextView) findViewById(R.id.actVoiceCommands_lblCommand);

        //launches TTS engine initialization
        mSpeechManager.initTTSEngine(this, REQUEST_TTS_CHECK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_TTS_CHECK:
            analyzeTTSResult(resultCode, data);
            break;
        
        case REQUEST_VOICE_COMMANDS:
            analyzeSpeechResult(resultCode, data);

            
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
        ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (containsWord(suggestedWords, "tempo") >= 0) {
            mLogFacility.v(LOG_HASH, "tempo");
            mTxtCommand.setText(suggestedWords.get(0));
            saySomething("Che ci stai a fare qui dentro, esci e vai a fare un giro all'area");
            startListeningCommands(true);
        } else if (containsWord(suggestedWords, "ragazzi") >= 0) {
            mLogFacility.v(LOG_HASH, "ragazzi");
            mTxtCommand.setText(suggestedWords.get(0));
            saySomething("Inutile dirti che questi ragazzi sono i migliori!");
            startListeningCommands(true);
        } else if (containsWord(suggestedWords, "stai") >= 0) {
            mLogFacility.v(LOG_HASH, "stai");
            mTxtCommand.setText(suggestedWords.get(0));
            saySomething("Vorrei poter reclamare i miei diritti sindacali, ma la legge non e ancora pronta per me!");
            startListeningCommands(true);
        } else if (containsWord(suggestedWords, "grazie") >= 0) {
            mLogFacility.v(LOG_HASH, "grazie");
            mTxtCommand.setText(suggestedWords.get(0));
            saySomething("Alla prossima, speriamo il piu tardi possibile!");
            finish();
        } else {
            mLogFacility.v(LOG_HASH, "niente");
            mTxtCommand.setText(suggestedWords.get(0));
            saySomething(R.string.actVoiceCommands_msgUnknowCommand);
            startListeningCommands(true);
        }
    }


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
            startListeningCommands(false);
        } else {
            mLogFacility.i(LOG_HASH, "Cannot find italian language installed");
            Toast.makeText(this, R.string.actReadText_msgNoLanguageAvailable, Toast.LENGTH_SHORT).show();
        }
    }

    private OnInitListener mTextToSpeechListener = new TextToSpeech.OnInitListener() {
        public void onInit(int status) {
            if (TextToSpeech.SUCCESS != status) {
                mLogFacility.v(LOG_HASH, "TTS engine initialization has failed with status " + status);
                Toast.makeText(VoiceCommandsActivity.this, R.string.actReadText_msgLanguageInitializationFailed, Toast.LENGTH_SHORT).show();
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
        mBtnListenCommands.setEnabled(enabled);
    }
    
    private void saySomething(String message) {
        mTextToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
    }
    
    private void saySomething(int resId) {
        saySomething(getString(resId));
    }
    
    private void startListeningCommands(final boolean longWait) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    if (longWait) {
                        Thread.sleep(6000);
                    } else {
                        Thread.sleep(6000);
                    }
                } catch (InterruptedException e) {}
                mSpeechManager.listenToSpeech(
                        VoiceCommandsActivity.this,
                        getString(R.string.actVoiceCommands_msgWaitForCommands),
                        REQUEST_VOICE_COMMANDS);
            }
        });
        t.start();
    }
    
    private int containsWord(ArrayList<String> sentences, String wordToSearch) {
        for (int i=0; i<sentences.size(); i++) {
            String singleSentence = sentences.get(i);
            StringTokenizer words = new StringTokenizer(singleSentence);
            while (words.hasMoreElements()) {
                Object wordObject = words.nextElement();
                if (null != wordObject) {
                    String word = wordObject.toString();
                    if (word.toLowerCase().contains(wordToSearch.toLowerCase())) return i;
                }
            }
            i++;
        }
        return -1;
    }

    // ----------------------------------------- Private Classes
    
}
