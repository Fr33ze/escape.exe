package at.ac.tuwien.mmue_sb10.persistence;

import java.util.List;

public interface OnHighscoresLoadedListener {
    //todo
    /**
     *
     * @since 1.0
     * @author Lukas Lidauer & Jan König
     */
    void onHighscoresLoaded(List<Highscore> highscores);
}
