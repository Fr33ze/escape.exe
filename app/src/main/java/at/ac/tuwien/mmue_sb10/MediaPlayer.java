package at.ac.tuwien.mmue_sb10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MediaPlayer extends AppCompatActivity {
    // Variables
    android.media.MediaPlayer player;
    boolean playerIsMuted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
    }

    public void onClickBackToMain(View v){
        Intent i2 = new Intent(MediaPlayer.this, MainActivity.class);
        MediaPlayer.this.startActivity(i2);
    }

    public void onClickPlay(View v) {
        Intent i = getIntent();
        int selectedSong = i.getIntExtra("selectedSong", 0);
        if (player == null) {
            player = android.media.MediaPlayer.create(this, selectedSong); //TODO: variable file
        }
        player.setVolume(1,1);
        playerIsMuted = false;
        player.start();
        player.setLooping(true);
    }

    public void onClickStop(View v) {
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