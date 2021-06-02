package at.ac.tuwien.mmue_sb10.persistence;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "highscores")
public class Highscore {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int level;
    public int deaths;

    public Highscore(String name, int level, int deaths) {
        this.name = name;
        this.level = level;
        this.deaths = deaths;
    }
}
