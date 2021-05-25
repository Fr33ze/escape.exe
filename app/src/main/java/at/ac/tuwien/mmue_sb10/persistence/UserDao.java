package at.ac.tuwien.mmue_sb10.persistence;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM user")
    List<User> selectAllUsers();

    @Query("DELETE FROM user")
    void deleteAllUsers();
}