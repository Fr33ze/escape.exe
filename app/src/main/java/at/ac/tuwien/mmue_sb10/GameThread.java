package at.ac.tuwien.mmue_sb10;

import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

    private static final String TAG = GameThread.class.getSimpleName();

    private GameState state;
    private SurfaceHolder holder;
    private float frametime;

    private Canvas canvas;
    private boolean running;

    public GameThread(GameState state, SurfaceHolder holder, float fps) {
        this.state = state;
        this.holder = holder;
        this.frametime = 1000 / fps;
    }

    public void setRunning(boolean active) {
        this.running = active;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        long currentFrameTime, deltaFrameTime, lastFrameTime = System.currentTimeMillis();
        while (running) {
            currentFrameTime = System.currentTimeMillis();
            deltaFrameTime = currentFrameTime - lastFrameTime;

            state.update(deltaFrameTime);

            try {
                canvas = holder.lockCanvas();
                synchronized (holder) {
                    state.draw(canvas);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            lastFrameTime = currentFrameTime;

            //SLEEP AND FRAME SKIP
            int sleepTime = (int) (frametime - deltaFrameTime);
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
