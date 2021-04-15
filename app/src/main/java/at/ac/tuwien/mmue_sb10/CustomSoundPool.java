package at.ac.tuwien.mmue_sb10;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

// TODO: make it work without buttons but intents, make it work inside of existing activity
public class CustomSoundPool extends AppCompatActivity {
    private SoundPool soundPool;
    private int soundeffect1, soundeffect2, soundeffect3, soundeffect4;
    private int soundeffect1ID, soundeffect2ID, soundeffect3ID, soundeffect4ID; // needed to pause sounds on call of different sound


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Build.Version has to be checked in order to use the right way to build the soundpool
        // new way of building soundpool
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        // old version of building soundpool
        else{
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC,0);
        }
        soundeffect1 = soundPool.load(this,R.raw.testsound1,1);
        soundeffect2 = soundPool.load(this,R.raw.testsound2,1);
        soundeffect3 = soundPool.load(this,R.raw.testsound3,1);
        soundeffect4 = soundPool.load(this,R.raw.testsound4,1);
    }

    public void playSoundEffect(View v) {
        switch(v.getId()) {
            case R.id.btn_se1: // THIS IS AN EXAMPLE SOUND THAT SHOWS ALL FUNCTIONALITY!
                soundeffect1ID = soundPool.play(soundeffect1, 1,1,0,0, 1); // loop -1: repeat forever, 0: don't repeat, n: repeat this many times
                soundPool.pause(soundeffect3ID); // sound 3 will be paused when sound 1 is triggered
                soundPool.autoPause(); // pauses every other sound when sound 1 is triggered
                break;
            case R.id.btn_se2:
                soundeffect2ID = soundPool.play(soundeffect2, 1,1,0,0, 1);
                break;
            case R.id.btn_se3:
                soundeffect3ID = soundPool.play(soundeffect3, 1,1,0,0, 1);
                break;
            case R.id.btn_se4:
                soundeffect4ID = soundPool.play(soundeffect4, 1,1,0,0, 1);
                break;
        }
    }

    @Override
    protected void onDestroy() { // releases resources!!!
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}