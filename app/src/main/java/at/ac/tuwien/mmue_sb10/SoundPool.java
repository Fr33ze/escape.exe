package at.ac.tuwien.mmue_sb10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class SoundPool extends AppCompatActivity {
    private android.media.SoundPool soundPool;
    private int soundeffect1, soundeffect2;
    private int soundeffect1ID, soundeffect2ID; // needed to pause sounds on call of different sound



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

    public void onClickBackToMain(View v){
        Intent i2 = new Intent(SoundPool.this, MainActivity.class);
        SoundPool.this.startActivity(i2);
    }

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

    @Override
    protected void onDestroy() { // releases resources!!!
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }
}