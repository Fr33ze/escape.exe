package at.ac.tuwien.mmue_sb10.persistence;

import java.util.List;

public interface OnHighscoresLoadedListener {
    void onHighscoresLoaded(List<Highscore> highscores);
}
