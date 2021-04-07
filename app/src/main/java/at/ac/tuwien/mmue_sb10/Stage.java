package at.ac.tuwien.mmue_sb10;

import android.graphics.Bitmap;

public class Stage {
    public String name;
    public int level;
    public int player_start_x, player_start_y;
    public int[][] collision;
    public Bitmap tiles;
    //public Enemy[] enemies; TODO

    public Stage(int level) {
        //LOAD STUFF FROM RESOURCES TODO
        this.level = level;
        this.name = "Test123";
        this.player_start_x = 1;
        this.player_start_y = 1;
    }
}
