package at.ac.tuwien.mmue_sb10.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import at.ac.tuwien.mmue_sb10.R;
import at.ac.tuwien.mmue_sb10.persistence.User;
import at.ac.tuwien.mmue_sb10.persistence.UserRoomDatabase;
import at.ac.tuwien.mmue_sb10.util.Concurrency;

public class SaveActivity extends AppCompatActivity {

    private EditText editPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        editPlayerName = findViewById(R.id.editPlayerName);
    }

    public void onSaveButtonClicked(View v) {
        String name = editPlayerName.getText().toString();

        if (name.isEmpty()) return;

        // Save user
        Concurrency.executeAsync(() -> saveUser(new User(name)));

        editPlayerName.setText("");
    }

    public void saveUser(User user) {
        UserRoomDatabase.getInstance(this).userDao().insert(user);
    }

    public void goToLoadActivity(View v) {
        startActivity(new Intent(this, LoadActivity.class));
    }
}