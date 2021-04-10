package at.ac.tuwien.mmue_sb10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class GameState {

    private static final String TAG = GameState.class.getSimpleName();

    /*
    * PLAYER STATE: POSITION, VELOCITY, ACCELERATION, ...
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

    public GameState(Context context) {
        this.context = context;
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void update(long deltaFrameTime) {
        player_pos_x += player_velocity_x * ((float)deltaFrameTime / 1000);
        player_velocity_y += ((float)deltaFrameTime / 1000) * player_acceleration_y * gravity;
        player_pos_y += player_velocity_y;

        if(finished && finished_continue)
            load(stage.level + 1);
    }

    public void draw(Canvas c) {
        c.drawColor(Color.WHITE);
        c.drawBitmap(stage.terrain, 0, 0, null);
        c.drawRect(player_pos_x, player_pos_y, player_pos_x+100, player_pos_y+100, paint);
    }

    public void invertGravity() {
        player_acceleration_y *= -1;
    }

    public void jump() {
        if(!inAir) {
            player_velocity_y += 500 * gravity; //TODO: Change to proper value
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
