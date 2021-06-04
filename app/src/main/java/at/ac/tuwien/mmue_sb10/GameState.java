/**
 * Handles the current state of the application.
 * This class saves information regarding the position of the player on a 2D grid, player velocity, player acceleration, gravity (up or down), is player dead and more.
 *
 * @author Lukas Lidauer & Jan König
 */
package at.ac.tuwien.mmue_sb10;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.MotionEvent;


import androidx.core.content.res.ResourcesCompat;

import at.ac.tuwien.mmue_sb10.persistence.EscapeDatabase;
import at.ac.tuwien.mmue_sb10.persistence.Highscore;
import at.ac.tuwien.mmue_sb10.persistence.User;
import at.ac.tuwien.mmue_sb10.util.Concurrency;

public class GameState {
    private static final int PLAYER_WIDTH = 18; //player width in pixels
    private static final int PLAYER_HEIGTH = 24; //player heigth in pixel (24 is maximum because of collision)
    private static final float FRAME_TIME = 83f; //player animation. 83f is default for 12fps

    /*
     * PLAYER: POSITION, VELOCITY, ACCELERATION
     */
    private float player_pos_x;
    private float player_pos_y;
    private float player_velocity_x;
    private float player_boost_x;
    private float player_velocity_y;
    private float player_acceleration_y;

    /*
     * PLAYER: STATE
     */
    private byte gravity; //gravity can either be regular or inverted (or top or bottom)
    private boolean player_inAir; //player is in air?
    private boolean player_onBoost; //player touches booster?
    private boolean player_first_gravity_inAir; //player is allowed to do only one gravity change in the air until he hits the ground again. This variable keeps track of that.
    private boolean player_dead; //player died
    private boolean player_no_input; //game doesnt accept input for player until stage is finished. starts screen fade out
    private float current_fade_out_time; //current timer to fade out

    private PlayerState player_state; //current state of the player. used for animations
    private PlayerState player_last_state; //last state of player. used for animations
    private Bitmap[] player_frames; //all frames of the player animations
    private float player_anim_time; //time counter used for animations
    private int player_current_frame; //current frame of the player to be drawn
    private Matrix player_draw_matrix; //transformation of player
    private float player_draw_scale; //factor to scale the player bitmap

    /*
     * CURRENT STAGE
     */
    private Stage stage; //current stage
    private boolean finished; //stage is finished
    private boolean started; //stage is started
    public boolean running; //game is running

    /*
     * PAUSE MENU
     */
    public boolean paused; //game is paused
    private RectF continue_touch_zone; //rectangle of the continue button
    private RectF exit_touch_zone; //rectangle of the exit button
    private RectF mute_pause_touch_zone; //rectangle of the mute button

    /*
     * MISC
     */
    private Context context; //context of the app
    private User user; //current savefile
    private boolean update_user; //indicates wheter the user needs to be updated
    private float screenWidth; //screen width of the smartphone in px
    private float screenHeight; //screen heigth of the smartphone in px

    /*
     * DRAW
     */
    private float density; //density of the smartphone screen
    private float trans_x = 0; //draw-translation on x axis
    private float trans_y = 0; //draw-translation on y axis
    private float trans_x_unscaled = 0; //draw-translation on x axis unscaled
    private float trans_y_unscaled = 0; //draw-translation on y axis unscaled
    private Rect draw_src; //source rectangle for the region of the map to draw
    private Rect draw_tar; //target rectangle on the screen (full screen)
    private float start_circle_radius; //interpolates between 0 and 1
    private Bitmap start_circle_bmp; //bitmap for the expanding circle at the start
    private Canvas start_circle_canvas; //canvas to draw on start_circle_bmp
    private boolean player_invisible; //draw player or not
    private Bitmap death_counter_icon; //icon for the death counter
    private Bitmap icon_mute; //icon for the mute button
    private Bitmap icon_sound; //icon for the unmute button
    private Bitmap icon_pause; //icon for the pause button

    /*
     * PAINT
     */
    private Paint text_paint; //paint for text
    private Paint text_border_paint; //border paint for text
    private Paint trans_paint; //paint for transparency
    private Paint button_paint; //paint for buttons
    private Paint button_text_paint; //paint for text on buttons
    private Paint death_counter_paint; //paint for the death counter

    /*
     * STRINGS
     */
    private String you_died_retry; //message to display when player died
    private String finished_next_level; //message to display when level is finished

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
     *
     * @param context      Context of the App to get resources
     * @param density      Pixel density of the screen
     * @param screenWidth  Width of the screen in pixel
     * @param screenHeight Heigth of the screen in pixel
     * @since 0.1
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

