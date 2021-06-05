package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.mmue_sb10.persistence.EscapeDatabase;
import at.ac.tuwien.mmue_sb10.persistence.Highscore;
import at.ac.tuwien.mmue_sb10.persistence.OnHighscoresLoadedListener;
import at.ac.tuwien.mmue_sb10.util.Concurrency;

public class HighscoreActivity extends Activity {

    public static final int TOTAL_LEVELS = 5;

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

        /*for (int level = 0; level < TOTAL_LEVELS; level++) {
            Concurrency.executeAsync(() -> {
                List<Highscore> highscores = loadHighscores(level);
                runOnUiThread(() -> onHighscoresLoadedListener.onHighscoresLoaded(highscores));
            });
        }*/
    }

    @Override
    public void finish() {
        super.finish();
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(dm);
        float aspect_rounded = Math.round((float)dm.widthPixels / dm.heightPixels * 10) / 10f;
        if (aspect_rounded == Math.round(16f/9 * 10) / 10f) {
            overridePendingTransition(R.anim.shrink_main_activity_wide, R.anim.fade_out_activity);
        } else {
            overridePendingTransition(R.anim.shrink_main_activity_xwide, R.anim.fade_out_activity);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void onHighscoresLoaded(List<Highscore> highscores) {
    }

    /**
     * Loads the highscores from the database
     * @return Highscores List
     */
    private List<Highscore> loadHighscores(int level) {
        List<Highscore> highscores = EscapeDatabase.getInstance(this).highscoreDao().getHighscoresForLevel(level);
        return highscores;
    }
}