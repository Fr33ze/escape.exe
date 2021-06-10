// source: PersistentStorageExample2021

package at.ac.tuwien.mmue_sb10.persistence;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HighscoreDao {

    /**
     * DAO for inserting an Highscore object into the DB
     * @param highscore the object that gets inserted
     * @since 1.0
     * @author Lukas Lidauer & Jan König
     */
    @Insert
    void insert(Highscore highscore);

    /**
     * Returns a list of Scores from the DB for a certain level. Ordered by amount of deaths.
     * @since 1.0
     * @author Lukas Lidauer & Jan König
     */
    @Query("SELECT * FROM highscores WHERE level == :level ORDER BY deaths ASC")
    List<Highscore> getHighscoresForLevel(int level);

    /**
     * Returns a list of Scores from the DB. Order by amount of deaths.
     * @since 1.0
     * @author Lukas Lidauer & Jan König
     */
    @Query("SELECT * FROM highscores ORDER BY deaths")
    List<Highscore> getHighscores();
}
