package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class GameActivity extends Activity {

    private static final String TAG = GameActivity.class.getSimpleName();

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        gameView = (GameView) findViewById(R.id.gameView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.endgame();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}