        loadPlayerFrames();
        loadDeathCounter();

        Typeface font_joystix = ResourcesCompat.getFont(this.context, R.font.joystix_monospace);

        this.text_paint = new Paint();
        this.text_paint.setColor(Color.GREEN);
        this.text_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.text_paint.setTypeface(font_joystix);
        this.text_paint.setTextAlign(Paint.Align.CENTER);
        this.text_paint.setTextSize(24 * this.density);
        this.text_border_paint = new Paint();
        this.text_border_paint.setColor(Color.WHITE);
        this.text_border_paint.setTextAlign(Paint.Align.CENTER);
        this.text_border_paint.setTextSize(24 * this.density);
        this.text_border_paint.setTypeface(font_joystix);
        this.text_border_paint.setStyle(Paint.Style.STROKE);
        this.text_border_paint.setStrokeWidth(2 * this.density);

        this.trans_paint = new Paint();
        this.trans_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        this.button_paint = new Paint();
        this.button_paint.setAntiAlias(true);
        this.button_paint.setColor(Color.GRAY);

        this.button_text_paint = new Paint();
        this.button_text_paint.setColor(Color.BLACK);
        this.button_text_paint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.button_text_paint.setTypeface(font_joystix);
        this.button_text_paint.setTextAlign(Paint.Align.CENTER);
        this.button_text_paint.setTextSize(22 * this.density);

        this.death_counter_paint = new Paint();
        this.death_counter_paint.setColor(Color.BLACK);
        this.death_counter_paint.setTypeface(font_joystix);
        this.death_counter_paint.setTextSize(this.death_counter_icon.getHeight() * 0.65f);

        this.you_died_retry = context.getResources().getString(R.string.player_died);
        this.finished_next_level = context.getResources().getString(R.string.next_level);

        this.start_circle_bmp = Bitmap.createBitmap((int) this.screenWidth, (int) this.screenHeight, Bitmap.Config.ARGB_8888);
        this.start_circle_canvas = new Canvas(this.start_circle_bmp);

        this.draw_src = new Rect();
        this.draw_tar = new Rect();

        this.continue_touch_zone = new RectF(this.screenWidth * 0.33f, this.screenHeight / 2 - 10 * this.density, this.screenWidth * 0.66f, this.screenHeight / 2 + 50 * this.density);
        this.exit_touch_zone = new RectF(this.screenWidth * 0.33f, this.screenHeight / 2 + 70 * this.density, this.screenWidth * 0.66f, this.screenHeight / 2 + 130 * this.density);
        this.mute_pause_touch_zone = new RectF(10 * this.density, 10 * this.density, 40 * this.density, 40 * this.density);
        loadMuteIcons();

        this.player_state = PlayerState.IDLE;
        this.player_anim_time = 0;
        this.player_draw_matrix = new Matrix();
        this.player_draw_scale = (float) PLAYER_WIDTH / this.player_frames[0].getWidth();

