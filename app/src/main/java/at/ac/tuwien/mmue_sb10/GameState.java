package at.ac.tuwien.mmue_sb10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class GameState {

    private static final String TAG = GameState.class.getSimpleName();

    /*
     * PLAYER STATE: POSITION, VELOCITY, ACCELERATION, ... in pixels
     */
    private float player_pos_x;
    private float player_pos_y;
    private float player_velocity_x;
    private float player_boost_x;
    private float player_velocity_y;
    private float player_acceleration_y;
    private byte gravity; //gravity top or bottom?
    private boolean player_inAir; //player is in air?
    private boolean player_onBoost; //player touches booster?
    private boolean player_first_gravity_inAir; //can do 1x gravity change while in air
    private boolean player_dead; //player died?

    /*
     * CURRENT STAGE
     */
    private Stage stage; //current stage
    private boolean finished; //stage is finished
    private boolean started; //stage is started

    /*
     * MISC
     */
    private Context context; //context of the app
    private float density; //density of the smartphone screen
    private float screenWidth; //screen width of the smartphone in px
    private float screenHeight; //screen heigth of the smartphone in px
    private float start_circle_radius; //interpolates between 0 and 1
    private Bitmap start_circle_bmp; //bitmap for the expanding circle at the start
    private Canvas start_circle_canvas; //canvas to draw on start_circle_bmp

    /*
     * PAINT
     */
    private Paint player_paint; //TODO: Remove when player sprite is implemented
    private Paint text_paint; //paint for text
    private Paint text_border_paint; //border paint for text
    private Paint trans_paint; //paint for transparency

    /*
     * STRINGS
     */
    private String you_died_retry; //message to display when player died

    /*
     * COLLISION
     */
    private RectF player_collision_px; //contains player corner coordinates in px after next step
    private Rect player_collision_tiles; //contains player corner coordinates in tiles after next step
    private int[] collision_corners; //0=TopLeft, 1=TopRight, 2=BottomRight, 3=BottomLeft
    private float col_time_x; //collision time on x-axis
    private float col_time_y; //collision time on y-axis

    /**
     * Creates a new GameState instance
     * @param context Context of the App to get resources
     * @param density Pixel density of the screen
     * @param screenWidth Width of the screen in pixel
     * @param screenHeight Heigth of the screen in pixel
     */
    public GameState(Context context, float density, float screenWidth, float screenHeight) {
        this.context = context;
        this.stage = new Stage(context, density);
        this.density = density;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.player_collision_px = new RectF();
        this.player_collision_tiles = new Rect();
        this.collision_corners = new int[4];

        this.player_paint = new Paint();
        this.player_paint.setColor(Color.RED);
        this.player_paint.setStyle(Paint.Style.FILL_AND_STROKE);

        this.text_paint = new Paint();
        this.text_paint.setColor(Color.RED);
        this.text_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.text_paint.setTypeface(Typeface.DEFAULT_BOLD);
        this.text_paint.setTextAlign(Paint.Align.CENTER);
        this.text_paint.setTextSize(24 * this.density);
        this.text_border_paint = new Paint();
        this.text_border_paint.setColor(Color.WHITE);
        this.text_border_paint.setTextAlign(Paint.Align.CENTER);
        this.text_border_paint.setTextSize(24 * this.density);
        this.text_border_paint.setTypeface(Typeface.DEFAULT_BOLD);
        this.text_border_paint.setStyle(Paint.Style.STROKE);
        this.text_border_paint.setStrokeWidth(2 * this.density);

        this.trans_paint = new Paint();
        this.trans_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        this.you_died_retry = context.getResources().getString(R.string.player_died);

        this.start_circle_bmp = Bitmap.createBitmap((int)this.screenWidth, (int)this.screenHeight, Bitmap.Config.ARGB_8888);
        this.start_circle_canvas = new Canvas(this.start_circle_bmp);
    }

    /**
     * Updates the state of the game depending on the deltaFrameTime. Handles collision detection, gravity, movement, ...
     * @param deltaFrameTime The passed time since the last updated frame.
     */
    public void update(long deltaFrameTime) {
        if (this.player_dead || this.finished || !this.started) {
            //Game over. Proceed to next stage or retry
            return;
        } else if(this.start_circle_radius < 1) {
            //Black circle at start of level is expanding. After 1 second the screen is fully visible
            this.start_circle_radius += (float)deltaFrameTime / 1000;
            return;
        }

        this.player_velocity_y += ((float) deltaFrameTime / 1000) * this.player_acceleration_y * this.gravity;

        //Player position after this deltatime-step
        this.player_collision_px.set(this.player_pos_x + this.player_velocity_x * this.player_boost_x * ((float) deltaFrameTime / 1000), this.player_pos_y + this.player_velocity_y, this.player_pos_x + this.player_velocity_x * this.player_boost_x * ((float) deltaFrameTime / 1000) + 24, this.player_pos_y + this.player_velocity_y + 24);
        this.player_collision_tiles.set((int) (this.player_collision_px.left / 24), (int) (this.player_collision_px.top / 24), (int) (this.player_collision_px.right / 24), (int) (this.player_collision_px.bottom / 24));

        if (this.player_collision_tiles.left >= 0 && this.player_collision_tiles.top >= 0 && this.player_collision_tiles.right < this.stage.stage_collision.length && this.player_collision_tiles.bottom <= this.stage.stage_collision[0].length) {
            //Player is inside bounds => CHECK COLLISION!
            this.collision_corners[0] = this.stage.stage_collision[this.player_collision_tiles.left][this.player_collision_tiles.top]; //TopLeft
            this.collision_corners[1] = this.stage.stage_collision[this.player_collision_tiles.right][this.player_collision_tiles.top]; //TopRight
            this.collision_corners[2] = this.stage.stage_collision[this.player_collision_tiles.right][this.player_collision_tiles.bottom]; //BottomRight
            this.collision_corners[3] = this.stage.stage_collision[this.player_collision_tiles.left][this.player_collision_tiles.bottom]; //BottomLeft
            if (this.collision_corners[0] != 0 || this.collision_corners[1] != 0 || this.collision_corners[2] != 0 || this.collision_corners[3] != 0) {
                //At least one of the player corners collides with a tile with behavior (solid, die, ...)
                if ((this.collision_corners[0] == 1 && this.collision_corners[1] == 1) || (this.collision_corners[2] == 1 && this.collision_corners[3] == 1)) {
                    //Y Solid Collision => Position adjustment
                    adjustPositionY();
                    //X Collision can still happen
                    checkCollisionX();
                    this.player_onBoost = false;
                } else if ((this.collision_corners[0] == 4 && this.collision_corners[1] == 4) || (this.collision_corners[2] == 4 && this.collision_corners[3] == 4)) {
                    adjustPositionY();
                    checkCollisionX();
                    boostPlayerRight();
                } else if ((this.collision_corners[0] == 5 && this.collision_corners[1] == 5) || (this.collision_corners[2] == 5 && this.collision_corners[3] == 5)) {
                    adjustPositionY();
                    checkCollisionX();
                    boostPlayerLeft();
                } else if ((collision_corners[0] != 0 && collision_corners[3] != 0) || (collision_corners[1] != 0 && collision_corners[2] != 0)) {
                    //X Collision
                    checkCollisionX();
                } else {
                    //Only one corner collided, can be either X or Y first
                    calcCollisionTimeX();
                    calcCollisionTimeY();

                    if (this.col_time_y < 0 && this.col_time_x > 0) {
                        //no valid collision on Y, collision on X
                        this.player_dead = true;
                    } else {
                        //Y before X => Y Solid Collosion => Position adjustment
                        adjustPositionY();
                        if (collision_corners[0] == 4 || collision_corners[3] == 4 || collision_corners[1] == 4 || collision_corners[2] == 4) {
                            boostPlayerRight();
                        } else if (collision_corners[0] == 5 || collision_corners[3] == 5 || collision_corners[1] == 5 || collision_corners[2] == 5) {
                            boostPlayerLeft();
                        } else {
                            this.player_onBoost = false;
                        }
                    }
                }

                if (collision_corners[0] == 3 || collision_corners[1] == 3 || collision_corners[2] == 3 || collision_corners[3] == 3) {
                    //X Inverter Collision
                    this.player_velocity_x *= -1;
                } else if (collision_corners[0] == 6 || collision_corners[1] == 6 || collision_corners[2] == 6 || collision_corners[3] == 6) {
                    //X Finish Collision
                    this.finished = true;
                } else if (collision_corners[0] == 2 || collision_corners[1] == 2 || collision_corners[2] == 2 || collision_corners[3] == 2) {
                    //X Death Collision (spikes)
                    this.player_dead = true;
                }

                this.player_velocity_y = 0;
                this.player_inAir = false;
                this.player_first_gravity_inAir = false;
            } else {
                //None of the player corners collides with anything
                this.player_pos_y = this.player_collision_px.top;
                this.player_inAir = true;
                this.player_onBoost = false;
            }
            this.player_pos_x = this.player_collision_px.left;
        } else {
            //Player is out of bounds => DIE!
            this.player_dead = true;
        }
    }

    /**
     * Boosts the player speed by a factor of 1.5 if going right, otherwise slows down by factor of 0.66
     * Only works once per boost platform
     */
    private void boostPlayerRight() {
        if (this.player_velocity_x > 0 && !this.player_onBoost) {
            this.player_boost_x *= 1.5; //TODO
            this.player_onBoost = true;
        } else if (this.player_velocity_x < 0 && !this.player_onBoost) {
            this.player_boost_x *= (2f / 3);
            this.player_onBoost = true;
        }
    }

    /**
     * Boosts the player speed by a factor of 1.5 if going left, otherwise slows down by factor of 0.66
     * Only works once per boost platform
     */
    private void boostPlayerLeft() {
        if (this.player_velocity_x < 0 && !this.player_onBoost) {
            this.player_boost_x *= 1.5; //TODO
            this.player_onBoost = true;
        } else if (this.player_velocity_x > 0 && !this.player_onBoost) {
            this.player_boost_x *= (2f / 3);
            this.player_onBoost = true;
        }
    }

    /**
     * When player object collides with tiles on Y axis (basically when it is walking on the ground), adjust Y position to be exactly
     */
    private void adjustPositionY() {
        if (this.player_velocity_y > 0)
            this.player_pos_y = this.player_collision_px.top - this.player_collision_px.top % 24;
        else
            this.player_pos_y = this.player_collision_px.top + (24 - this.player_collision_px.top % 24);
    }

    /**
     * When player object collides with a wall horizontally, player dies
     */
    private void checkCollisionX() {
        if ((this.collision_corners[0] == 1 && this.collision_corners[3] == 1) || (this.collision_corners[1] == 1 && this.collision_corners[2] == 1)) {
            this.player_dead = true;
        }
    }

    /**
     * Calculates the exact time it took the player object to collide with the tile object on x axis
     * Player object might overlap the collided object, this calculates exact time it takes to collide without overlap
     */
    private void calcCollisionTimeX() {
        if(this.player_velocity_x < 0)
            this.col_time_x = (this.player_collision_tiles.right * 24 - this.player_pos_x) / (this.player_velocity_x * this.player_boost_x);
        else
            this.col_time_x = (this.player_collision_tiles.left * 24 - this.player_pos_x) / (this.player_velocity_x * this.player_boost_x);
    }

    /**
     * Calculates the exact time it took the player object to collide with the tile object on y axis
     * Player object might overlap the collided object, this calculates exact time it takes to collide without overlap
     */
    private void calcCollisionTimeY() {
        if(this.player_velocity_y < 0)
            this.col_time_y = (this.player_collision_tiles.bottom * 24 - this.player_pos_y) / this.player_velocity_y;
        else
            this.col_time_y = (this.player_collision_tiles.top * 24 - this.player_pos_y) / this.player_velocity_y;
    }

    private float trans_x = 0;
    private float trans_y = 0;

    /**
     * Draws the current state of the game onto c
     * @param c The Canvas that is drawed onto
     */
    public void draw(Canvas c) {
        if (this.player_velocity_x > 0) {
            this.trans_x = this.player_pos_x * this.stage.stage_scale - this.stage.tile_size_scaled * 4;
        } else {
            this.trans_x = this.player_pos_x * this.stage.stage_scale - (c.getWidth() - this.stage.tile_size_scaled * 5);
        }
        if (this.trans_x < 0) this.trans_x = 0;
        else if (this.trans_x > this.stage.stage_foreground.getWidth() - c.getWidth()) this.trans_x = this.stage.stage_foreground.getWidth() - c.getWidth();

        if (this.player_pos_y * this.stage.stage_scale - this.trans_y > this.trans_y + c.getHeight() - this.stage.tile_size_scaled * 2)
            this.trans_y += (this.player_pos_y * this.stage.stage_scale - this.trans_y) - (this.trans_y + c.getHeight() - this.stage.tile_size_scaled * 2);
        else if (this.player_pos_y * this.stage.stage_scale - this.trans_y < this.trans_y + this.stage.tile_size_scaled)
            this.trans_y += (this.player_pos_y * this.stage.stage_scale - this.trans_y) - (this.trans_y + this.stage.tile_size_scaled);

        c.drawBitmap(this.stage.stage_foreground, -this.trans_x, -this.trans_y, null);
        c.drawRect(
                this.player_pos_x * this.stage.stage_scale - this.trans_x,
                this.player_pos_y * this.stage.stage_scale - this.trans_y,
                (this.player_pos_x + 24) * this.stage.stage_scale - this.trans_x,
                (this.player_pos_y + 24) * this.stage.stage_scale - this.trans_y,
                player_paint);

        if(this.player_dead) {
            //Player is dead. Draw retry message
            c.drawText(this.you_died_retry, this.screenWidth / 2, this.screenHeight / 2, this.text_border_paint);
            c.drawText(this.you_died_retry, this.screenWidth / 2, this.screenHeight / 2, this.text_paint);
        } else if (this.finished) {
            c.drawText(this.stage.stage_name + " finished!\nTap to continue.", this.screenWidth / 2, this.screenHeight / 2, this.text_border_paint); //TODO: Replace string
            c.drawText(this.stage.stage_name + " finished!\nTap to continue.", this.screenWidth / 2, this.screenHeight / 2, this.text_paint); //TODO: Replace string
        } else if(this.start_circle_radius < 1) {
            //Stage has started. Draw expanding circle first second
            this.start_circle_canvas.drawCircle((this.player_pos_x + 24) * this.stage.stage_scale, (this.player_pos_y + 24) * this.stage.stage_scale, this.start_circle_radius * this.screenWidth, trans_paint);
            c.drawBitmap(start_circle_bmp, 0, 0, null);
        }
    }

    /**
     * Inverts the gravity of the game to face upside down. Also marks the player to be in air
     * Only works if player is not in air when method call happens
     */
    private void invertGravity() {
        if (!this.player_inAir || !this.player_first_gravity_inAir) {
            this.gravity *= -1;
            this.player_inAir = true;
            this.player_first_gravity_inAir = true;
        }
    }

    /**
     * Sets the vertical velocity of the player to make a small jump. Also marks the player to be in air
     * Only works if player is not in air when method call happens
     */
    private void jump() {
        if (!this.player_inAir) {
            this.player_velocity_y -= 5 * gravity; //TODO: Change to proper value
            this.player_inAir = true;
        }
    }

    /**
     * Manipulates the state of the game depending on incoming MotionEvents
     * @param event Incoming MotionEvent
     */
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(this.player_dead) {
                this.retry();
            } else if (this.finished) {
                this.load(this.stage.stage_level + 1);
            } else if (!this.started) {
                this.started = true;
            } else {
                if (event.getX() < this.screenWidth / 2) {
                    this.invertGravity();
                } else {
                    this.jump();
                }
            }
        } else {
            //TODO: Other Events?
        }
    }

    /**
     * Loads the GameState and the a stage for the first use only
     * @param level The ID of the stage to be loaded. Starts with 0
     */
    public void load(int level) {
        this.started = false;

        this.stage.load(level);

        this.player_pos_x = stage.player_start_x * 24;
        this.player_pos_y = stage.player_start_y * 24;
        this.player_velocity_x = stage.player_velocity_x;
        this.player_boost_x = 1.0f;
        this.player_velocity_y = 0;
        this.player_acceleration_y = 10;
        this.player_dead = false;
        this.player_inAir = true;
        this.player_onBoost = false;
        this.player_first_gravity_inAir = false;
        this.gravity = 1;

        this.start_circle_radius = 0;
        this.start_circle_canvas.drawColor(Color.BLACK);
        this.start_circle_canvas.drawText(this.stage.stage_name, this.screenWidth / 2, this.screenHeight / 2, this.text_border_paint);
        this.start_circle_canvas.drawText(this.stage.stage_name, this.screenWidth / 2, this.screenHeight / 2, this.text_paint);

        this.finished = false;
    }

    /**
     * Resets all changed values since the start of the level. Restarts the stage
     */
    private void retry() {
        this.player_pos_x = stage.player_start_x * 24;
        this.player_pos_y = stage.player_start_y * 24;
        this.player_velocity_x = stage.player_velocity_x;
        this.player_boost_x = 1.0f;
        this.player_velocity_y = 0;
        this.player_acceleration_y = 10;
        this.player_dead = false;
        this.player_inAir = true;
        this.player_onBoost = false;
        this.player_first_gravity_inAir = false;
        this.gravity = 1;

        this.finished = false;
    }
}
