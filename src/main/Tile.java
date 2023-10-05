package main;

import java.awt.*;

public class Tile {
    private char value;             //character that is displayed on the tile in game
    private Sprite spritesheet;     //the animation spritesheet for the tile
    private Animation animation;    //the animation for the tile
    private int tileSize;           //width and height of the tile
    private int xPos;               //x position of the tile
    private int yPos;               //y position of the tile
    private float scale;            //used to scale the size of the tile

    private Tile swapVal1;          //for swap tiles, indicates the first value that is being swapped
    private Tile swapVal2;          //for swap tiles, indicates the second value that is being swapped

    private TileType tileType;      //indicates the tile type (ex: normal, Swap, etc.)

    //enum for the different types of tiles
    enum TileType{
        NORMAL,
        LINE,
        SWAP_LN,
        SWAP,
        END
    }

    //constructor for initiating a line tile for indicating that the player can't move back to start position of level
    public Tile(String file, int tileSize, int xPos, int yPos) {
        this.spritesheet = new Sprite(file, tileSize, tileSize);
        this.animation = new Animation(spritesheet.getSpritesheetFrames(), 12);
        animation.start();
        this.tileSize = tileSize;

        this.xPos = xPos;
        this.yPos = yPos;

        tileType = TileType.LINE;

        scale = 1;
    }

    //constructor for initiating a regular number or letter tile
    public Tile(char value, String file, int tileSize, int xPos, int yPos) {
        if(file.equals("."))
            file = "Decimal";
        if(file.equals("-"))
            file = "Sub";
        this.value = value;
        this.spritesheet = new Sprite("Animations/Tiles/" + file, tileSize, tileSize);
        this.animation = new Animation(spritesheet.getSpritesheetFrames(), 12);
        animation.start();
        this.tileSize = tileSize;

        this.xPos = xPos;
        this.yPos = yPos;

        tileType = TileType.NORMAL;

        scale = 1;
    }

    //constructor for initiating end tiles or tiles that swap numbers to letters and vice versa
    public Tile(String file, int tileSize, int xPos, int yPos, TileType tType) {
        if(file.equals("."))
            file = "Decimal";
        if(file.equals("-"))
            file = "Sub";
        this.spritesheet = new Sprite("Animations/Tiles/" + file, tileSize, tileSize);
        this.animation = new Animation(spritesheet.getSpritesheetFrames(), 12);
        animation.start();
        this.tileSize = tileSize;

        this.xPos = xPos;
        this.yPos = yPos;

        this.tileType = tType;

        scale = 1;
    }

    //constructor for initiating swap tiles that swap one specified character for another
    public Tile(String file, int tileSize, int xPos, int yPos, Tile swapVal1, Tile swapVal2) {
        if(file.equals("."))
            file = "Decimal";
        if(file.equals("-"))
            file = "Sub";
        this.spritesheet = new Sprite("Animations/Tiles/" + file, tileSize, tileSize);
        this.animation = new Animation(spritesheet.getSpritesheetFrames(), 12);
        animation.start();
        this.tileSize = tileSize;

        this.xPos = xPos;
        this.yPos = yPos;
        this.swapVal1 = swapVal1;
        this.swapVal2 = swapVal2;

        tileType = TileType.SWAP;

        scale = 1;
    }

    //get the value of the tile, if the value is 0, set it to 10 as tile values are stored as a single character
    public String getValue() {
        if(value == '0')
            return "10";
        else
            return "" + value;
    }

    //get the x coordinate
    public int getXPos() {
        return xPos;
    }

    //get the y coordinate
    public int getYPos() {
        return yPos;
    }

    //get the first value for a swap tile
    public String getSwapVal1() {
        return swapVal1.getValue();
    }

    //get the second value for a swap tile
    public String getSwapVal2() {
        return swapVal2.getValue();
    }

    //get the tile type
    public TileType getTileType() {
        return tileType;
    }

    //set the size of the tile
    public void scale(float scale) {
        this.scale = scale;
    }

    //draw function for drawing the tile and it's current animation frame
    public void draw(Graphics2D g2D) {
        //draw sprite animation here
        g2D.drawImage((Image) animation.getCurrentFrame(), (int) xPos, (int) yPos, (int)(tileSize * scale), (int)(tileSize * scale), null);
        if(swapVal1 != null & swapVal2 != null) {
            swapVal1.draw(g2D);
            swapVal2.draw(g2D);
        }
    }

    //update the animation(s)
    public void update() {
        animation.update();
        if(swapVal1 != null & swapVal2 != null) {
            swapVal1.update();
            swapVal2.update();
        }
    }
}
