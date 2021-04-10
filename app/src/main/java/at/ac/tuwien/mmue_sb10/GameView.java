package at.ac.tuwien.mmue_sb10;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = GameView.class.getSimpleName();

    private GameState state;
    private GameThread thread;
    private float fps;

    public GameView(Context context) {
        super(context);
        fps = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRefreshRate();
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fps = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRefreshRate();
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        fps = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRefreshRate();
        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        state = new GameState(getContext());
        state.load(0);
        thread = new GameThread(state, holder, fps);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!thread.isRunning()) {
                startgame();
            } else {
                state.invertGravity();
            }
        } else {

        }
        return super.onTouchEvent(event);
    }

    public void startgame() {
        if(thread == null) {
            state = new GameState(getContext()); //TODO: Load from saved instance?
            thread = new GameThread(state, getHolder(), fps);
        } else {
            thread.setRunning(true);
            thread.start();
        }
    }

    public void endgame() {
        thread.setRunning(false);
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
