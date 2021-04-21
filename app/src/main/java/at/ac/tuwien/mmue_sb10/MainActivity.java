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

    public void onClickNewGame(View v) {
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    public void onClickContinue(View v) {

    }

    public void onClickQuit(View v) {
        finish();
    }

    public void onClickMediaPlayer(View v) {
        Intent i = new Intent(MainActivity.this, MediaPlayer.class);
        i.putExtra("selectedSong", R.raw.techno02); // Change this to play specific song
        MainActivity.this.startActivity(i);
    }

    public void onClickSoundPool(View v) {
        Intent i = new Intent(MainActivity.this, SoundPool.class);
        MainActivity.this.startActivity(i);
    }
}