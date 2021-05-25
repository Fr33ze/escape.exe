package at.ac.tuwien.mmue_sb10.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import at.ac.tuwien.mmue_sb10.R;
import at.ac.tuwien.mmue_sb10.persistence.User;
import at.ac.tuwien.mmue_sb10.persistence.UserRoomDatabase;
import at.ac.tuwien.mmue_sb10.util.Concurrency;

public class LoadActivity extends AppCompatActivity {

    private interface OnUsersLoadedListener {
        void onUsersLoaded(List<User> users);
    }

    private final OnUsersLoadedListener onUsersLoadedListener = this::updateUsersTable;

    private TextView textViewUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        textViewUsers = findViewById(R.id.textViewUsers);

        // Load users
        Concurrency.executeAsync(() -> {
            List<User> users = loadUsers();
            runOnUiThread(() -> onUsersLoadedListener.onUsersLoaded(users));
        });
    }

    public void onDeleteButtonClicked(View v) {
        // Delete users
        Concurrency.executeAsync(this::deleteUsers);
        textViewUsers.setText("");
    }

    private List<User> loadUsers() {
        return UserRoomDatabase.getInstance(this).userDao().selectAllUsers();
    }

    private void deleteUsers() {
        UserRoomDatabase.getInstance(this).userDao().deleteAllUsers();
    }

    private void updateUsersTable(List<User> users) {
        StringBuilder text = new StringBuilder();
        for (User user : users) {
            text.append(user.name).append(": Level ").append(user.currentLevel).append("\n");
        }
        textViewUsers.setText(text.toString());
    }
}