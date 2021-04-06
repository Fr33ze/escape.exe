package at.ac.tuwien.mmue_sb10;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

    private static final String TAG = GameThread.class.getSimpleName();

    private GameState state;
    private SurfaceHolder holder;
    private GameView view;

    private Canvas canvas;
    private boolean running;

    public GameThread(GameState state, GameView view) {
        this.state = state;
        this.view = view;
        this.holder = view.getHolder();

        this.running = true;
    }

    @Override
    public void run() {
        long currentFrameTime, deltaFrameTime, lastFrameTime = System.currentTimeMillis();
        while (running) {
            currentFrameTime = System.currentTimeMillis();
            deltaFrameTime = currentFrameTime - lastFrameTime;

            state.update(deltaFrameTime);

            synchronized (holder) {
                try {
                    canvas = holder.lockCanvas();
                } finally {
                    if (canvas != null) {
                        state.draw(canvas);
                        holder.unlockCanvasAndPost(canvas);
                        view.postInvalidate();
                    }
                }
            }

            lastFrameTime = currentFrameTime;

            //CALCULATE SLEEP HERE
        }
    }
}
