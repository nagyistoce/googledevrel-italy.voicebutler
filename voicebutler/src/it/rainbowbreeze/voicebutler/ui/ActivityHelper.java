/**
 * 
 */
package it.rainbowbreeze.voicebutler.ui;

import android.app.Activity;
import android.content.Context;
import it.rainbowbreeze.libs.common.IRainbowLogFacility;
import it.rainbowbreeze.libs.ui.RainbowActivityHelper;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActivityHelper extends RainbowActivityHelper {

    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors
    public ActivityHelper(IRainbowLogFacility logFacility, Context context) {
        super(logFacility, context);
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public void openTTSResources(Activity callerActivity) {
        openActivity(callerActivity, TTSResourcesActivity.class, null, false, REQUESTCODE_NONE);
    }

    public void openReadText(Activity callerActivity) {
        openActivity(callerActivity, ReadTextActivity.class, null, false, REQUESTCODE_NONE);
    }

    public void openSimpleRecognition(Activity callerActivity) {
        openActivity(callerActivity, SimpleSpeechRecognitionActivity.class, null, false, REQUESTCODE_NONE);
    }

    public void openVoiceCommands(Activity callerActivity) {
        openActivity(callerActivity, VoiceCommandsActivity.class, null, false, REQUESTCODE_NONE);
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
