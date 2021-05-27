package at.ac.tuwien.mmue_sb10.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import at.ac.tuwien.mmue_sb10.GameActivity;
import at.ac.tuwien.mmue_sb10.MainActivity;
import at.ac.tuwien.mmue_sb10.R;

public class SubmenuContinue extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submenucontinue);
    }


    /**
     * When clicked brings you back to the main menu.
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickBackToMain(View v) {
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * When clicked brings you to the submenu for creating a new player profile.
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickPlay(View v) {
        startActivity(new Intent(this, GameActivity.class));
    }
}