package main;

import java.awt.image.BufferedImage;

public class Animation {
    private BufferedImage[] frames;     //stores the list of frames in the animation
    private int numFrames;              //the number of frames in the animation
    private int currentFrame;           //index of current frame animation is on
    private int frameCount;             //counts the number of frames in game since last frame in animation updated
    private int frameDelay;             //number of game updates before next frame of animation is displayed
    private boolean start;              //cues the animation to begin

    public Animation(BufferedImage[] frames, int frameDelay) {
        this.frames = frames;
        numFrames = frames.length;
        currentFrame = 0;
        frameCount = 0;
        this.frameDelay = frameDelay;
        start = false;
    }

    //cues the animation to begin
    public void start() {
        start = true;
    }

    //stops the animation
    public void stop() {
        start = false;
    }

    //returns the current frame in the animation
    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }

    //updates the frame of the animation
    public void update() {
        //if animation is currently running, increment the frame count
        if(start) {
            frameCount++;

            //if the frame count is greater than frame delay, go to next frame in animation
            if(frameCount > frameDelay) {
                frameCount = 0;
                currentFrame++;

                //if the current frame index is greater than the number of frames, set index to 0 (animation cycles)
                if(currentFrame >= numFrames)
                    currentFrame = 0;
            }
        }
    }
}
