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
    float x_velocity = 200;
    float y_velocity = 0;
    float y_acceleration = 20;
    Paint paint;

    public GameState() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void invertGravity() {
        y_acceleration *= -1;
    }

    public void update(long deltaFrameTime) {
        x += x_velocity * ((float)deltaFrameTime / 1000);
        y_velocity += ((float)deltaFrameTime / 1000) * y_acceleration;
        y += y_velocity;
    }

    public void draw(Canvas c) {
        c.drawColor(Color.WHITE);
        c.drawRect(x, y, x+100, y+100, paint);
    }
}
