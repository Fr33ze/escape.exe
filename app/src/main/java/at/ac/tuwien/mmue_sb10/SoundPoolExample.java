/**
 * This class provides a sound pool for sound effects.
 * Currently it works as a standalone sound pool that can be accessed via main menu.
 * @author Jan König
 */
package at.ac.tuwien.mmue_sb10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class SoundPoolExample extends AppCompatActivity {
    private android.media.SoundPool soundPool;
    private int soundeffect1, soundeffect2;
    private int soundeffect1ID, soundeffect2ID; // needed to pause sounds on call of different sound


    /**
     * The sound pool needs to be created in respect due to the build version of the device.
     * The onCreate methods is overwrittent to do so.
     * Also this method loads sound effects (in this version this is static)
     * @since 0.1
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_pool);

        // new way of building soundpool
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new android.media.SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        // old version of building soundpool
        else{
            soundPool = new android.media.SoundPool(6, AudioManager.STREAM_MUSIC,0);
        }

        soundeffect1 = soundPool.load(this,R.raw.testsound1,1);
        soundeffect2 = soundPool.load(this,R.raw.testsound2,1);
    }

    /**
     * This function leads the user back to the main menu.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickBackToMain(View v){
        Intent i2 = new Intent(SoundPoolExample.this, MainActivity.class);
        SoundPoolExample.this.startActivity(i2);
    }

    /**
     * Depending on the sound effect that was specified by the user (by clicking a button) triggers the playing of said sound effect
     * Currently this is bound to buttons but in future versions it shall be triggered by the game loop
     * @param v the view as used by this method
     * @since 0.1
     */
    public void playSoundEffect(View v) {
        switch(v.getId()) {
            case R.id.btn_soundeffect1:
                soundeffect1ID = soundPool.play(soundeffect1, 1,1,0,0, 1); // loop -1: repeat forever, 0: don't repeat, n: repeat this many times
                // soundPool.pause(soundeffect2ID); // sound 2 will be paused when sound 1 is triggered
                // soundPool.autoPause(); // pauses every other sound when sound 1 is triggered
                break;
            case R.id.btn_soundeffect2:
                soundeffect2ID = soundPool.play(soundeffect2, 1,1,0,0, 1);
                break;
        }
    }

    /**
     * The default onDestroy function of the build in sound pool gets overwritten to allow for releasing resources.
     * @since 0.1
     */
    @Override
    protected void onDestroy() { // releases resources!!!
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}