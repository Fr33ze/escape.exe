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

    // CONSTRUCTOR
    public User(String name) {
        this.name = name;
        this.currentLevel = 0;
        this.deathsCurrentLevel = 0;
        this.deathsTotal = 0;
        this.muted = false;
    }

    // GETTER, SETTER, HELPER-METHODS
    public int getCurrentLevel() {
        return currentLevel;
    }

    public void incCurrentLevel() {
        this.currentLevel++;
    }

    public int getDeathsTotal() {
        return deathsTotal;
    }

    public void incDeathsTotal() {
        this.deathsTotal++;
    }

    public int getDeathsCurrentLevel() {
        return deathsCurrentLevel;
    }

    public void incDeathsCurrentLevel() {
        this.deathsCurrentLevel++;
    }

    public boolean getMuted() {
        return muted;
    }

    public void toggleMuted() {
        this.muted = !this.muted;
    }

}
