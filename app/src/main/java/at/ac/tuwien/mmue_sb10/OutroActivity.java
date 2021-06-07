package at.ac.tuwien.mmue_sb10;

import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class OutroActivity extends Activity {

    CustomVideoView videoView;
    TextView skipView;
    Button muteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outro);
    }

    @Override
    protected void onResume() {
        super.onResume();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        skipView = findViewById(R.id.txt_skip_outro);
        muteBtn = findViewById(R.id.btn_mute_outro);
        videoView = findViewById(R.id.outroView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.outro)); //TODO
        videoView.setOnCompletionListener(mp -> finish());
        videoView.start();

        EscapeSoundManager.getInstance(this).releaseMediaPlayer();

        if (EscapeSoundManager.getInstance(this).isMuted()) {
            videoView.mute();
            muteBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_mute, null));
        } else {
            videoView.unmute();
            muteBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_sound, null));
        }
    }

    public void onClickVideo(View view) {
        if (skipView.getVisibility() == View.VISIBLE) {
            skipView.setVisibility(View.GONE);
            muteBtn.setVisibility(View.GONE);
        } else {
            skipView.setVisibility(View.VISIBLE);
            muteBtn.setVisibility(View.VISIBLE);
        }
    }

    public void onClickMute(View view) {
        EscapeSoundManager.getInstance(this).toggleMute();
        if (EscapeSoundManager.getInstance(this).isMuted()) {
            videoView.mute();
            muteBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_mute, null));
        } else {
            videoView.unmute();
            muteBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_sound, null));
        }
    }

    public void onClickSkip(View view) {
        //EscapeSoundManager.getInstance(this).playSound(EscapeSoundManager.getInstance(this).snd_button);
        videoView.stopPlayback();
        videoView.suspend();
        finish();
    }
}