package at.ac.tuwien.mmue_sb10;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomVideoView extends VideoView implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private boolean muted;

    public CustomVideoView(Context context) {
        super(context);
        this.setOnPreparedListener(this);
        muted = false;
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnPreparedListener(this);
        muted = false;
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnPreparedListener(this);
        muted = false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        this.mediaPlayer = mp;
        if(muted)
            this.mediaPlayer.setVolume(0, 0);
    }

    /**
     * Mutes the CustomVideoView
     */
    public void mute() {
        try {
            mediaPlayer.setVolume(0, 0);
        } catch (Exception exc) {
            muted = true;
        }
    }

    /**
     * Unmutes the CustomVideoView
     */
    public void unmute() {
        try {
            mediaPlayer.setVolume(1, 1);
        } catch (Exception exc) {
            muted = false;
        }
    }
}
