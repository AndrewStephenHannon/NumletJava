package main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Sprite {
    private BufferedImage spriteSheet;      //stores the spritesheet as one image
    private int frameWidth;                 //pixel width of each frame in spritesheet
    private int frameHeight;                //pixel height of each frame in spritesheet

    public Sprite(String file, int frameWidth, int frameHeight) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;

        try {
            spriteSheet = ImageIO.read(new File("Assets/" + file + ".png"));    //load the spritesheet
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get the spritesheet as an array of images
    public BufferedImage[] getSpritesheetFrames() {
        //in this simple game where every entity has one small animation, each spritesheet
        //will be one row of sprites, so no need to accommodate for several rows in spritesheet.
        //The number of frames will be equal to the width of the sprite sheet divided by the width of a single frame
        int numFrames = spriteSheet.getWidth() / frameWidth;
        BufferedImage[] frames = new BufferedImage[numFrames];

        //get each frame as a subimage of the spritesheet and store in an array
        for(int i=0; i<numFrames; i++) {
            frames[i] = spriteSheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
        }

        return frames;
    }
}
