package at.ac.tuwien.mmue_sb10;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

public class CustomVideoView extends VideoView implements MediaPlayer.OnPreparedListener {

    private MediaPlayer mediaPlayer;
    private boolean muted;

    //todo
    /**
     *
     * @param context
     * @since 1.0
     * @author Lukas Lidauer
     */
    public CustomVideoView(Context context) {
        super(context);
        this.setOnPreparedListener(this);
        muted = false;
    }

    //todo
    /**
     *
     * @param context
     * @param attrs
     * @since 1.0
     * @author Lukas Lidauer
     */
    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnPreparedListener(this);
        muted = false;
    }

    //todo
    /**
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @since 1.0
     * @author Lukas Lidauer
     */
    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnPreparedListener(this);
        muted = false;
    }

    //todo
    /**
     *
     * @param mp
     * @since 1.0
     * @author Lukas Lidauer
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        this.mediaPlayer = mp;
        if(muted)
            this.mediaPlayer.setVolume(0, 0);
    }

    /**
     * Mutes the custom video view
     * @since 1.0
     * @author Lukas Lidauer
     */
    public void mute() {
        try {
            mediaPlayer.setVolume(0, 0);
        } catch (Exception exc) {
            muted = true;
        }
    }

    /**
     * unmutes the custom Video view
     * @since 1.0
     * @author Lukas Lidauer
     */
    public void unmute() {
        try {
            mediaPlayer.setVolume(1, 1);
        } catch (Exception exc) {
            muted = false;
        }
    }
}
