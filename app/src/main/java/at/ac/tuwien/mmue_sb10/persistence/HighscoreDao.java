package at.ac.tuwien.mmue_sb10.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HighscoreDao {
    @Insert
    void insert(Highscore highscore);

    @Query("SELECT * FROM highscores WHERE level == :level ORDER BY deaths ASC")
    List<Highscore> getHighscoresForLevel(int level);
}
