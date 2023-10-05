package main;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Scanner;

public class Level {

    private Sprite spritesheet;         //store the level spritesheet
    private Animation animation;        //controls the level animation
    private int xPos;                   //x position of where level is drawn
    private int yPos;                   //y position of where level is drawn
    private int startX;                 //starting x position for player in level
    private int startY;                 //starting y position for player in level
    private String goal;                //the string for the goal the player is trying to achieve in solving the puzzle
    private LinkedList<Tile> goalObject;          //array that stores the goal tiles so that it can be displayed
    private int levelWidth;             //width of level in action tiles
    private int levelHeight;            //height of level in action tiles
    private LinkedList<Tile> actionTiles;         //stores the action tile objects in the level
    private boolean lockActive;         //determines whether the lock is currently active or not
    private boolean hasStartString;     //a boolean that is triggered if another line in the level file is detected after the player character, indicating there is startString to read in
    private String currLevelName;       //store the current level name
    private Tile lineTile;                  //visual line to indicate to player they can't move back to start position
    private boolean displayLine;                //used to trigger displaying the line mentioned above

    private GamePanel gp;               //game panel object
    private Player player;              //player object

    //constructor to instantiate Level object
    public Level (GamePanel gp, Player player, String levelAsset, String levelName) {
        this.gp = gp;
        this.player = player;

        goalObject = new LinkedList<Tile>();
        actionTiles = new LinkedList<Tile>();
        displayLine = false;
        lockActive = true;
        hasStartString = false;
        currLevelName = levelName;
        LoadLevel("Assets/Levels/" + levelName + ".txt");       //load the level layout

        //setup and start the level animation
        spritesheet = new Sprite(levelAsset, (levelWidth + 2) * gp.tileSize, (levelHeight + 4) * gp.tileSize);
        animation = new Animation(spritesheet.getSpritesheetFrames(), 12);
        animation.start();

        //position the level
        xPos = (gp.screenWidth / 2) - ((levelWidth + 2) * gp.tileSize / 2);
        yPos = (gp.screenHeight / 2) - ((levelHeight + 4) * gp.tileSize / 2);

        //display the goal objective
        displayGoal();
    }

    //Loads the level layout based from text file passed through
    public void LoadLevel(String levelName) {
        int lineIndex = 0;      //keeps track of line number in the text file

        try {
            //access the text file
            File levelFile = new File(levelName);
            Scanner fileReader = new Scanner(levelFile);

            //read through each line of the text file
            while(fileReader.hasNextLine()) {
                String line = fileReader.nextLine();

                //the first line will be the goal objective for the player, so goal is assigned from first line
                if(lineIndex == 0) {
                    goal = line.toUpperCase();
                }
                //second line will indicate the size of the level (width and height)
                else if (lineIndex == 1) {
                    String[] dimensions = line.split(" ");
                    if(dimensions.length == 2) {
                        levelWidth = Integer.parseInt(dimensions[0]);   //assign the first number as the level's width
                        levelHeight = Integer.parseInt(dimensions[1]);  //assign the second number as the level's height
                    }
                    else {
                        System.out.println("Level file has incorrect number of dimensions for level");      //throws error for incorrect number of dimensions
                    }
                }
                else {
                    int offsetCount = 0;        //used to determine the amount of offset on the index when there is character Swap tile
                    int oddWidth = 0;           //variable used to correct tile position depending on whether or not that level width is an odd number of tiles
                    int oddHeight = 0;          //variable used to correct tile position depending on whether or not that level height is an odd number of tiles

                    //check if the width or height is odd or even so as to get the right positioning relative to the screen
                    if(levelWidth%2==1)
                        oddWidth = gp.tileSize / 2;
                    if(levelHeight%2==1)
                        oddHeight = gp.tileSize / 2;

                    //for each character in the line instantiate the tiles with their values and positions
                    for(int i = 0; i < line.length(); i++) {
                        //if the character detected is the '&' symbol, this indicates the lock/end tile of the level
                        if(line.charAt(i) == '&') {
                            //set level lock object's position here (relative to the level grid coordinates)
                            int x = (gp.screenWidth / 2) - oddWidth - gp.tileSize + (gp.tileSize * (i));
                            int y = (gp.screenHeight / 2) - oddHeight - gp.tileSize + (gp.tileSize * (lineIndex-3));

                            actionTiles.add(new Tile("Lock", gp.tileSize, x, y, Tile.TileType.END));       //add lock/end tile to the tile list
                        }

                        //if the character detected is a '!' symbol, this indicates the start position/tile of the player
                        else if(line.charAt(i) == '!') {
                            //set player's position here (relative to the level grid coordinates)
                            int x = (gp.screenWidth / 2) - oddWidth - gp.tileSize + (gp.tileSize * (i));
                            int y = (gp.screenHeight / 2) - oddHeight - gp.tileSize + (gp.tileSize * (lineIndex-3));

                            player.setPosition(x, y);

                            //set the line tile for when the player leaves the start position
                            lineTile = new Tile("Animations/Level/Line", gp.tileSize, x, y);

                            //check for another line of text (this is for levels that have a starting string for the player), otherwise set the player string to empty string
                            if(fileReader.hasNextLine())
                                hasStartString=true;
                            else
                                player.setCurrentString("");
                        }
                        //set the player string to the starting value as indicated in the level's text file
                        else if(hasStartString) {
                            player.setCurrentString(line);
                        }
                        else {
                            //get position for the tile
                            int x = (gp.screenWidth / 2) - oddWidth - gp.tileSize + (gp.tileSize * (i-offsetCount));
                            int y = (gp.screenHeight / 2) - oddHeight - gp.tileSize + (gp.tileSize * (lineIndex-3));

                            //if the character value is '+', create an addition tile
                            if(line.charAt(i) == '+')
                                actionTiles.add(new Tile(line.charAt(i), "add", gp.tileSize, x, y));
                            //if the character value is '-', create a subtraction tile
                            else if(line.charAt(i) == '-')
                                actionTiles.add(new Tile(line.charAt(i), "sub", gp.tileSize, x, y));
                            //if the character value is '*', create a multiplication tile
                            else if(line.charAt(i) == '*')
                                actionTiles.add(new Tile(line.charAt(i), "mult", gp.tileSize, x, y));
                            //if the character value is '/', create a division tile
                            else if(line.charAt(i) == '/')
                                actionTiles.add(new Tile(line.charAt(i), "div", gp.tileSize, x, y));
                            //if the character value is '%', create a subtraction tile
                            else if(line.charAt(i) == '%')
                                actionTiles.add(new Tile(line.charAt(i), "mod", gp.tileSize, x, y));
                            //if the character value is '@', create a character for character swap tile
                            else if(line.charAt(i) == '@') {
                                //get the first swap character's value and create a tile for it
                                Tile swapVal1 = new Tile(line.charAt(i + 1), "" + line.charAt(i + 1), gp.tileSize, x + 15, y + 10);
                                swapVal1.scale(0.3f);   //scale it down to fit inside the parent tile

                                //get the second swap character's value and create a tile for it
                                Tile swapVal2 = new Tile(line.charAt(i + 2), "" + line.charAt(i + 2), gp.tileSize, x + 60, y + 60);
                                swapVal2.scale(0.3f);   //scale it down to fit inside the parent tile

                                //instantiate the swap tile
                                actionTiles.add(new Tile("Swap", gp.tileSize, x, y, swapVal1, swapVal2));
                                offsetCount = offsetCount + 2;
                                i = i+2;    //increment the character index by two since the next two characters are for determining the swap values when a swap tile charcter is detected
                            }
                            //if the character is a '#', this indicates a swap tile that swaps all letters to numbers and numbers to letters in the player's string
                            else if(line.charAt(i) == '#')
                                actionTiles.add(new Tile("Swap_LN", gp.tileSize, x, y, Tile.TileType.SWAP_LN));
                            //if the character is not a space or any of the above characters, then it will be a number or letter tile
                            else if(line.charAt(i) != ' ') {
                                if(line.charAt(i) == '0')
                                    actionTiles.add(new Tile(line.charAt(i), "10", gp.tileSize, x, y));
                                else
                                    actionTiles.add(new Tile(line.charAt(i), "" + line.charAt(i), gp.tileSize, x, y));
                            }
                        }
                    }
                }

                lineIndex++;
            }

            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Specified file could not be found.");
            e.printStackTrace();
        }

    }

