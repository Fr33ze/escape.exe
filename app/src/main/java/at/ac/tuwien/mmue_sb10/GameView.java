/**
 * The class GameView handles the visual representation of the applications state.
 * Therefore GameState and GameThread must be delivered to this class.
 * This class takes into account the screen size, FPS and density of the device it's running on.
 * @author Lukas Lidauer
 */

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

import at.ac.tuwien.mmue_sb10.persistence.User;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = GameView.class.getSimpleName();

    private GameState state;
    private GameThread thread;
    /**
     * The FPS of the device this program is running on
     */
    private float fps;
    /**
     * The pixel density of the device this program is running on
     */
    private float density;
    /**
     * The screen width of the device this program is running on
     */
    private int screenWidth;
    /**
     * The screen height of the device this program is running on
     */
    private int screenHeigth;
    /**
     * Indicates if the game is ready to be started
     */
    private boolean ready;
    /**
     * User of the gamestate
     */
    private User user;

    /**
     *
     * @param context
     * @since 0.1
     */
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


    /**
     *
     * @param context
     * @param attrs
     * @since 0.1
     */
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

    /**
     *
     * @param context
     * @param attrs
     * @param defStyle
     * @since 0.1
     */
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
        this.thread = new GameThread(state, holder, getContext());
        if(this.ready)
            startgame();
        else
            this.ready = true;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        endgame();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            this.state.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    /**
     * Sets the user of the gamestate. Starts the game if surface has been created already
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
        if(this.ready)
            startgame();
        else
            this.ready = true;
    }

    /**
     * Starts the game and while doing so sets density, FPS and screen ration.
     * This method also handles which level will be loaded but currently is static since no database is yet implemented
     * After loading a level it sets the main thread to running.
     * @since 0.1
     */
    public void startgame() {
        this.state.setUser(user);
        this.state.load(user.currentLevel);
        this.thread.setRunning(true);
        this.thread.start();
    }

    /**
     * Pauses the game - method needs to be implemented in the future, currently not working
     * @since 0.1
     */
    public void onBackPressed() {
        if(this.thread != null) {
            this.state.onBackPressed();
        }
    }

    /**
     * Declares the game not running anymore (by setting the corresponding thread to not running)
     * @since 0.1
     */
    public void endgame() {
        this.thread.setRunning(false);
        this.thread = null;
    }
}
