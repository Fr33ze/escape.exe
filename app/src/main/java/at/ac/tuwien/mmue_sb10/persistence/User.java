package at.ac.tuwien.mmue_sb10.persistence;


import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int currentLevel;
    public int deathsTotal;
    public int deathsCurrentLevel;
    public boolean muted;

    public User(String name) {
        this.name = name;
        // todo
    }
}



