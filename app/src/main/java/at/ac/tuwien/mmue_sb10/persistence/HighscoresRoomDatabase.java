package at.ac.tuwien.mmue_sb10.persistence;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.HashMap;

@Database(entities = {Highscores.class}, version = 1)
public abstract class HighscoresRoomDatabase extends RoomDatabase {
    // todo

    public abstract HighscoresDao highscoresDao();

    private static final HashMap<Context, HighscoresRoomDatabase> INSTANCES = new HashMap<>();

    public static HighscoresRoomDatabase getInstance(Context context) {
        HighscoresRoomDatabase db = INSTANCES.get(context);
        if (db == null) {
            db = Room.databaseBuilder(context, HighscoresRoomDatabase.class, "highscores_db").build();
            INSTANCES.put(context, db);
        }
        return db;
    }
}