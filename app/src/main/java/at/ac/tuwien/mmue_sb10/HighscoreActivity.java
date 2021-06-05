package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.core.app.ActivityOptionsCompat;

import java.util.List;

import at.ac.tuwien.mmue_sb10.persistence.EscapeDatabase;
import at.ac.tuwien.mmue_sb10.persistence.Highscore;
import at.ac.tuwien.mmue_sb10.persistence.OnHighscoresLoadedListener;
import at.ac.tuwien.mmue_sb10.util.Concurrency;

public class HighscoreActivity extends Activity {

    private final OnHighscoresLoadedListener onHighscoresLoadedListener = this::onHighscoresLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        Concurrency.executeAsync(() -> {
            List<Highscore> highscores = loadHighscores();
            runOnUiThread(() -> onHighscoresLoadedListener.onHighscoresLoaded(highscores));
        });
    }

    @Override
    public void finish() {
        super.finish();
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(dm);
        float aspect = (float)dm.widthPixels / dm.heightPixels;
        if (aspect == 20f/9) {
            overridePendingTransition(R.anim.shrink_main_activity_xwide, R.anim.fade_out_activity);
        } else if (aspect == 16f/9){
            overridePendingTransition(R.anim.shrink_main_activity_wide, R.anim.fade_out_activity);
        } else {
            overridePendingTransition(R.anim.shrink_main_activity_wide, R.anim.fade_out_activity);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void onHighscoresLoaded(List<Highscore> highscores) {

        //TODO: display list
    }

    /**
     * Loads the highscores from the database
     * @return Highscores List
     */
    private List<Highscore> loadHighscores() {
        List<Highscore> highscores = EscapeDatabase.getInstance(this).highscoreDao().getHighscoresForEachLevel();
        return highscores;
    }
}