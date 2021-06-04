package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.core.content.res.ResourcesCompat;

public class IntroActivity extends Activity {

    CustomVideoView videoView;
    TextView skipView;
    Button muteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
    }

    @Override
    protected void onResume() {
        super.onResume();
        skipView = findViewById(R.id.txt_skip);
        muteBtn = findViewById(R.id.btn_mute_intro);
        videoView = findViewById(R.id.videoView);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro));
        videoView.setOnCompletionListener(mp -> startActivity(new Intent(IntroActivity.this, MainActivity.class)));
        videoView.start();

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
        startActivity(new Intent(this, MainActivity.class));
    }
}