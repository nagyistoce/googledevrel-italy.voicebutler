/**
 * 
 */
package it.rainbowbreeze.voicebutler.logic;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class SpeechManager {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties
    private TextToSpeech mTextToSpeech;
    public TextToSpeech getTextToSpeech() {
        return mTextToSpeech;
    }
    public void setTextToSpeech(TextToSpeech mTextToSpeech) {
        this.mTextToSpeech = mTextToSpeech;
    }

    
    

    // ------------------------------------------ Public Methods
    
    /**
     * Verifies if speech recognition feature is available on device
     * 
     * @param context
     * @return
     */
    public boolean isSpeechRecognitionAvailable(Context context) {
        //find out whether speech recognition is supported
        PackageManager packManager = context.getPackageManager();
        List<ResolveInfo> intActivities = packManager.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (intActivities.size() != 0) {
            //speech recognition is supported
            return true;
        } else {
              //speech recognition not supported
              return false;
          }        
    }

    public Intent createSpeechRecognitionIntent(String callingPackage) {
        //start the speech recognition intent passing required data
        Intent recognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //set calling package
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, callingPackage);
        //set speech model
        // LANGUAGE_MODEL_WEB_SEARCH : For short phrases
        // LANGUAGE_MODEL_FREE_FORM  : for something more similar to a free-form voice search (a natural sentence)
        recognitionIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //specify max number of results to retrieve
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        
        return recognitionIntent;
    }

    /**
     * Instruct the app to listen for user speech input
     */
    public void listenToSpeech(Activity activity, String prompt, int requestCode) {
        listenToSpeech(activity, prompt, null, requestCode);
    }
    
    /**
     * Instruct the app to listen for user speech input
     */
    public void listenToSpeech(Activity activity, String prompt, String ieft_language, int requestCode) {
        //start the speech recognition intent passing required data
        Intent recognitionIntent = createSpeechRecognitionIntent(activity.getClass().getPackage().getName());
        //message to display while listening
        recognitionIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, prompt);
        if (!TextUtils.isEmpty(ieft_language)) {
            recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, ieft_language);
        }
        //start listening
        activity.startActivityForResult(recognitionIntent, requestCode);
    }
    
    /**
     * Instruct the app to listen for user speech input
     */
    public void openWebSpeechSearch(Activity activity, int requestCode) {
        //start the speech recognition intent passing required data
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
        //indicate package
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, activity.getClass().getPackage().getName());
        //start listening
        activity.startActivityForResult(listenIntent, requestCode);
    }
    
    /**
     * Retrieves resources used by the current TTS engine.
     * 
     * @param activity
     * @param requestCode
     */
    public void retrieveTTSEngineResources(Activity activity, int requestCode) {
        //prepare the TTS to repeat chosen words
        Intent checkTTSIntent = new Intent();
        //check TTS data
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        //start the checking Intent - will retrieve result in onActivityResult
        activity.startActivityForResult(checkTTSIntent, requestCode);        
    }
    
    /**
     * Creates a new instance of a text to speech particular instance
     * 
     * @param context
     * @param listener
     * @return
     */
    public TextToSpeech createTTSEngine(Context context, TextToSpeech.OnInitListener listener) {
        TextToSpeech textToSpeech = new TextToSpeech(context, listener);
        return textToSpeech;
    }

    /**
     * Launches Play Store asking for TTS installation
     * @param activity
     */
    public void startTTSDownloadFromPlayStore(Activity activity, int requestCode) {
        //intent will take user to TTS download page in Google Play
        Intent installTTSIntent = new Intent();
        installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        activity.startActivityForResult(installTTSIntent, requestCode);
    }
    
    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}

