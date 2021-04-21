package at.ac.tuwien.mmue_sb10;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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
    private float density;
    private int screenWidth;
    private int screenHeigth;

    public GameView(Context context) {
        super(context);
        Display dsp = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.fps = dsp.getRefreshRate();
        DisplayMetrics dm = new DisplayMetrics();
        dsp.getMetrics(dm);
        this.density = dm.density;
        this.screenWidth = dm.widthPixels;
        this.screenHeigth = dm.heightPixels;
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Display dsp = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.fps = dsp.getRefreshRate();
        DisplayMetrics dm = new DisplayMetrics();
        dsp.getMetrics(dm);
        this.density = dm.density;
        this.screenWidth = dm.widthPixels;
        this.screenHeigth = dm.heightPixels;
        getHolder().addCallback(this);
        setFocusable(true);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Display dsp = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        this.fps = dsp.getRefreshRate();
        DisplayMetrics dm = new DisplayMetrics();
        dsp.getMetrics(dm);
        this.density = dm.density;
        this.screenWidth = dm.widthPixels;
        this.screenHeigth = dm.heightPixels;
        getHolder().addCallback(this);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        this.state = new GameState(getContext(), this.density, this.screenWidth, this.screenHeigth);
        this.state.load(0);
        this.thread = new GameThread(state, holder, fps);
        startgame(); //TODO
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
            if(!this.thread.isRunning()) {
                startgame();
            } else {
                this.state.onTouchEvent(event);
            }
        } else {
            //TODO: ???
        }
        return super.onTouchEvent(event);
    }

    public void startgame() {
        if(this.thread == null) {
            this.state = new GameState(getContext(), this.density, this.screenWidth, this.screenHeigth); //TODO: Load from saved instance?
            this.state.load(0); //TODO
            this.thread = new GameThread(state, getHolder(), fps);
        } else {
            this.thread.setRunning(true);
            this.thread.start();
        }
    }

    public void pausegame() {
        if(this.thread != null) {
            //this.state. //TODO
        }
    }

    public void endgame() {
        this.thread.setRunning(false);
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
