package at.ac.tuwien.mmue_sb10;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class GameState {

    private static final String TAG = GameState.class.getSimpleName();

    /*
    * TESTING VALUES FOR UPDATE AND DRAW
    * */
    float x = 0;
    float y = 100;
    float vel = 10;
    Paint paint;

    public GameState() {
        paint = new Paint();
        paint.setColor(Color.parseColor("green"));
    }

    public void update(long deltaFrameTime) {
        x += vel * ((float)deltaFrameTime / 1000);
    }

    public void draw(Canvas c) {
        Log.d(TAG, "x=" + x + ", y=" + y);
        c.drawColor(Color.parseColor("white"));
        c.drawRect(x, y, x+200, y+200, paint);
    }
}
