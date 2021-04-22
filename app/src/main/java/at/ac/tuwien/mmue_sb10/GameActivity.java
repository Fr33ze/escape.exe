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
import android.view.WindowManager;
import android.widget.LinearLayout;

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
        gameView = (GameView) findViewById(R.id.gameView);
    }

    /**
     * Stops running threads of gameView and destroys this activity.
     * @since 0.1
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameView.endgame();
    }

    /**
     * Handles presses of the back button .
     * Currently no dialog to ensure user actually wants to exit the application.
     * @since 0.1
     */
    // TODO: add dialog to make sure user doesn't accidentally closes the app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //gameView.pauseGame();
    }

    /**
     * If the activity is restarted after being paused onResume is called.
     * @since 0.1
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
}