package at.ac.tuwien.mmue_sb10;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import at.ac.tuwien.mmue_sb10.GameActivity;
import at.ac.tuwien.mmue_sb10.MainActivity;
import at.ac.tuwien.mmue_sb10.R;
import at.ac.tuwien.mmue_sb10.persistence.User;

public class SubContinueActivity extends Activity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submenucontinue);
        user = (User)getIntent().getSerializableExtra("user");
        ((TextView)findViewById(R.id.currentPlayer)).setText(user.name);
        ((TextView)findViewById(R.id.level)).setText("Level: " + user.currentLevel);
        ((TextView)findViewById(R.id.totaldeaths)).setText(getResources().getText(R.string.deaths_total).toString() + ": " + user.deathsTotal);
        ((TextView)findViewById(R.id.leveldeaths)).setText(getResources().getText(R.string.deaths_level).toString() + ": " + user.deathsCurrentLevel);
    }


    /**
     * When clicked brings you back to the main menu.
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickBackToMain(View v) {
        finish();
    }

    /**
     * When clicked brings you to the submenu for creating a new player profile.
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickPlay(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}