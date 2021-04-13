package at.ac.tuwien.mmue_sb10;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class CustomMediaPlayer extends AppCompatActivity {
    MediaPlayer player;
    boolean playerIsMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void play(View v) {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.techno03); //TODO: variable file
        }

        player.setVolume(1,1);
        playerIsMuted = false;
        player.start();
        player.setLooping(true);
    }

    public void onClickToggleMute(View v) {
        if (player != null) {
            player.setVolume(0,0);
            if(playerIsMuted == true) {
                player.setVolume(1,1);
            }
            playerIsMuted = !playerIsMuted;
        }
    }

    public void stop(View v) {
        stopPlayer();
    }

    private void stopPlayer() {
        if(player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
}