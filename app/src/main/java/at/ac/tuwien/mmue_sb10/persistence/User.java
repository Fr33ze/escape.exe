package at.ac.tuwien.mmue_sb10.persistence;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "users")
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int currentLevel;
    public int deathsTotal;
    public int deathsCurrentLevel;
    public boolean muted;

    public User(String name) {
        this.name = name;
        this.currentLevel = 1;
        this.deathsCurrentLevel = 0;
        this.deathsTotal = 0;
        this.muted = false;
    }
}