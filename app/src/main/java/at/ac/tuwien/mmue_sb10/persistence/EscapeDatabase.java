package at.ac.tuwien.mmue_sb10.persistence;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.HashMap;

@Database(entities = {User.class, Highscore.class}, version = 1)
public abstract class EscapeDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract HighscoreDao highscoreDao();

    private static final HashMap<Context, EscapeDatabase> INSTANCES = new HashMap<>();

    public static EscapeDatabase getInstance(Context context) {
        EscapeDatabase db = INSTANCES.get(context);
        if (db == null) {
            db = Room.databaseBuilder(context, EscapeDatabase.class, "escape_db").build();
            INSTANCES.put(context, db);
        }
        return db;
    }
}