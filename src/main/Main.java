package main;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();       //create a window for the game to be displayed in
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  //allows game to close properly when player closes window
        window.setResizable(false);     //disallows window resizing to keep proper aspect ratio
        window.setTitle("Numlet");      //Display name of game at top of window

        GamePanel gamePanel = new GamePanel();      //instantiate the game window
        window.add(gamePanel);

        window.pack();

        window.setLocationRelativeTo(null);     //window is displayed at center of screen
        window.setVisible(true);

        gamePanel.startGameThread();
    }
}