    //displays the goal objective for the player in the level
    public void displayGoal() {
        goalObject = new LinkedList<Tile>();
        float scale = 0.5f;
        int x = 0 ;
        int y = 0;

        //get the relative position for the goal string to display
        for(int i = 0; i < actionTiles.size(); i++) {
            if(actionTiles.get(i).getTileType() == Tile.TileType.END) {
                x = actionTiles.get(i).getXPos();
                y = actionTiles.get(i).getYPos();
            }
        }

        //create the goal string as a list of tiles
        for(int i=0; i< goal.length(); i++) {
            if(goal.charAt(i)!=' ') {
                goalObject.add(new Tile(goal.charAt(i), "" + goal.charAt(i), gp.tileSize, (int) (x + gp.tileSize + (i * gp.tileSize * scale / 1.5)), (int) (y - gp.tileSize / 1.5)));
                goalObject.getLast().scale(scale);
            }
        }
    }

    //returns the list of action tiles in the level
    public LinkedList<Tile> getActionTiles() {
        return actionTiles;
    }

    //returns a boolean that determines whether or not the lock object is active in the level
    public boolean isLockActive() {
        return lockActive;
    }

    //sets the lock object as either active or inactive (true or false)
    public void setLockActive(boolean isActive) {
        lockActive = isActive;
    }

    //get the goal string of the current level
    public String getGoal() {
        return goal;
    }

    //get the height of the level in number of tiles
    public int getLevelHeight() {
        return levelHeight;
    }

    //get the name of the current level
    public String getCurrLevelName() {
        return currLevelName;
    }

    //triggers the line at the start of the level to display
    public void triggerDisplayLine() {
        displayLine = true;
    }

    //draws the goal objective and tiles of the level
    public void draw(Graphics2D g2D) {
        g2D.drawImage((Image)animation.getCurrentFrame(), xPos, yPos, null);
        for(int i = 0; i< goalObject.size(); i++) {
            goalObject.get(i).draw(g2D);
        }
        for(int i=0; i<actionTiles.size();i++) {
            if(actionTiles.get(i).getTileType() != Tile.TileType.END)
                actionTiles.get(i).draw(g2D);
            else if(lockActive)
                actionTiles.get(i).draw(g2D);
        }

        if(displayLine)
            lineTile.draw(g2D);
    }

    //updates the animations of the goal objective and tiles of the level
    public void update() {
        animation.update();
        for(int i = 0; i< goalObject.size(); i++) {
            goalObject.get(i).update();
        }
        for(int i=0; i<actionTiles.size();i++) {
            actionTiles.get(i).update();
        }

        if(displayLine)
            lineTile.update();
    }
}
