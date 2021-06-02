/**
 * Main activity of the game currently works as a main menu.
 * @author Lukas Lidauer & Jan König
 */
package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import at.ac.tuwien.mmue_sb10.persistence.EscapeDatabase;
import at.ac.tuwien.mmue_sb10.persistence.User;
import at.ac.tuwien.mmue_sb10.util.Concurrency;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private interface OnUserLoadedListener {
        void onUserLoaded(User user);
    }

    private final OnUserLoadedListener onUserLoadedListener_update = this::updateContinueButton;
    private final OnUserLoadedListener onUserLoadedListener_activity = this::startGameActivity;

    private Button btn_continue;
    private User user;

    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;

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
        btn_continue = findViewById(R.id.btn_continue);

        Concurrency.executeAsync(() -> {
            User user = loadUser();
            runOnUiThread(() -> onUserLoadedListener_update.onUserLoaded(user));
        });

        initMediaPlayer();
        initSoundPool();
    }

    private void updateContinueButton(User user) {
        if(user == null)
            return;

        this.user = user;
        btn_continue.setEnabled(true);
    }

    private void startGameActivity(User user) {
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    /**
     * When clicked starts a new game by creating a new player profile first.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickNewGame(View v) {
        soundPool.play(sound_button, 1, 1, 0, 0, 1);

        if(user != null) {
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
                            Concurrency.executeAsync(() -> {
                                User user = loadUser();
                                runOnUiThread(() -> onUserLoadedListener_activity.onUserLoaded(user));
                            });
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
                            Concurrency.executeAsync(() -> deleteUser());
                            Concurrency.executeAsync(() -> saveUser(new User(input.getText().toString())));
                            Concurrency.executeAsync(() -> {
                                User user = loadUser();
                                runOnUiThread(() -> onUserLoadedListener_activity.onUserLoaded(user));
                            });
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

    /**
     * When clicked takes you to current players overview and allows you to start the game from there
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickContinue(View v) {
        soundPool.play(sound_button, 1, 1, 0, 0, 1);
        Intent intent = new Intent(this, SubContinueActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onClickHighscores(View v) {
        soundPool.play(sound_button, 1, 1, 0, 0, 1);
        //TODO: Start highscores activity
    }

    /**
     * Closes the software.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickQuit(View v) {
        soundPool.play(sound_button, 1, 1, 0, 0, 1);
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        soundPool.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    /**
     * Initialise the MediaPlayer for the main activity
     */
    private void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.techno02);
        mediaPlayer.setVolume(1, 1);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    /**
     * Initialize the sound pool for the main activity
     */
    private void initSoundPool() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        sound_button = soundPool.load(this, R.raw.button, 1);
    }

    /**
     * Loads the current user from the database
     * @return User or null, if it does not exist
     */
    private User loadUser() {
        List<User> users = EscapeDatabase.getInstance(this).userDao().selectAllUsers();
        User user;
        if(users.size() > 0)
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