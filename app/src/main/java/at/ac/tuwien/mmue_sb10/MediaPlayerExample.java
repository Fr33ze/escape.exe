/**
 * This class provides a Media player for background music and the like.
 * An intent will specify the media resource that the player is going to play.
 * @author Jan König
 */

package at.ac.tuwien.mmue_sb10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MediaPlayerExample extends AppCompatActivity {
    // Variables
    /**
     * Is the instance of the media player
     */
    android.media.MediaPlayer player;
    /**
     * Tracks if the media player is currently muted or not
     */
    boolean playerIsMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
    }


    /**
     * This function leads the user back to the main menu.
     * @param v is the view as used by this method
     * @since 0.1
     */
    public void onClickBackToMain(View v){
        Intent i2 = new Intent(MediaPlayerExample.this, MainActivity.class);
        MediaPlayerExample.this.startActivity(i2);
    }

    /**
     * Checks if a media player was already created. If so, that media player plays the currently selected song.
     * If no media player was created yet this method creates one and then continues to play the currently selected song.
     * @param v is the view as used by this method
     * @since 0.1
     */
    public void onClickPlay(View v) {
        Intent i = getIntent();
        /**
         * Currently selected Song ais delivered by intent i
         */
        int selectedSong = i.getIntExtra("selectedSong", 0);
        if (player == null) {
            player = android.media.MediaPlayer.create(this, selectedSong); //TODO: variable file
        }
        player.setVolume(1,1);
        playerIsMuted = false;
        player.start();
        /**
         * The media Player is set to loop by default
         */
        player.setLooping(true);
    }

    /**
     * Stops the media player.
     * To do this it calls a helper function (stopPlayer()) which releases resources.
     * @param v is the view as used by this method
     * @since 0.1
     */
    public void onClickStop(View v) {
        stopPlayer();
    }

    /**
     * Stops the media player as soon as onClickStop calls this function.
     * @since 0.1
     */
    private void stopPlayer() {
        if(player != null) {
            player.release();
            player = null;
        }
    }

    /**
     * The default onStop function of the build in media player gets overwritten to allow for releasing resources.
     * @since 0.1
     */
    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

    /**
     * Toggles between muted and not-muted. Does so by simply setting the volume of the media player.
     * @param v the view as used by this method
     */
    public void onClickToggleMute(View v) {
        if (player != null) {
            player.setVolume(0,0);
            if(playerIsMuted == true) {
                player.setVolume(1,1);
            }
            playerIsMuted = !playerIsMuted;
        }
    }
}