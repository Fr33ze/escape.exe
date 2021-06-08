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

    public User() {}

    public User(String name, boolean tutorial) {
        this.name = name;
        this.deathsCurrentLevel = 0;
        this.deathsTotal = 0;

        if (tutorial)
            this.currentLevel = -5;
        else
            this.currentLevel = 1;
    }
}