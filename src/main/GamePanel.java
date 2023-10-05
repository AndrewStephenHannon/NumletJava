package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    //Screen settings
    public int tileSize;            //tile size
    public int screenWidth;         //screen width
    public int screenHeight;        //screen height
    public int levelIndex;          //index for loading the current level

    final int numLevels = 10;
    int FPS = 60;

    //instantiate the player object
    Player player = new Player(this, 100, 100);
    Thread gameThread;      //for creating the game loop
    Level level;            //current level
    EndScreen endScreen;    //displays the end screen when player beats game

    public GamePanel() {
        tileSize = 100;
        screenWidth = 1024;
        screenHeight = 768;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));        //set the panel size
        this.setBackground(Color.BLACK);        //set the background color
        this.setDoubleBuffered(true);
        this.addKeyListener(player);            //add player as a key listener
        this.setFocusable(true);

        levelIndex = 1;     //set level index to first level

        loadLevel("Level" + levelIndex);        //load level based from index
        endScreen = new EndScreen(this);            //instantiate end screen
    }

    public void startGameThread() {
        gameThread = new Thread(this);  //start the thread by passing the game panel through it
        gameThread.start();     //automatically calls the run method
    }

    //loads the level
    public void loadLevel(String levelName) {
        player.resetPlayer();       //reset the player to start position and clear the player string

        //first level is smaller than the others, so when player is on first level, load the corresponding level animation
        if(levelIndex == 1)
            level = new Level(this, player, "Animations/Level/Level1Wall", levelName);
        else
            level = new Level(this, player, "Animations/Level/LevelWall", levelName);
        player.setLevel(level);
        player.displayCurrentString();
    }

    //increments the level index and loads the next level
    public void loadNextLevel() {
        levelIndex++;

        if(levelIndex>numLevels) {
            System.out.println("End Reached!");
            //Display End Screen
        } else {
            loadLevel("Level" + levelIndex);
        }
    }

    //Creates the game loop
    @Override
    public void run() {
        double drawInterval = 1000000000/FPS;       //draw the screen after this much time elapses
        double delta = 0;                           //change in time
        long lastTime = System.nanoTime();          //time from previous update
        long currentTime;                           //current time

        //gameloop
        while(gameThread != null) {  //while gameThread exists, keep updating game variables/objects and draw visuals with update values.
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;   //get change in time between last time through loop and now and divide by draw interval see if draw interval is reached
                                                                //Once Delta hits 1

            lastTime = currentTime;

            if(delta >= 1) {    //Once Delta reaches 1, then a full draw interval occurred and update and repaint can be called. Delta is reset
                update();
                repaint();
                delta--;
            }

        }
    }

    //draws all the visuals of the game
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;    //converts to 2D for using particular 2d graphics functions

        //if game isn't finished, draw level and player, otherwise draw end screen
        if(levelIndex<=numLevels) {
            level.draw(g2D);
            player.draw(g2D);
        } else {
            endScreen.draw(g2D);
        }

        g2D.dispose();
    }

    //update the values and animations of the game
    public void update() {
        //if game isn't finished, update level and player, otherwise update end screen
        if(levelIndex<=numLevels) {
            level.update();
            player.update();
        } else {
            endScreen.update();
        }
    }
}