        this.running = false;
        EscapeSoundManager.getInstance(this.context).unlock();
    }

    /**
     * Loads the player frames from the sprite sheet into the player_frames array
     */
    private void loadPlayerFrames() {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        Bitmap player_sheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.hero_sheet, o);
        int h = player_sheet.getWidth() / 13;
        int v = player_sheet.getHeight() / 17;
        this.player_frames = new Bitmap[h * v];
        int framenumber = 0;
        for (int y = 0; y < v; y++) {
            for (int x = 0; x < h; x++) {
                this.player_frames[framenumber] = Bitmap.createBitmap(player_sheet, x * 13, y * 17, 13, 17);
                framenumber++;
            }
        }
    }

    /**
     * Loads the death counter sprite from the resources
     */
    private void loadDeathCounter() {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        Bitmap temp_death = BitmapFactory.decodeResource(context.getResources(), R.drawable.life_counter, o);
        this.death_counter_icon = Bitmap.createScaledBitmap(
                temp_death, (int) (temp_death.getWidth() * 0.5f * this.density), (int) (temp_death.getHeight() * 0.5f * this.density), true
        );
    }

    /**
     * Loads the mute and unmute icons from the resources
     */
    private void loadMuteIcons() {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        Bitmap temp_mute = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_mute, o);
        this.icon_mute = Bitmap.createScaledBitmap(
                temp_mute, (int) (this.mute_pause_touch_zone.width()), (int) (mute_pause_touch_zone.height()), true
        );
        temp_mute = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_sound, o);
        this.icon_sound = Bitmap.createScaledBitmap(
                temp_mute, (int) (30 * this.density), (int) (30 * this.density), true
        );
    }

    private void loadPauseIcon() {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        Bitmap temp_pause = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_pause, o);
        this.death_counter_icon = Bitmap.createScaledBitmap(
                temp_pause, (int) (30 * this.density), (int) (30 * this.density), true
        );
    }

    /**
     * Updates the state of the game depending on the deltaFrameTime. Handles collision detection, gravity, movement, ...
     *
     * @param deltaFrameTime The passed time since the last updated frame.
     * @since 0.1
     */
    public void update(long deltaFrameTime) {
        if (this.player_dead || this.finished || !this.started) {
            //Game over. Proceed to next stage or retry
            return;
        } else if (this.start_circle_radius < 1) {
            //Black circle at start of level is expanding. After 1 second the screen is fully visible
            this.start_circle_radius += (float) deltaFrameTime / 1000;
            return;
        } else if (this.paused) {
            return;
        }

        this.player_velocity_y += ((float) deltaFrameTime / 1000) * this.player_acceleration_y * this.gravity;

        //Player position after this deltatime-step
        this.player_collision_px.set(this.player_pos_x + this.player_velocity_x * this.player_boost_x * ((float) deltaFrameTime / 1000), this.player_pos_y + this.player_velocity_y, this.player_pos_x + this.player_velocity_x * this.player_boost_x * ((float) deltaFrameTime / 1000) + PLAYER_WIDTH, this.player_pos_y + this.player_velocity_y + PLAYER_HEIGTH);
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
                        killPlayer();
                        this.player_pos_x = this.player_collision_px.left;
                        this.player_pos_y = this.player_collision_px.top;
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
                    this.player_pos_y = this.player_collision_px.top;
                } else if (collision_corners[0] == 6 || collision_corners[1] == 6 || collision_corners[2] == 6 || collision_corners[3] == 6) {
                    //X Finish Collision
                    finishStage();
                    this.player_pos_x = this.player_collision_px.left;
                    this.player_pos_y = this.player_collision_px.top;
                } else if (collision_corners[0] == 7 || collision_corners[1] == 7 || collision_corners[2] == 7 || collision_corners[3] == 7) {
                    //X Collision with no-input tile
                    //happens before finish line for running out of screen effect
                    setNoPlayerInput();
                    this.player_pos_x = this.player_collision_px.left;
                    this.player_pos_y = this.player_collision_px.top;
                } else if (collision_corners[0] == 2 || collision_corners[1] == 2 || collision_corners[2] == 2 || collision_corners[3] == 2) {
                    //X Death Collision (spikes)
                    killPlayer();
                    this.player_pos_x = this.player_collision_px.left;
                    this.player_pos_y = this.player_collision_px.top;
                }

                this.player_velocity_y = 0;
                this.player_inAir = false;
                this.player_first_gravity_inAir = false;
            } else {
                //None of the player corners collides with anything
                this.player_pos_y = this.player_collision_px.top;
                this.player_inAir = true;
                this.player_onBoost = false;

                EscapeSoundManager.getInstance(this.context).stopSoundLoop();
            }
            this.player_pos_x = this.player_collision_px.left;
        } else {
            //Player is out of bounds => DIE!
            killPlayer();
            this.player_pos_x = this.player_collision_px.left;
            this.player_pos_y = this.player_collision_px.top;
        }
    }

    /**
     * Boosts the player speed by a factor of 1.5 if going right, otherwise slows down by factor of 0.66
     * Only works once per boost platform
     *
     * @since 0.1
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
     *
     * @since 0.1
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
     *
     * @since 0.1
     */
    private void adjustPositionY() {
        if (this.player_velocity_y > 0)
            this.player_pos_y = this.player_collision_px.bottom - this.player_collision_px.bottom % 24 - PLAYER_HEIGTH;
        else
            this.player_pos_y = this.player_collision_px.top + (24 - this.player_collision_px.top % 24);

        this.player_last_state = this.player_state;
        this.player_state = PlayerState.RUNNING;

        EscapeSoundManager.getInstance(this.context).playSoundLoop(EscapeSoundManager.getInstance(this.context).snd_steps);
    }

    /**
     * When player object collides with a wall horizontally, player dies
     *
     * @since 0.1
     */
    private void checkCollisionX() {
        if ((this.collision_corners[0] == 1 && this.collision_corners[3] == 1) || (this.collision_corners[1] == 1 && this.collision_corners[2] == 1)) {
            killPlayer();
            this.player_pos_x = this.player_collision_px.left;
            this.player_pos_y = this.player_collision_px.top;
        }
    }

    /**
     * Calculates the exact time it took the player object to collide with the tile object on x axis
     * Player object might overlap the collided object, this calculates exact time it takes to collide without overlap
     *
     * @since 0.1
     */
    private void calcCollisionTimeX() {
        if (this.player_velocity_x < 0)
            this.col_time_x = (this.player_collision_tiles.right * 24 - this.player_pos_x) / (this.player_velocity_x * this.player_boost_x);
        else
            this.col_time_x = (this.player_collision_tiles.left * 24 + (24 - PLAYER_WIDTH) - this.player_pos_x) / (this.player_velocity_x * this.player_boost_x); //TODO: (24 - PLAYER_WIDTH) only works with PLAYER_WIDTH < 24
    }

    /**
     * Calculates the exact time it took the player object to collide with the tile object on y axis
     * Player object might overlap the collided object, this calculates exact time it takes to collide without overlap
     *
     * @since 0.1
     */
    private void calcCollisionTimeY() {
        if (this.player_velocity_y < 0)
            this.col_time_y = (this.player_collision_tiles.bottom * 24 - this.player_pos_y) / this.player_velocity_y;
        else
            this.col_time_y = (this.player_collision_tiles.top * 24 - this.player_pos_y) / this.player_velocity_y;
    }

    /**
     * Draws the current state of the game onto c
     *
     * @param c              The Canvas that is drawed onto
     * @param deltaFrameTime The passed time since the last frame
     * @since 0.1
     */
    public void draw(Canvas c, float deltaFrameTime) {
        if (!this.player_no_input) {
            translateX(c);
            translateY(c);
        }

        drawMap(c);

        if (!this.player_invisible)
            drawPlayer(c, deltaFrameTime);

        drawHUD(c, deltaFrameTime);
    }

    /**
     * Translates the X drawing area to fit the current player position
     *
     * @param c Canvas that needs to be translated (needed for width and heigth)
     */
    private void translateX(Canvas c) {
        if (this.player_velocity_x > 0) {
            this.trans_x = this.player_pos_x * this.stage.stage_scale - 96 * this.stage.stage_scale;
        } else {
            this.trans_x = this.player_pos_x * this.stage.stage_scale - (c.getWidth() - 120 * this.stage.stage_scale);
        }
        if (this.trans_x < 0) this.trans_x = 0;
        else if (this.trans_x > this.stage.stage_foreground.getWidth() * this.stage.stage_scale - c.getWidth())
            this.trans_x = this.stage.stage_foreground.getWidth() * this.stage.stage_scale - c.getWidth();

        this.trans_x_unscaled = this.trans_x / this.stage.stage_scale;
    }

    /**
     * Translates the Y drawing area to fit the current player position
     *
     * @param c Canvas that needs to be translated (needed for width and heigth)
     */
    private void translateY(Canvas c) {
        if (this.player_pos_y * this.stage.stage_scale + PLAYER_HEIGTH * this.stage.stage_scale > this.trans_y + c.getHeight() - 48 * this.stage.stage_scale)
            this.trans_y = this.player_pos_y * this.stage.stage_scale + PLAYER_HEIGTH * this.stage.stage_scale - c.getHeight() + 48 * this.stage.stage_scale;
        else if (this.player_pos_y * this.stage.stage_scale < this.trans_y + 48 * this.stage.stage_scale)
            this.trans_y = this.player_pos_y * this.stage.stage_scale - 48 * this.stage.stage_scale;
        if (this.trans_y < 0) this.trans_y = 0;
        else if (this.trans_y > this.stage.stage_foreground.getHeight() * this.stage.stage_scale - c.getHeight())
            this.trans_y = this.stage.stage_foreground.getHeight() * this.stage.stage_scale - c.getHeight();

        this.trans_y_unscaled = this.trans_y / this.stage.stage_scale;
    }

    /**
     * Draws the level including background
     *
     * @param c Canvas to draw the level onto
     */
    private void drawMap(Canvas c) {
        this.draw_src.set(
                (int) (this.trans_x_unscaled),
                (int) (this.trans_y_unscaled),
                (int) (c.getWidth() / this.stage.stage_scale + this.trans_x_unscaled),
                (int) (c.getHeight() / this.stage.stage_scale + this.trans_y_unscaled)
        );

        this.draw_tar.set(
                0,
                0,
                c.getWidth(),
                c.getHeight()
        );

        //c.drawBitmap(this.stage.stage_foreground, -this.trans_x, -this.trans_y, null);
        c.drawBitmap(
                this.stage.stage_foreground,
                this.draw_src,
                this.draw_tar,
                null
        );
    }

    /**
     * Draws the current player frame fully transformed
     *
     * @param c              Canvas to draw the player frame onto
     * @param deltaFrameTime The passed time since the last frame
     */
    private void drawPlayer(Canvas c, float deltaFrameTime) {
        this.player_draw_matrix.reset();
        if (this.player_velocity_x > 0) {
            this.player_draw_matrix.setTranslate(this.player_pos_x * this.stage.stage_scale - this.trans_x, this.player_pos_y * this.stage.stage_scale - this.trans_y);
            this.player_draw_matrix.preScale(this.player_draw_scale * this.stage.stage_scale, this.player_draw_scale * this.stage.stage_scale);
        } else {
            this.player_draw_matrix.setTranslate((this.player_pos_x + PLAYER_WIDTH) * this.stage.stage_scale - this.trans_x, this.player_pos_y * this.stage.stage_scale - this.trans_y);
            this.player_draw_matrix.preScale(-this.player_draw_scale * this.stage.stage_scale, this.player_draw_scale * this.stage.stage_scale);
        }

        if (this.player_last_state == PlayerState.JUMPING && this.player_state == PlayerState.RUNNING) {
            //LANDING
            this.player_state = PlayerState.START_END_JUMP;
            this.player_anim_time = 0;
        }

        this.player_anim_time = (this.player_anim_time + deltaFrameTime) % 1000;
        switch (this.player_state) {
            case IDLE:
                this.player_current_frame = (int) ((this.player_anim_time / FRAME_TIME) % 11) + 8;
                break;
            case WAKEUP:
                this.player_current_frame = (int) ((this.player_anim_time / FRAME_TIME) % 8) + 20;
                break;
            case RUNNING:
                this.player_current_frame = (int) ((this.player_anim_time / FRAME_TIME) % 6) + 42;
                if (this.gravity < 0) {
                    this.player_draw_matrix.postTranslate(0, PLAYER_HEIGTH * this.stage.stage_scale);
                    this.player_draw_matrix.preScale(1, -1);
                }
                break;
            case JUMPING:
                if (this.player_velocity_y < 0 && this.gravity > 0 || this.player_velocity_y > 0 && this.gravity < 0) {
                    //JUMP UP
                    this.player_current_frame = (int) (this.player_anim_time / FRAME_TIME) % 3 + 39;
                } else if (this.player_velocity_y < 0 && this.gravity < 0 || this.player_velocity_y > 0 && this.gravity > 0) {
                    //JUMP DOWN
                    this.player_current_frame = (int) (this.player_anim_time / FRAME_TIME) % 2 + 34;
                }
                if (this.gravity < 0) {
                    this.player_draw_matrix.postTranslate(0, 24 * this.stage.stage_scale);
                    this.player_draw_matrix.preScale(1, -1);
                }
                break;
            case START_END_JUMP:
                if (this.player_anim_time > FRAME_TIME * 2) {
                    if (this.player_last_state == PlayerState.JUMPING) {
                        this.player_last_state = this.player_state;
                        this.player_state = PlayerState.RUNNING;
                    } else if (this.player_last_state == PlayerState.RUNNING) {
                        this.player_last_state = this.player_state;
                        this.player_state = PlayerState.JUMPING;
                    }
                    this.player_anim_time = 0;
                }
                this.player_current_frame = (int) ((this.player_anim_time) / FRAME_TIME) % 3 + 36;
                if (this.gravity < 0) {
                    this.player_draw_matrix.postTranslate(0, PLAYER_HEIGTH * this.stage.stage_scale);
                    this.player_draw_matrix.preScale(1, -1);
                }
                break;
            case GRAVITY:
                if (this.gravity < 0) {
                    this.player_current_frame = (int) (this.player_anim_time / FRAME_TIME) % 3 + 31;
                } else {
                    this.player_current_frame = (int) (this.player_anim_time / FRAME_TIME) % 3 + 28;
                }
                break;
            case DYING:
                if (this.player_anim_time > FRAME_TIME * 7) {
                    this.player_invisible = true;
                } else {
                    this.player_current_frame = (int) ((this.player_anim_time / FRAME_TIME) % 8);
                }
                if (this.gravity < 0) {
                    this.player_draw_matrix.postTranslate(0, PLAYER_HEIGTH * this.stage.stage_scale);
                    this.player_draw_matrix.preScale(1, -1);
                }
                break;
        }

        c.drawBitmap(this.player_frames[this.player_current_frame], player_draw_matrix, null);
    }

    /**
     * Draws the HUD on the canvas
     * HUD includes pausescreen, deathscreen, finishedscreen, fadeins, fadeouts, deathcounter, ...
     *
     * @param c              Canvas to draw the HUD onto
     * @param deltaFrameTime Passed time since the last frame
     */
    private void drawHUD(Canvas c, float deltaFrameTime) {
        drawDeathCounter(c);

        if (this.paused) {
            drawFadeout(c, deltaFrameTime, 200, 128);
            drawPauseScreen(c);
        } else if (this.player_dead) {
            //Player is dead. Draw retry message
            drawFadeout(c, deltaFrameTime, 1000, 255, 300);
            c.drawText(this.you_died_retry, this.screenWidth / 2, this.screenHeight / 2, this.text_border_paint);
            c.drawText(this.you_died_retry, this.screenWidth / 2, this.screenHeight / 2, this.text_paint);
        } else if (this.finished) {
            drawFadeout(c, deltaFrameTime, 3500, 255);
            c.drawText(this.stage.stage_name + " " + finished_next_level, this.screenWidth / 2, this.screenHeight / 2, this.text_border_paint);
            c.drawText(this.stage.stage_name + " " + finished_next_level, this.screenWidth / 2, this.screenHeight / 2, this.text_paint);
        } else if (this.start_circle_radius < 1) {
            //Stage has started. Draw expanding circle first second
            this.start_circle_canvas.drawCircle((this.player_pos_x + PLAYER_WIDTH / 2) * this.stage.stage_scale, (this.player_pos_y + PLAYER_HEIGTH / 2) * this.stage.stage_scale, this.start_circle_radius * this.screenWidth, trans_paint);
            c.drawBitmap(start_circle_bmp, 0, 0, null);
        } else if (this.player_no_input) {
            drawFadeout(c, deltaFrameTime, 2500, 255);
            EscapeSoundManager.getInstance(this.context).fadeSoundLoop(this.current_fade_out_time, 3500, 0f);
        }
    }

    /**
     * Draws the death counter onthe canvas
     *
     * @param c Canvas to draw the death counter onto
     */
    private void drawDeathCounter(Canvas c) {
        c.drawBitmap(this.death_counter_icon, 10 * this.density, c.getHeight() - this.death_counter_icon.getHeight() - 10 * this.density, null);
        c.drawText("" + this.user.deathsCurrentLevel, 48 * this.density, c.getHeight() - 13 * this.density - this.death_counter_icon.getHeight() / 2f - this.death_counter_paint.ascent() / 2, this.death_counter_paint);
    }

    /**
     * Draws the pause screen on the canvas
     *
     * @param c Canvas to draw the pause screen onto
     */
    private void drawPauseScreen(Canvas c) {
        c.drawText(context.getResources().getText(R.string.pause_game).toString(), this.screenWidth / 2, this.screenHeight / 3, this.text_border_paint);
        c.drawText(context.getResources().getText(R.string.pause_game).toString(), this.screenWidth / 2, this.screenHeight / 3, this.text_paint);
        c.drawRoundRect(this.continue_touch_zone, 15 * this.density, 15 * this.density, this.button_paint);
        c.drawRoundRect(this.exit_touch_zone, 15 * this.density, 15 * this.density, this.button_paint);
        c.drawText(context.getResources().getText(R.string.continue_game).toString(), this.continue_touch_zone.centerX(), this.continue_touch_zone.centerY() - this.button_text_paint.ascent() / 2, this.button_text_paint);
        c.drawText(context.getResources().getText(R.string.back_to_menu).toString(), this.exit_touch_zone.centerX(), this.exit_touch_zone.centerY() - this.button_text_paint.ascent() / 2, this.button_text_paint);
        if (EscapeSoundManager.getInstance(this.context).isMuted()) {
            c.drawBitmap(this.icon_mute, this.mute_pause_touch_zone.left, this.mute_pause_touch_zone.top, null);
        } else {
            c.drawBitmap(this.icon_sound, this.mute_pause_touch_zone.left, this.mute_pause_touch_zone.top, null);
        }
    }

    /**
     * Draws the fadeout animation on the canvas
     *
     * @param c              Canvas to draw the fadeout animation onto
     * @param deltaFrameTime The passed time since the last frame
     * @param fade_out_time  Time for the screen to fully turn black
     * @param max_alpha      Maximum alpha for the fadeout effect
     */
    private void drawFadeout(Canvas c, float deltaFrameTime, int fade_out_time, int max_alpha) {
        this.current_fade_out_time += deltaFrameTime;
        c.drawARGB(Math.min((int) ((this.current_fade_out_time / fade_out_time) * max_alpha), max_alpha), 0, 0, 0);
    }

    /**
     * Draws the fadeout animation on the canvas
     *
     * @param c              Canvas to draw the fadeout animation onto
     * @param deltaFrameTime The passed time since the last frame
     * @param fade_out_time  Time in ms for the screen to fully turn black
     * @param max_alpha      Maximum alpha for the fadeout effect
     * @param wait_time      Time in ms to wait before starting the fadeout effect
     */
    private void drawFadeout(Canvas c, float deltaFrameTime, int fade_out_time, int max_alpha, int wait_time) {
        this.current_fade_out_time += deltaFrameTime;
        if (this.current_fade_out_time > wait_time) {
            c.drawARGB(Math.min((int) (((this.current_fade_out_time - wait_time) / fade_out_time) * max_alpha), max_alpha), 0, 0, 0);
        }
    }

    /**
     * Sets the stage to finished
     */
    private void finishStage() {
        this.finished = true;
        this.player_last_state = this.player_state;
        this.player_state = PlayerState.DYING; //Same animation as dying is played
        this.player_anim_time = 0;

        if (this.update_user) {
            EscapeSoundManager.getInstance(this.context).stopSoundLoop();

            Highscore highscore = new Highscore(this.user.name, this.user.currentLevel, this.user.deathsCurrentLevel);
            Concurrency.executeAsync(() -> insertHighscore(highscore));

            this.user.currentLevel++;
            this.user.deathsCurrentLevel = 0;
            Concurrency.executeAsync(() -> updateUser(this.user));
        }
        this.update_user = false;
    }

    /**
     * Prepares finishing a stage by not allowing any more input
     */
    private void setNoPlayerInput() {
        if(!this.player_no_input) {
            EscapeSoundManager.getInstance(this.context).pauseMediaPlayer();
            EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).msc_level_beat);
        }

        this.player_no_input = true;
        this.gravity = 1;
        this.player_velocity_y = 0;
    }

    /**
     * Sets the player to dead and applies the dying animation. Can be called multiple times
     */
    private void killPlayer() {
        this.player_dead = true;
        this.player_last_state = this.player_state;
        this.player_state = PlayerState.DYING;
        this.player_anim_time = 0;

        if (this.update_user) {
            EscapeSoundManager.getInstance(this.context).pauseMediaPlayer();
            EscapeSoundManager.getInstance(this.context).stopSoundLoop();
            EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_death);
            EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).msc_death);
            this.user.deathsCurrentLevel++;
            this.user.deathsTotal++;
            Concurrency.executeAsync(() -> updateUser(this.user));
        }
        this.update_user = false;
    }

    /**
     * Inverts the gravity of the game to face upside down. Also marks the player to be in air
     * Only works if player is not in air when method call happens
     *
     * @since 0.1
     */
    private void invertGravity() {
        if (!this.player_inAir || !this.player_first_gravity_inAir) {
            this.gravity *= -1;
            this.player_inAir = true;
            this.player_first_gravity_inAir = true;

            this.player_last_state = this.player_state;
            this.player_state = PlayerState.GRAVITY;
            this.player_anim_time = 0;

            if (this.gravity < 0)
                EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_gravity_up);
            else
                EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_gravity_down);
        }
    }

    /**
     * Sets the vertical velocity of the player to make a small jump. Also marks the player to be in air
     * Only works if player is not in air when method call happens
     *
     * @since 0.1
     */
    private void jump() {
        if (!this.player_inAir) {
            this.player_velocity_y -= 5 * gravity; //TODO: Change to proper value
            this.player_inAir = true;

            this.player_last_state = this.player_state;
            this.player_state = PlayerState.START_END_JUMP;
            this.player_anim_time = 0;

            EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_jump);
        }
    }

    /**
     * Manipulates the state of the game depending on incoming MotionEvents
     *
     * @param event Incoming MotionEvent
     * @since 0.1
     */
    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (this.paused) {
                if (this.continue_touch_zone.contains(event.getX(), event.getY())) {
                    this.paused = false;
                    this.current_fade_out_time = 0;
                    EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_button);
                } else if (this.exit_touch_zone.contains(event.getX(), event.getY())) {
                    this.running = false;
                    EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_button);
                } else if (this.mute_pause_touch_zone.contains(event.getX(), event.getY())) {
                    EscapeSoundManager.getInstance(this.context).toggleMute(this.stage.current_music_id);
                    EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_button);
                }
            } else if (this.mute_pause_touch_zone.contains(event.getX(), event.getY())) {
                this.paused = true;
                EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_button);
            } else if (this.player_dead) {
                this.retry();
            } else if (this.finished) {
                this.load(this.user.currentLevel);
            } else if (!this.started) {
                this.started = true;
                this.player_last_state = this.player_state;
                this.player_state = PlayerState.WAKEUP;
                this.player_anim_time = 0;
                EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_button);
            } else if (!this.player_no_input) {
                if (event.getX() < this.screenWidth / 2) {
                    this.invertGravity();
                } else {
                    this.jump();
                }
            }
        }
    }

    /**
     * Is forwarded from activity. Called when the "back" button is pressed on the device
     */
    public void onBackPressed() {
        if (!this.paused && this.started && !this.player_dead) {
            this.paused = true;
            EscapeSoundManager.getInstance(this.context).stopSoundLoop();
            EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_button);
        } else {
            this.user.deathsCurrentLevel++;
            this.user.deathsTotal++;
            Concurrency.executeAsync(() -> updateUser(this.user));
            this.running = false;
            EscapeSoundManager.getInstance(this.context).playSound(EscapeSoundManager.getInstance(this.context).snd_button);
        }
    }

    /**
     * Loads the GameState and the a stage for the first use only
     *
     * @param level The ID of the stage to be loaded. Starts with 0
     * @since 0.1
     */
    public void load(int level) {
        this.started = false;
        this.paused = false;
        this.finished = false;

        this.stage.load(level);

        EscapeSoundManager.getInstance(this.context).releaseMediaPlayer();
        EscapeSoundManager.getInstance(this.context).initMediaPlayer(this.stage.current_music_id, true);

        this.player_pos_x = stage.player_start_x * 24;
        this.player_pos_y = stage.player_start_y * 24 + 24 - PLAYER_HEIGTH;
        this.player_velocity_x = stage.player_velocity_x;
        this.player_boost_x = 1.0f;
        this.player_velocity_y = 0;
        this.player_acceleration_y = 10;
        this.player_dead = false;
        this.player_inAir = true;
        this.player_onBoost = false;
        this.player_first_gravity_inAir = false;
        this.gravity = 1;

        this.start_circle_radius = 0.1f;
        this.start_circle_canvas.drawColor(Color.BLACK);
        this.start_circle_canvas.drawText(this.stage.stage_name, this.screenWidth / 2, this.screenHeight / 2, this.text_border_paint);
        this.start_circle_canvas.drawText(this.stage.stage_name, this.screenWidth / 2, this.screenHeight / 2, this.text_paint);

        this.player_last_state = this.player_state;
        this.player_state = PlayerState.IDLE;
        this.player_anim_time = 0;
        this.player_invisible = false;
        this.player_no_input = false;
        this.current_fade_out_time = 0;

        this.update_user = true;
    }

    /**
     * Resets all changed values since the start of the level. Restarts the stage
     *
     * @since 0.1
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

        this.player_last_state = this.player_state;
        this.player_state = PlayerState.WAKEUP;
        this.player_anim_time = 0;
        this.player_invisible = false;
        this.current_fade_out_time = 0;

        this.paused = false;
        this.finished = false;

        this.update_user = true;

        EscapeSoundManager.getInstance(this.context).resumeMediaPlayer();
    }

    public void setUser(User user) {
        this.user = user;
        load(this.user.currentLevel);
    }

    private void updateUser(User user) {
        EscapeDatabase.getInstance(context).userDao().update(user);
    }

    private void insertHighscore(Highscore highscore) {
        EscapeDatabase.getInstance(context).highscoreDao().insert(highscore);
    }
}
