package at.ac.tuwien.mmue_sb10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;

import at.ac.tuwien.mmue_sb10.persistence.EscapeDatabase;
import at.ac.tuwien.mmue_sb10.persistence.Highscore;
import at.ac.tuwien.mmue_sb10.persistence.OnHighscoresLoadedListener;
import at.ac.tuwien.mmue_sb10.persistence.OnUserLoadedListener;
import at.ac.tuwien.mmue_sb10.persistence.User;
import at.ac.tuwien.mmue_sb10.util.Concurrency;

public class HighscoreActivity extends AppCompatActivity {

    private final OnHighscoresLoadedListener onHighscoresLoadedListener = this::onHighscoresLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Concurrency.executeAsync(() -> {
            List<Highscore> highscores = loadHighscores();
            runOnUiThread(() -> onHighscoresLoadedListener.onHighscoresLoaded(highscores));
        });
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