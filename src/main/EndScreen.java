package main;

import java.awt.*;
import java.util.LinkedList;

public class EndScreen {
    private String endMessage;                  //stores the string to indicate the end
    private String escPrompt;                   //stores the string to inform player of how to exit game
    private LinkedList<Tile> endMessageTiles;   //stores a list of the end message's characters as tiles
    private LinkedList<Tile> escPromptTiles;    //stores a list of the exit message's characters as tiles
    private GamePanel gp;                       //the game panel object

    //constructor instantiate end screen
    public EndScreen(GamePanel gp) {
        endMessage = "THE END";
        escPrompt = "PRESS ESC TO EXIT THE GAME";
        this.gp = gp;

        loadAssets();
    }

    //load the tiles for the end and exit messages
    private void loadAssets () {
        endMessageTiles = new LinkedList<Tile>();
        escPromptTiles = new LinkedList<Tile>();

        float scale = 1.0f;
        float x = gp.screenWidth/2 - (endMessage.length() * gp.tileSize * scale) / 2;   //set center x position for end message
        float y = gp.screenHeight/2 - gp.tileSize / 2;              //set the y position

        //instantiate the end message as list of tiles
        for(int i=0; i<endMessage.length(); i++) {
            if(endMessage.charAt(i) != ' ') {
                endMessageTiles.add(new Tile(endMessage.charAt(i), "" + endMessage.charAt(i), gp.tileSize, (int) (x + gp.tileSize + (i * gp.tileSize * scale / 1.5)), (int) (y - gp.tileSize / 1.5)));
                endMessageTiles.getLast().scale(scale);
            }
        }

        scale = 0.2f;
        x = gp.screenWidth/2 - (escPrompt.length() * gp.tileSize * scale) / 2;   //set center x position for exit message
        y = gp.screenHeight/2 + gp.tileSize / 2;              //set the y position

        //instantiate the exit message as list of tiles
        for(int i=0; i<escPrompt.length(); i++) {
            if(escPrompt.charAt(i) != ' ') {
                escPromptTiles.add(new Tile(escPrompt.charAt(i), "" + escPrompt.charAt(i), gp.tileSize, (int) (x + gp.tileSize + (i * gp.tileSize * scale / 1.5)), (int) (y - gp.tileSize / 1.5)));
                escPromptTiles.getLast().scale(scale);
            }
        }
    }

    //draw the end and exit messages
    public void draw(Graphics2D g2D) {
        for(int i=0; i<endMessageTiles.size(); i++) {
            endMessageTiles.get(i).draw(g2D);
        }
        for(int i=0; i<escPromptTiles.size(); i++) {
            escPromptTiles.get(i).draw(g2D);
        }
    }

    //update the end and exit animations
    public void update() {
        for(int i=0; i<endMessageTiles.size(); i++) {
            endMessageTiles.get(i).update();
        }
        for(int i=0; i<escPromptTiles.size(); i++) {
            escPromptTiles.get(i).update();
        }
    }
}
