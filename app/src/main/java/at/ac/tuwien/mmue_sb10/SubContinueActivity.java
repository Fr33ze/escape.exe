package at.ac.tuwien.mmue_sb10;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import at.ac.tuwien.mmue_sb10.GameActivity;
import at.ac.tuwien.mmue_sb10.MainActivity;
import at.ac.tuwien.mmue_sb10.R;
import at.ac.tuwien.mmue_sb10.persistence.User;

public class SubContinueActivity extends Activity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submenucontinue);
        user = (User)getIntent().getSerializableExtra("user");
        ((TextView)findViewById(R.id.currentPlayer)).setText(user.name);
        ((TextView)findViewById(R.id.level)).setText("" + user.currentLevel);
        ((TextView)findViewById(R.id.totaldeaths)).setText("" + user.deathsTotal);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        if (!EscapeSoundManager.getInstance(this).isMuted()) {
            EscapeSoundManager.getInstance(this).initMediaPlayer(R.raw.mmenu_bgmusic, true);
        }

        EscapeSoundManager.getInstance(this).lock();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EscapeSoundManager.getInstance(this).playSound(EscapeSoundManager.getInstance(this).snd_button);
    }

    @Override
    public void finish() {
        super.finish();
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(dm);
        float aspect_rounded = Math.round((float)dm.widthPixels / dm.heightPixels * 10) / 10f;
        if (aspect_rounded == Math.round(16f/9 * 10) / 10f) {
            overridePendingTransition(R.anim.shrink_main_activity_wide, R.anim.fade_out_activity);
        } else {
            overridePendingTransition(R.anim.shrink_main_activity_xwide, R.anim.fade_out_activity);
        }
    }

    /**
     * When clicked brings you back to the main menu.
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickBackToMain(View v) {
        EscapeSoundManager.getInstance(this).playSound(EscapeSoundManager.getInstance(this).snd_button);
        finish();
    }

    /**
     * When clicked brings you to the submenu for creating a new player profile.
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickPlay(View v) {
        EscapeSoundManager.getInstance(this).playSound(EscapeSoundManager.getInstance(this).snd_button);
        startActivity(new Intent(this, GameActivity.class));
    }
}