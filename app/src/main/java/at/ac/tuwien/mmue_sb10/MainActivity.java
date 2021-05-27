/**
 * Main activity of the game currently works as a main menu.
 * @author Lukas Lidauer & Jan König
 */
package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import at.ac.tuwien.mmue_sb10.activities.SubmenuContinue;
import at.ac.tuwien.mmue_sb10.activities.SubmenuNewGame;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu); // .activity_main for the other menu
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        WebView webView = findViewById(R.id.player_web_view);

        // for other menu, uncomment these lines
        // webView.loadUrl("file:///android_asset/player_title.html");
        // webView.setBackgroundColor(Color.TRANSPARENT);
        // webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        initMediaPlayer();
    }

    /**
     * When clicked starts a new game by creating a new player profile first.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickNewGame(View v) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    /**
     * When clicked starts a new game by creating a new player profile first.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickNewGameJan(View v) {
        Intent i = new Intent(this, SubmenuNewGame.class);
        startActivity(i);
    }

    /**
     * When clicked takes you to current players overview and allows you to start the game from there
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickContinue(View v) {
        startActivity(new Intent(this, SubmenuContinue.class));
    }

    /**
     * Closes the software.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickQuit(View v) {
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
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

    /**
     * Leads to the media player which currently is a stand alone media player meaning it's handled in a separate activity similar to the main menu
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickMediaPlayer(View v) {
        Intent i = new Intent(MainActivity.this, MediaPlayerExample.class);
        i.putExtra("selectedSong", R.raw.techno02); // Change this to play specific song
        MainActivity.this.startActivity(i);
    }

    /**
     * Leads to sound pool which currently is a stand alone sound pool meaning it's handled in a separate activity similar to the main menu
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickSoundPool(View v) {
        Intent i = new Intent(MainActivity.this, SoundPoolExample.class);
        MainActivity.this.startActivity(i);
    }
}