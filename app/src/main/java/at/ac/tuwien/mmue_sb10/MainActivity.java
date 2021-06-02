/**
 * Main activity of the game currently works as a main menu.
 * @author Lukas Lidauer & Jan König
 */
package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;

import at.ac.tuwien.mmue_sb10.activities.SubmenuContinue;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;

    private int sound_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // .activity_main for the other menu
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // for other menu, uncomment these lines
        // WebView webView = findViewById(R.id.player_web_view);
        // webView.loadUrl("file:///android_asset/player_title.html");
        // webView.setBackgroundColor(Color.TRANSPARENT);
        // webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

        initMediaPlayer();
        initSoundPool();
    }

    /**
     * When clicked starts a new game by creating a new player profile first.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickNewGame(View v) {
        soundPool.play(sound_button, 1, 1, 0, 0, 1);
        startActivity(new Intent(this, GameActivity.class));
    }

    /**
     * When clicked takes you to current players overview and allows you to start the game from there
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickContinue(View v) {
        soundPool.play(sound_button, 1, 1, 0, 0, 1);
        startActivity(new Intent(this, SubmenuContinue.class));
    }

    /**
     * Closes the software.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickQuit(View v) {
        soundPool.play(sound_button, 1, 1, 0, 0, 1);
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        soundPool.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.techno02);
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void initSoundPool() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound_button = soundPool.load(this, R.raw.button, 1);
    }
}