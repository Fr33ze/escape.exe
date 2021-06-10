// source: PersistentStorageExample2021

package at.ac.tuwien.mmue_sb10.persistence;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users")
    List<User> selectAllUsers();

    @Query("DELETE FROM users")
    void deleteAllUsers();
}