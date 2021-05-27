package at.ac.tuwien.mmue_sb10.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import at.ac.tuwien.mmue_sb10.GameActivity;
import at.ac.tuwien.mmue_sb10.MainActivity;
import at.ac.tuwien.mmue_sb10.R;
import at.ac.tuwien.mmue_sb10.persistence.User;
import at.ac.tuwien.mmue_sb10.persistence.UserRoomDatabase;
import at.ac.tuwien.mmue_sb10.util.Concurrency;

public class SubmenuNewGame extends AppCompatActivity {

    private EditText editPlayerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submenunewgame);

        editPlayerName = findViewById(R.id.editPlayerName);
    }

    /**
     * This function saves the name in database and then starts the game.
     * @param v is the view as used by this method
     * @since 0.2
     */
    public void onSaveButtonClicked(View v) {
        String name = editPlayerName.getText().toString();

        if (name.isEmpty()) return;

        // Save user
        Concurrency.executeAsync(() -> onClickSaveName(new User(name)));

        editPlayerName.setText("");
    }

    /**
     * Helper function for onSaveButtonClicked
     * @param user is the new user that gets created before starting the game
     * @since 0.2
     */
    public void onClickSaveName(User user) {
        UserRoomDatabase.getInstance(this).userDao().insert(user);
        Intent i = new Intent(this, GameActivity.class);
        startActivity(i);
    }

    /**
     * When clicked brings you back to the main menu.
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickBackToMain(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }
}