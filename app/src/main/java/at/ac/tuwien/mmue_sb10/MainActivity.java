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

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EscapeSoundManager.getInstance(this).release();
    }

    @Override
    protected void onPause() {
        super.onPause();

        EscapeSoundManager.getInstance(this).release();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (EscapeSoundManager.getInstance(this).isMuted()) {
            findViewById(R.id.btn_mute).setBackground(getResources().getDrawable(R.drawable.icon_mute));
        } else {
            findViewById(R.id.btn_mute).setBackground(getResources().getDrawable(R.drawable.icon_sound));
            EscapeSoundManager.getInstance(this).initMediaPlayer(R.raw.techno02);
            EscapeSoundManager.getInstance(this).initSoundPool();
        }

        Concurrency.executeAsync(() -> {
            User user = loadUser();
            runOnUiThread(() -> onUserLoadedListener.onUserLoaded(user));
        });
    }

    /**
     * When clicked starts a new game by creating a new player profile first.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickNewGame(View v) {
        EscapeSoundManager.getInstance(this).playSound(EscapeSoundManager.getInstance(this).snd_button);

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
        EscapeSoundManager.getInstance(this).toggleMute(R.raw.techno02);
        if (EscapeSoundManager.getInstance(this).isMuted()) {
            findViewById(R.id.btn_mute).setBackground(getResources().getDrawable(R.drawable.icon_mute));
        } else {
            findViewById(R.id.btn_mute).setBackground(getResources().getDrawable(R.drawable.icon_sound));
        }
    }

    /**
     * When clicked takes you to current players overview and allows you to start the game from there
     * @param v the view as used by this method
     * @since 0.2
     */
    public void onClickContinue(View v) {
        EscapeSoundManager.getInstance(this).playSound(EscapeSoundManager.getInstance(this).snd_button);
        Intent intent = new Intent(this, SubContinueActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void onClickHighscores(View v) {
        EscapeSoundManager.getInstance(this).playSound(EscapeSoundManager.getInstance(this).snd_button);
        Intent intent = new Intent(this, HighscoreActivity.class);
        startActivity(intent);
    }

    /**
     * Closes the software.
     * @param v the view as used by this method
     * @since 0.1
     */
    public void onClickQuit(View v) {
        EscapeSoundManager.getInstance(this).playSound(EscapeSoundManager.getInstance(this).snd_button);
        finishAffinity();
    }

    private void onUserLoaded(User user) {
        if (user == null)
            return;

        this.user = user;
        findViewById(R.id.btn_continue).setEnabled(true);
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