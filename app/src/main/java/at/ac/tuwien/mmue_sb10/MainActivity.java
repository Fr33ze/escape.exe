/**
 * Main activity of the game currently works as a main menu.
 *
 * @author Lukas Lidauer & Jan König
 */
package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import at.ac.tuwien.mmue_sb10.persistence.EscapeDatabase;
import at.ac.tuwien.mmue_sb10.persistence.OnUserLoadedListener;
import at.ac.tuwien.mmue_sb10.persistence.User;
import at.ac.tuwien.mmue_sb10.util.Concurrency;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final OnUserLoadedListener onUserLoadedListener = this::onUserLoaded;

    private Button btn_continue;
    private User user;

    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;
    private boolean muted;

    private int sound_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // for other menu, uncomment these lines
        // WebView webView = findViewById(R.id.player_web_view);
        // webView.loadUrl("file:///android_asset/player_title.html");
        // webView.setBackgroundColor(Color.TRANSPARENT);
        // webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

        SharedPreferences sp = this.getSharedPreferences("escapePrefs", 0);
        muted = sp.getBoolean("muted", false);

        if (muted) {
            findViewById(R.id.btn_mute).setBackground(getResources().getDrawable(R.drawable.icon_mute));
        } else {
            findViewById(R.id.btn_mute).setBackground(getResources().getDrawable(R.drawable.icon_sound));
        }

        btn_continue = findViewById(R.id.btn_continue);

        Concurrency.executeAsync(() -> {
            User user = loadUser();
            runOnUiThread(() -> onUserLoadedListener.onUserLoaded(user));
        });
    }

    private void onUserLoaded(User user) {
        if (user == null)
            return;

        this.user = user;
        btn_continue.setEnabled(true);
    }

    /**
     * When clicked starts a new game by creating a new player profile first.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickNewGame(View v) {
        if (!muted)
            soundPool.play(sound_button, 1, 1, 0, 0, 1);

        if (user != null) {
            final EditText input = new EditText(MainActivity.this);
            input.setHint(R.string.player_name);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.new_game_warning)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Concurrency.executeAsync(() -> deleteUser());
                            Concurrency.executeAsync(() -> saveUser(new User(input.getText().toString())));
                            startActivity(new Intent(getBaseContext(), GameActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog

                        }
                    })
                    .setView(input);
            builder.create().show();
        } else {
            final EditText input = new EditText(MainActivity.this);
            input.setHint(R.string.player_name);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.enter_player_name)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Concurrency.executeAsync(() -> saveUser(new User(input.getText().toString())));
                            startActivity(new Intent(getBaseContext(), GameActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog

                        }
                    })
                    .setView(input);
            builder.create().show();
        }
    }

    public void onMuteClick(View v) {
        muted = !muted;
        if (muted) {
            v.setBackground(getResources().getDrawable(R.drawable.icon_mute));
            releaseMediaPlayer();
            releaseSoundPool();
        } else {
            v.setBackground(getResources().getDrawable(R.drawable.icon_sound));
            initMediaPlayer();
            initSoundPool();
        }

        SharedPreferences sp = this.getSharedPreferences("escapePrefs", 0);
        sp.edit().putBoolean("muted", muted).apply();
    }

    /**
     * When clicked takes you to current players overview and allows you to start the game from there
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickContinue(View v) {
        if (!muted)
            soundPool.play(sound_button, 1, 1, 0, 0, 1);
        Intent intent = new Intent(this, SubContinueActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onClickHighscores(View v) {
        if (!muted)
            soundPool.play(sound_button, 1, 1, 0, 0, 1);
        //TODO: Start highscores activity
    }

    /**
     * Closes the software.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickQuit(View v) {
        if (!muted)
            soundPool.play(sound_button, 1, 1, 0, 0, 1);
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(!muted) {
            releaseMediaPlayer();
            releaseSoundPool();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(!muted) {
            releaseMediaPlayer();
            releaseSoundPool();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = this.getSharedPreferences("escapePrefs", 0);
        muted = sp.getBoolean("muted", false);
        if (muted) {
            findViewById(R.id.btn_mute).setBackground(getResources().getDrawable(R.drawable.icon_mute));
        } else {
            findViewById(R.id.btn_mute).setBackground(getResources().getDrawable(R.drawable.icon_sound));
            initMediaPlayer();
            initSoundPool();
        }

        Concurrency.executeAsync(() -> {
            User user = loadUser();
            runOnUiThread(() -> onUserLoadedListener.onUserLoaded(user));
        });
    }

    /**
     * Initialise the MediaPlayer for the main activity
     */
    private void initMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.techno02);
            mediaPlayer.setVolume(1, 1);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (NullPointerException | IllegalStateException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Initialize the sound pool for the main activity
     */
    private void initSoundPool() {
        try {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            sound_button = soundPool.load(this, R.raw.button, 1);
        } catch (NullPointerException | IllegalStateException exc) {
            exc.printStackTrace();
        }
    }

    private void releaseMediaPlayer() {
        try {
            mediaPlayer.release();
        } catch (NullPointerException | IllegalStateException exc) {
            exc.printStackTrace();
        }
    }

    private void releaseSoundPool() {
        try {
            soundPool.release();
        } catch (NullPointerException | IllegalStateException exc) {
            exc.printStackTrace();
        }
    }

    /**
     * Loads the current user from the database
     * @return User or null, if it does not exist
     */
    private User loadUser() {
        List<User> users = EscapeDatabase.getInstance(this).userDao().selectAllUsers();
        User user;
        if (users.size() > 0)
            user = users.get(0);
        else
            user = null;
        return user;
    }

    /**
     * Saves a new User in the database
     * @param user
     */
    private void saveUser(User user) {
        EscapeDatabase.getInstance(this).userDao().insert(user);
    }

    /**
     * Deletes the current User from the database
     */
    private void deleteUser() {
        EscapeDatabase.getInstance(this).userDao().deleteAllUsers();
    }
}