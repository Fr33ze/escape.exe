package at.ac.tuwien.mmue_sb10;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class EscapeSoundManager {
    private static EscapeSoundManager sInstance;

    public int snd_button;
    public int snd_steps;
    public int snd_jump;
    public int snd_land;
    public int snd_gravity_up;
    public int snd_gravity_down;

    private Context context;
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private boolean muted;

    private EscapeSoundManager(Context context) {
        this.context = context.getApplicationContext();
        SharedPreferences sp = context.getSharedPreferences("escapePrefs", 0);
        muted = sp.getBoolean("muted", false);
    }

    public static EscapeSoundManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (EscapeSoundManager.class) {
                sInstance = new EscapeSoundManager(context);
            }
        }
        return sInstance;
    }

    public boolean isMuted() {
        return muted;
    }

    public void toggleMute(int music_resource) {
        muted = !muted;
        if (muted) {
            release();
        } else {
            initMediaPlayer(music_resource);
            initSoundPool();
        }
        SharedPreferences sp = context.getSharedPreferences("escapePrefs", 0);
        sp.edit().putBoolean("muted", muted).apply();
    }

    public void release() {
        releaseMediaPlayer();
        releaseSoundPool();
    }

    public void releaseMediaPlayer() {
        try {
            mediaPlayer.release();
        } catch (NullPointerException | IllegalStateException exc) {
            exc.printStackTrace();
        }
    }

    public void releaseSoundPool() {
        try {
            soundPool.release();
        } catch (NullPointerException | IllegalStateException exc) {
            exc.printStackTrace();
        }
    }

    public void initMediaPlayer(int music_resource) {
        if (muted)
            return;

        try {
            mediaPlayer = MediaPlayer.create(context, music_resource);
            mediaPlayer.setVolume(1, 1);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (NullPointerException | IllegalStateException exc) {
            exc.printStackTrace();
        }
    }

    public void initSoundPool() {
        if (muted)
            return;

        try {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            snd_button = soundPool.load(context, R.raw.button_click, 1);
            snd_gravity_up = soundPool.load(context, R.raw.gravity_to_invert, 1);
            snd_gravity_down = soundPool.load(context, R.raw.gravity_to_normal, 1);


        } catch (NullPointerException | IllegalStateException exc) {
            exc.printStackTrace();
        }
    }

    public void playSound(int sound_id) {
        if (muted)
            return;

        soundPool.play(sound_id, 1, 1, 0, 0, 1);
    }
}
