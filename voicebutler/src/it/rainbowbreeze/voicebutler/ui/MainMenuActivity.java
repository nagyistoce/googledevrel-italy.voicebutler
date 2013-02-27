/**
 * 
 */
package it.rainbowbreeze.voicebutler.ui;

import it.rainbowbreeze.voicebutler.R;
import it.rainbowbreeze.voicebutler.common.AppEnv;
import it.rainbowbreeze.voicebutler.common.LogFacility;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class MainMenuActivity extends Activity {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = MainMenuActivity.class.getSimpleName();

    private LogFacility mLogFacility;

    private ActivityHelper mActivityHelper;

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties


    // -------------------------------------------------- Events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppEnv appEnv = AppEnv.i(getApplicationContext());
        mLogFacility = appEnv.getLogFacility();
        mLogFacility.logStartOfActivity(getClass(), savedInstanceState);
        mActivityHelper = appEnv.getActivityHelper();

        setContentView(R.layout.act_main_menu);
        
        assignMenuAction(R.id.actMainMenu_btnCheckTTS, new View.OnClickListener() {
            public void onClick(View v) {
                mActivityHelper.openTTSResources(MainMenuActivity.this);
            }
        });
        assignMenuAction(R.id.actMainMenu_btnReadText, new View.OnClickListener() {
            public void onClick(View v) {
                mActivityHelper.openReadText(MainMenuActivity.this);
            }
        });
        assignMenuAction(R.id.actMainMenu_btnSimpleRecognition, new View.OnClickListener() {
            public void onClick(View v) {
                mActivityHelper.openSimpleRecognition(MainMenuActivity.this);
            }
        });
        assignMenuAction(R.id.actMainMenu_btnVoiceCommands, new View.OnClickListener() {
            public void onClick(View v) {
                mActivityHelper.openVoiceCommands(MainMenuActivity.this);
            }
        });
        assignMenuAction(R.id.actMainMenu_btnBackgroundRecognition, new View.OnClickListener() {
            public void onClick(View v) {
                mActivityHelper.openBackgroundRecognition(MainMenuActivity.this);
            }
        });
    }

    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods
    private void assignMenuAction(int buttonResourceId, View.OnClickListener listener) {
        View button = findViewById(buttonResourceId);
        if (null != button) {
            button.setOnClickListener(listener);
        } else {
            mLogFacility.e(LOG_HASH, "Button is unavailable: " + buttonResourceId);
        }
    }

    // ----------------------------------------- Private Classes
}
