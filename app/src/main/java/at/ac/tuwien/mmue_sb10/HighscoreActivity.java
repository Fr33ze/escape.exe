package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import at.ac.tuwien.mmue_sb10.persistence.EscapeDatabase;
import at.ac.tuwien.mmue_sb10.persistence.Highscore;
import at.ac.tuwien.mmue_sb10.persistence.OnHighscoresLoadedListener;
import at.ac.tuwien.mmue_sb10.util.Concurrency;

public class HighscoreActivity extends Activity {

    public static final int TOTAL_LEVELS = 2; //TODO

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
        ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(dm);
        float aspect_rounded = Math.round((float) dm.widthPixels / dm.heightPixels * 10) / 10f;
        if (aspect_rounded == Math.round(16f / 9 * 10) / 10f) {
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
        for (int level = 0; level <= TOTAL_LEVELS; level++) {
            ArrayList<Highscore> filtered = new ArrayList<>();
            for (Highscore highscore : highscores) {
                if(highscore.level == level)
                    filtered.add(highscore);
            }

            if (level == 0) {
                final TableLayout table_highscores = findViewById(R.id.table_highscores);
                final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_header, null);
                TextView text;

                //Game beat
                text = tableRow.findViewById(R.id.aaah);
                text.setText(R.string.game_beat);

                //Deaths
                text = tableRow.findViewById(R.id.bbbh);
                text.setText(R.string.deaths_total);

                //Player
                text = tableRow.findViewById(R.id.ccch);
                text.setText(R.string.player);

                table_highscores.addView(tableRow);

                if (filtered.isEmpty()) {
                    final TableRow minusRow = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_highscore, null);
                    TextView minusText;

                    //Level
                    minusText = minusRow.findViewById(R.id.aaa);
                    minusText.setText("-");

                    //Deaths
                    minusText = minusRow.findViewById(R.id.bbb);
                    minusText.setText("-");

                    //Name
                    minusText = minusRow.findViewById(R.id.ccc);
                    minusText.setText("-");

                    table_highscores.addView(minusRow);
                }

            } else if (level > 0 && !filtered.isEmpty()) {
                final TableLayout table_highscores = findViewById(R.id.table_highscores);
                final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_header, null);
                tableRow.setPadding(0, 50, 0, 0);
                TextView text;

                //Level
                text = tableRow.findViewById(R.id.aaah);
                text.setText("Level");

                //Deaths
                text = tableRow.findViewById(R.id.bbbh);
                text.setText(R.string.deaths_level);

                //Player
                text = tableRow.findViewById(R.id.ccch);
                text.setText(R.string.player);

                table_highscores.addView(tableRow);
            }

            for (int i = 0, num_scores = Math.min(5, filtered.size()); i < num_scores; i++) {
                final TableLayout table_highscores = findViewById(R.id.table_highscores);
                final TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.tablerow_highscore, null);
                TextView text;

                //Level
                text = tableRow.findViewById(R.id.aaa);
                text.setText("Level " + filtered.get(i).level);

                //Deaths
                text = tableRow.findViewById(R.id.bbb);
                text.setText("" + filtered.get(i).deaths);

                //Name
                text = tableRow.findViewById(R.id.ccc);
                text.setText(filtered.get(i).name);

                table_highscores.addView(tableRow);
            }
        }
    }

    /**
     * Loads the highscores from the database
     *
     * @return Highscores List
     */
    private List<Highscore> loadHighscores() {
        List<Highscore> highscores = EscapeDatabase.getInstance(this).highscoreDao().getHighscores();
        return highscores;
    }
}