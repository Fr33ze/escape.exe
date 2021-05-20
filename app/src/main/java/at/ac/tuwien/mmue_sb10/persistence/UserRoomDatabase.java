package at.ac.tuwien.mmue_sb10.persistence;



import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.HashMap;

@Database(entities = {User.class}, version = 1)
public abstract class UserRoomDatabase extends RoomDatabase {
    // todo

    public abstract UserDao userDao();

    private static final HashMap<Context, UserRoomDatabase> INSTANCES = new HashMap<>();

    public static UserRoomDatabase getInstance(Context context) {
        UserRoomDatabase db = INSTANCES.get(context);
        if (db == null) {
            db = Room.databaseBuilder(context, UserRoomDatabase.class, "users_db").build();
            INSTANCES.put(context, db);
        }
        return db;
    }
}


