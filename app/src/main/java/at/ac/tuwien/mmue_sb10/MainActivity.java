/**
 * Main activity of the game currently works as a main menu.
 * @author Lukas Lidauer & Jan König
 */
package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * When clicked starts a new game.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickNewGame(View v) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    /**
     * Currently not implemented as there is not database for save files yet.
     * @param v the view as used by this method
     */
    public void onClickContinue(View v) {

    }

    /**
     * Closes the software.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickQuit(View v) {
        finish();
    }

    /**
     * Leads to the media player which currently is a stand alone media player meaning it's handled in a separate activity similar to the main menu
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickMediaPlayer(View v) {
        Intent i = new Intent(MainActivity.this, MediaPlayer.class);
        i.putExtra("selectedSong", R.raw.techno02); // Change this to play specific song
        MainActivity.this.startActivity(i);
    }

    /**
     * Leads to sound pool which currently is a stand alone sound pool meaning it's handled in a separate activity similar to the main menu
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickSoundPool(View v) {
        Intent i = new Intent(MainActivity.this, SoundPool.class);
        MainActivity.this.startActivity(i);
    }
}