package at.ac.tuwien.mmue_sb10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class GameState {

    private static final String TAG = GameState.class.getSimpleName();

    /*
    * PLAYER STATE: POSITION, VELOCITY, ACCELERATION, ... in pixels
    */
    float player_pos_x;
    float player_pos_y;
    float player_velocity_x = 200;
    float player_velocity_y = 0;
    float player_acceleration_y = 20;
    byte gravity = 1;
    boolean inAir;

    /*
    * CURRENT STAGE
    */
    Stage stage;
    boolean finished; //Stage is finished
    boolean finished_continue; //Player presses continue after stage is finished

    /*
     * TEMPORARY
     */
    Paint paint;
    Context context;
    private float density;

    public GameState(Context context, float density) {
        this.context = context;
        this.density = density;
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void update(long deltaFrameTime) {
        this.player_velocity_y += ((float)deltaFrameTime / 1000) * this.player_acceleration_y * this.gravity;

        //TODO: Get collision and check

        this.player_pos_x += this.player_velocity_x * ((float) deltaFrameTime / 1000);
        this.player_pos_y += this.player_velocity_y;

        if(this.finished && this.finished_continue)
            load(this.stage.level + 1);
    }

    public void draw(Canvas c) {
        c.drawColor(Color.WHITE);
        c.drawBitmap(this.stage.terrain, null, new RectF(0, 0, this.stage.terrain.getWidth() * this.density, this.stage.terrain.getHeight() * this.density), null);
        c.drawRect(this.player_pos_x * this.density, this.player_pos_y * this.density, (this.player_pos_x + 24) * this.density, (this.player_pos_y + 24) * this.density, paint);
    }

    public void invertGravity() {
        if(!inAir) {
            player_acceleration_y *= -1;
            inAir = true;
        }
    }

    public void jump() {
        if(!inAir) {
            player_velocity_y += 200 * gravity; //TODO: Change to proper value
            inAir = true;
        }
    }

    public void load(int level) {
        stage = new Stage(context);
        stage.load(level);
        player_pos_x = stage.player_start_x;
        player_pos_y = stage.player_start_y;
        player_velocity_x = 200; //TODO: Change to default value
        player_velocity_y = 0; //TODO: Change to default value
        finished = false;
        finished_continue = false;
    }
}
