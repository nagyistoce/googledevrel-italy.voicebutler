package it.rainbowbreeze.voicebutler.ui;

import it.rainbowbreeze.voicebutler.R;
import it.rainbowbreeze.voicebutler.common.AppEnv;
import it.rainbowbreeze.voicebutler.common.LogFacility;
import it.rainbowbreeze.voicebutler.logic.SpeechManager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class TTSResourcesActivity extends Activity {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = TTSResourcesActivity.class.getSimpleName();

    private SpeechManager mSpeechManager;
    private ListView mLstAvailable;
    private ListView mLstUnavailable;
    private Button mBtnDownloadMore;

    private LogFacility mLogFacility;
    
    private static final int REQUEST_TTS_CHECK = 100;
    private static final int REQUEST_TTS_EDIT_CONFIG = 101;

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
        
        setContentView(R.layout.act_tts_resources);
        
        mLstAvailable = (ListView) findViewById(R.id.actTTSResources_lstAvailable);
        mLstUnavailable = (ListView) findViewById(R.id.actTTSResources_lstUnavailable);
        mBtnDownloadMore = (Button) findViewById(R.id.actTTSResources_btnDownloadMore);
 
        mBtnDownloadMore.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSpeechManager.startTTSDownloadFromPlayStore(TTSResourcesActivity.this, REQUEST_TTS_EDIT_CONFIG);
            }
        });
        
        launchTTSCapabilitesRefresh();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_TTS_CHECK:
            analyzeTTSResult(resultCode, data);
            break;
            
        case REQUEST_TTS_EDIT_CONFIG:
            launchTTSCapabilitesRefresh();
            break;

        default:
            super.onActivityResult(requestCode, resultCode, data);
            break;
        }
        
    }
    

    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods
    private void launchTTSCapabilitesRefresh() {
        mSpeechManager.retrieveTTSEngineResources(this, REQUEST_TTS_CHECK);
    }

    private void analyzeTTSResult(int resultCode, Intent data) {
        mLogFacility.v(LOG_HASH, "Returned from TTS engine resource checking with result: " + resultCode);
        
        boolean voicesFound = false;
        
        switch (resultCode) {
        //optimum, all languages are installed
        case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
            mLogFacility.v(LOG_HASH, "All languages available");
            mBtnDownloadMore.setEnabled(true);
            voicesFound = true;
            break;

        //only some languages are installed
        case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:
            mLogFacility.v(LOG_HASH, "Some languages available, someothers no");
            mBtnDownloadMore.setEnabled(true);
            voicesFound = true;
            break;

        //mmmm... some sort of troubles :(
        case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:
        case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME: 
        case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
            String message = String.format(
                    getString(R.string.actTTSResources_msgErrorProcessingData),
                    resultCode);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            mBtnDownloadMore.setEnabled(true);
            break;
            
        default:
            mLogFacility.i(LOG_HASH, "Unmanaged return from the TTS engine resources checking");
            break;
        }
        
        if (voicesFound) {
            //populated listview with languages
            ArrayList<String> availableVoices = 
                    data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_AVAILABLE_VOICES);
            mLstAvailable.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, availableVoices));

            ArrayList<String> unavailableVoices = 
                    data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES);
            mLstUnavailable.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, unavailableVoices));
        }
    }


    // ----------------------------------------- Private Classes
}
