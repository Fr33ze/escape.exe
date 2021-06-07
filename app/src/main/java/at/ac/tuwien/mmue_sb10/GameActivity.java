/**
 * Basic activity that calls GameView and is responsible for state of app (via onDestroy and onBackPressed)
 * @author Lukas Lidauer & Jan König
 */

package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import at.ac.tuwien.mmue_sb10.persistence.User;

public class GameActivity extends Activity {

    private static final String TAG = GameActivity.class.getSimpleName();
    private GameView gameView;

    /**
     * Starts an activity
     * @param savedInstanceState saves data in case onCreate is called again (e.g. change of orientation)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    /**
     * Stops running threads of gameView and destroys this activity.
     * @since 0.1
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Handles presses of the back button .
     * Currently no dialog to ensure user actually wants to exit the application.
     * @since 0.1
     */
    @Override
    public void onBackPressed() {
        gameView.onBackPressed();
    }

    /**
     * If the activity is restarted after being paused onResume is called.
     * @since 0.1
     */
    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        gameView = findViewById(R.id.gameView);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}