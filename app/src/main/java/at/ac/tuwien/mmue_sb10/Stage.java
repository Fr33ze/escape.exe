package at.ac.tuwien.mmue_sb10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Stage {
    public String name;
    public int level;
    public int player_start_x, player_start_y;
    public int[][] collision;
    public Bitmap terrain;

    private Context context;
    private Bitmap[] tiles_textures;
    private int[] tiles_collision;
    private int tiles_width;
    private int tiles_heigth;

    public Stage(Context context) {
        this.context = context;
        loadTileset();
    }

    private void loadTileset() {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        Bitmap tileset = BitmapFactory.decodeResource(context.getResources(), R.drawable.tileset24, o);
        tiles_collision = context.getResources().getIntArray(R.array.collision);
        int h = tileset.getWidth() / 24;
        int v = tileset.getHeight() / 24;
        tiles_textures = new Bitmap[h * v];
        int tilenumber = 0;
        for (int x = 0; x < h; x++) {
            for (int y = 0; y < v; y++) {
                tiles_textures[tilenumber] = Bitmap.createBitmap(tileset, x * 24, y * 24, 24, 24);
                tilenumber++;
            }
        }
    }

    public void load(int level) {
        Canvas canvas = null; //draws on terrain bitmap
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("stage" + level + ".txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                switch (line) {
                    case "#info":
                        line = reader.readLine();
                        this.name = line.split("=")[1];
                        break;
                    case "#player":
                        line = reader.readLine();
                        this.player_start_x = Integer.parseInt(line.split("=")[1]);
                        line = reader.readLine();
                        this.player_start_y = Integer.parseInt(line.split("=")[1]);
                        break;
                    case "#size":
                        line = reader.readLine();
                        this.tiles_width = Integer.parseInt(line.split("=")[1]);
                        line = reader.readLine();
                        this.tiles_heigth = Integer.parseInt(line.split("=")[1]);
                        this.terrain = Bitmap.createBitmap(tiles_width * 24, tiles_heigth * 24, Bitmap.Config.ARGB_8888);
                        canvas = new Canvas(this.terrain);
                        this.collision = new int[tiles_width][tiles_heigth];
                        break;
                    case "#tiles":
                        for (int y = 0; y < this.tiles_heigth; y++) {
                            line = reader.readLine();
                            String[] tilesInLine = line.split(" ");
                            for (int x = 0; x < tilesInLine.length; x++) {
                                if (!tilesInLine[x].equals("--")) {
                                    int tile_id = Integer.parseInt(tilesInLine[x]);
                                    canvas.drawBitmap(tiles_textures[tile_id], null, new Rect(x * 24, y * 24, x * 24 + 24, y * 24 + 24), null);
                                    collision[x][y] = tiles_collision[tile_id];
                                }
                            }
                        }
                        break;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
