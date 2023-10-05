package main;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedList;

import static java.lang.System.exit;

public class Player implements KeyListener {
    public float xPos;     //x position of player
    public float yPos;     //y position of player
    public int width;      //player width
    public int height;      //player height
    public float moveInterval = 100;     //the amount of pixels the player moves per movement action

    private Sprite spritesheet;       //stores the player spritesheet
    private Animation animation;      //controls the animation of the player character

    private float startValue;       //start value for player movement. Since tiles width and height are same and player can only move in one direction at a time, this can be used for x or y
    private float endValue;         //same as start value, but for the end point of the player's movement for the lerp
    private long startTime;        //current system time when lerp begins
    private float timeElapsed;      //the amount of time that has elapsed so far during the player movement lerp
    private float lerpDuration;     //The duration of the lerp for player movement
    private float lerpValue;        //keeps track of player movement lerp progress
    private boolean isMovingX;       //boolean for indicating the lerp is currently in progress in the x direction
    private boolean isMovingY;       //boolean for indicating the lerp is currently in progress in the y direction
    private String currentString;   //Stores the Player's current result
    private LinkedList<Tile> currStringTiles; //an array to store the tiles to represent the player's currentString
    private String currOperator;      //stores the last operator tile value that player moved onto, so that it may be used for calculation on the next tile if applicable
    private boolean playerMoved;       //used to detect if the player moved their first move so that the player's string can get initiated

    private int maxStringLength;        //indicates the maximum length the player string can be as they solve the puzzle

    private GamePanel gp;           //game panel object
    private Level level;            //current level object

    //player constructor
    public Player (GamePanel gp, int width, int height) {
        this.gp = gp;
        this.width = width;
        this.height = height;

        //load the player spritesheet and begin the animation
        spritesheet = new Sprite("Animations/Player/Player", width, height);
        animation = new Animation(spritesheet.getSpritesheetFrames(), 12);
        animation.start();

        timeElapsed = 0;
        lerpDuration = 0.1f;        //in seconds

        xPos = 0;
        yPos = 0;
        isMovingX = false;
        isMovingY = false;
        currentString = "";
        currStringTiles = new LinkedList<Tile>();
        currOperator = "";
        playerMoved = false;

        maxStringLength = 12;

        displayCurrentString();     //display the player's starting string of characters
    }

    //set the current level that is loaded
    public void setLevel(Level level) {
        this.level = level;
    }

    //set the player's position
    public void setPosition(float x, float y) {
        xPos = x;
        yPos = y;
    }

    //set the player's current string
    public void setCurrentString(String string) {
        currentString = string;
    }

    //reset the player's values (position, movement variables, current string etc.)
    public void resetPlayer() {
        timeElapsed = 0;
        lerpDuration = 0.1f;        //in seconds

        xPos = 0;
        yPos = 0;
        isMovingX = false;
        isMovingY = false;
        currentString = "";
        currStringTiles = new LinkedList<Tile>();
        currOperator = "";
        playerMoved = false;

        displayCurrentString();
    }

    //currently not utilized
    @Override
    public void keyTyped(KeyEvent e) {

    }

    //detects when the player presses a key down on the keyboard
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();      //get the key that was pressed

        //if the player isn't in the process of moving in either the x or y direction and the attempted move is possible, execute the action when the corresponding key is pressed
        if(!isMovingX && !isMovingY && legalMove(code)) {

            //if the W or Up arrow key is pressed, the player will move up on the screen
            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                startValue = yPos;      //set the start value for the movement lerp
                endValue = yPos - moveInterval;     //set the end value for the movement lerp
                isMovingY = true;       //set the y movement boolean to true to activate the lerp method in Update
                startTime = System.currentTimeMillis();     //get the time when the lerp began
            }
            //if the S or Down arrow key is pressed, the player will move down on the screen
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                startValue = yPos;      //set the start value for the movement lerp
                endValue = yPos + moveInterval;     //set the end value for the movement lerp
                isMovingY = true;       //set the y movement boolean to true to activate the lerp method in Update
                startTime = System.currentTimeMillis();     //get the time when the lerp began
            }
            //if the A or Left arrow key is pressed, the player will move left on the screen
            if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                startValue = xPos;      //set the start value for the movement lerp
                endValue = xPos - moveInterval;     //set the end value for the movement lerp
                isMovingX = true;       //set the x movement boolean to true to activate the lerp method in Update
                startTime = System.currentTimeMillis();     //get the time when the lerp began
            }
            //if the D or Right arrow key is pressed, the player will move right on the screen
            if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                startValue = xPos;      //set the start value for the movement lerp
                endValue = xPos + moveInterval;     //set the end value for the movement lerp
                isMovingX = true;       //set the x movement boolean to true to activate the lerp method in Update
                startTime = System.currentTimeMillis();     //get the time when the lerp began
            }
        }

        //if the R key is pressed, reset the level  and Player to its original state
        if(code == KeyEvent.VK_R)
            gp.loadLevel(level.getCurrLevelName());

        //if the Esc key is pressed, exit the application
        if(code == KeyEvent.VK_ESCAPE)
            exit(0);
    }

    //currently not utilized
    @Override
    public void keyReleased(KeyEvent e) {

    }

    //checks the key that was pressed and if it's a key that would move the player, check what the Player's
    //resulting position would be and check if it's within the confines of the level
    private boolean legalMove(int keyCode) {
        LinkedList<Tile> actionTiles = level.getActionTiles();  //get list of all tiles the Player can move on (includes end tile even if level was not solved)

        if(keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            //return true if the Player's resulting position is equal to any of the tiles in the list (and if it's the end tile, that the lock isn't active)
            for(int i=0; i< actionTiles.size(); i++) {
                if(actionTiles.get(i).getXPos() == xPos && actionTiles.get(i).getYPos() == yPos - moveInterval) {
                    if(actionTiles.get(i).getTileType() == Tile.TileType.END) {
                        if(!level.isLockActive())
                            return true;
                    }
                    else {
                        return true;
                    }
                }
            }
        }
        else if(keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN) {
            //return true if the Player's resulting position is equal to any of the tiles in the list (and if it's the end tile, that the lock isn't active)
            for(int i=0; i< actionTiles.size(); i++) {
                if(actionTiles.get(i).getXPos() == xPos && actionTiles.get(i).getYPos() == yPos + moveInterval) {
                    if(actionTiles.get(i).getTileType() == Tile.TileType.END) {
                        if(!level.isLockActive())
                            return true;
                    }
                    else {
                        return true;
                    }
                }
            }
        }
        else if(keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT) {
            //return true if the Player's resulting position is equal to any of the tiles in the list (and if it's the end tile, that the lock isn't active)
            for(int i=0; i< actionTiles.size(); i++) {
                if(actionTiles.get(i).getXPos() == xPos - moveInterval && actionTiles.get(i).getYPos() == yPos) {
                    if(actionTiles.get(i).getTileType() == Tile.TileType.END) {
                        if(!level.isLockActive())
                            return true;
                    }
                    else {
                        return true;
                    }
                }
            }
        }
        else if(keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) {
            //return true if the Player's resulting position is equal to any of the tiles in the list (and if it's the end tile, that the lock isn't active)
            for(int i=0; i< actionTiles.size(); i++) {
                if(actionTiles.get(i).getXPos() == xPos + moveInterval && actionTiles.get(i).getYPos() == yPos) {
                    if(actionTiles.get(i).getTileType() == Tile.TileType.END) {
                        if(!level.isLockActive())
                            return true;
                    }
                    else {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    //handles the movement lerping process
    private void lerpMove() {
        if(timeElapsed < lerpDuration) {        //while time elapsed is less than lerp duration, lerp is still occurring
            lerpValue = lerp(startValue, endValue, timeElapsed / lerpDuration);     //get the current value of the lerp based off the amount of time that has elapsed

            //if the movement is in the X direction, set the current lerp value to X
            if(isMovingX)
                xPos = lerpValue;
            //if the movement is in the Y direction, set the current lerp value to Y
            else if(isMovingY)
                yPos = lerpValue;

            //get time elapsed in seconds
            timeElapsed = (System.currentTimeMillis() - startTime) / 1000f;
        }
        else {
            lerpValue = endValue;       //once lerp has completed, end Value has been reached

            if(isMovingX)
                xPos = endValue;        //set the X as the endValue, so it snaps to tile based grid
            if(isMovingY)
                yPos = endValue;        //set the Y as the endValue, so it snaps to tile based grid

            level.triggerDisplayLine(); //trigger line to indicate the player can't move back to the start position (for when the player makes their first move)
            onTileAction();

            //reset lerp values
            timeElapsed = 0;
            isMovingX = false;
            isMovingY = false;
        }
    }

    //lerp calculation (progress based from percentage of lerp duration and elapsed time)
    private float lerp(float start, float end, float percentage) {
        return start + percentage * (end - start);
    }

    //when the player lands on a tile, this method will carry out the action that needs to occur
    private void onTileAction() {
        //loop through each tile in the level
        for(int i=0; i<level.getActionTiles().size(); i++) {
            Tile currTile = level.getActionTiles().get(i);

            //when the current tile matches the player's position, execute the corresponding action
            if(currTile.getXPos() == xPos && currTile.getYPos() == yPos) {
                //if the player is now on the end tile, load the next level (doesn't need to check if the lock is
                //active as this is checked first before player can move)
                if(currTile.getTileType() == Tile.TileType.END) {
                    gp.loadNextLevel();
                } else if(currTile.getTileType() == Tile.TileType.SWAP_LN) {    //if the Player is on a letter/number swap tile, call method to swap letters with numbers and vice versa in player string
                    currentString = applyLetterNumberSwapAction();
                    currOperator = "";
                } else if(currTile.getTileType() == Tile.TileType.SWAP) {   //if the Player is on the char-char swap tile, call method to swap these characters in player string
                    currentString = applySwapAction(currTile);
                    currOperator = "";
                } else if(currTile.getTileType() == Tile.TileType.NORMAL) { //when the player lands on a letter, number, or operator tile, carry out the following code
                    //if the tile the player is on is an operator tile, store the operator value in a variable, to carry out the calculation when the player lands on a letter or number next
                    if(currTile.getValue().equals("+") || currTile.getValue().equals("-") || currTile.getValue().equals("*") || currTile.getValue().equals("/") || currTile.getValue().equals("%")) {
                        currOperator = currTile.getValue();
                    }
                    else {
                        //if the player's string is currently empty set the player's string to the character of the tile they are on
                        //as long as it was the player's first move or the previous tile they were on was an addition tile
                        if(!playerMoved && currentString.equals("") || currOperator.equals("+") && currentString.equals("")) {
                            currentString = currTile.getValue();
                            playerMoved = true;
                        }
                        else if(currTile.getValue() != null && !currOperator.equals("")){
                            int digitCounter = 0;       //initialize a counter to keep track of how many digits in a row for currentString, to be used for calculations
                            boolean isString = false;      //initialize a bool that determines if there is a letter in the string or not and therefore string manipulation needs to take place

                            StringBuilder tempString = new StringBuilder();     //keeps track of the player's string as it's being manipulated

                            //cycle through each character in the player's string
                            for(int j=0; j<currentString.length(); j++) {
                                //check if the character is a number and increment the digit counter
                                if(Character.isDigit(currentString.charAt(j)))
                                    digitCounter++;
                                //checks if the character is a decimal
                                else if (currentString.charAt(j) == '.') {
                                    //if the character before and after the decimal is a number, increment the digit counter
                                    if(j>0 && Character.isDigit(currentString.charAt(j-1)) && j < currentString.length() - 1 && !Character.isDigit(currentString.charAt(j+1)))
                                        digitCounter++;
                                    //if the character before and after the decimal is a number, increment the digit counter
                                    else if(j>0 && Character.isDigit(currentString.charAt(j-1)) && j < currentString.length() - 1 && Character.isDigit(currentString.charAt(j+1)))
                                        digitCounter++;
                                    //if the character before the decimal is a number and the last character is the decimal, increment digit counter
                                    else if(j>0 && Character.isDigit(currentString.charAt(j-1)) && j == currentString.length() - 1)
                                        digitCounter++;
                                    //if the first character of the string is a decimal and the next character is a number, increment digit counter
                                    else if(j == 0 && j < currentString.length() - 1 && Character.isDigit(currentString.charAt(j + 1)))
                                        digitCounter++;
                                    //otherwise add the character to the temporary string
                                    else
                                        tempString.append(currentString.charAt(j));
                                }
                                //if letter is detected in the string at this point, apply the calculation of any string of digits before it
                                else if(Character.isLetter(currentString.charAt(j)) || currentString.charAt(j) == ' ') {
                                    //call method to apply calculation to digits up to this detected letter
                                    tempString.append(applyCalculation(j, digitCounter, currTile.getValue()));
                                    tempString.append(currentString.charAt(j)); //add the current character to the end of the resulting string
                                    digitCounter = 0;   //reset digit counter
                                    isString = true;    //if a letter is detected in the string, then string calculation will occur
                                }
                                else if(currentString.charAt(j) == '-') {       //if current character is negative, apply calculation as we reached the end of a number potentially
                                    tempString.append(applyCalculation(j, digitCounter, currTile.getValue()));

                                    //if character after negative value is a number, then we begin incrementing the digit counter
                                    if(j < currentString.length() - 1 && Character.isDigit(currentString.charAt(j+1)))
                                      digitCounter = 1;
                                    //otherwise add the character to the end of the temp string
                                    else {
                                      tempString.append(currentString.charAt(j));
                                      digitCounter = 0;
                                    }
                                }
                            }

                            //apply final digit calculation if last string of characters were digits
                            tempString.append(applyCalculation(currentString.length(), digitCounter, currTile.getValue()));
                            currentString = tempString.toString();
                            //apply the string calculation, which will apply to all letters in the string
                            tempString = new StringBuilder(applyStringManipulation(isString, currTile.getValue()));
                            currentString = tempString.toString();

                            currOperator = "";  //reset operator as calculation took place
                        }
                    }
                }

                //display the updated string
                displayCurrentString();

                //if the resulting string is equal to the goal value of the level, unlock the end tile
                if(currentString.equals(level.getGoal()))
                    level.setLockActive(false);
                //otherwise lock the end tile off from the player
                else
                    level.setLockActive(true);
            }
        }
    }

    //applies the calculation on the substring of digits based on the current operator
    private String applyCalculation(int index, int digitCounter, String currValue) {
        if(digitCounter > 0 && tryParse(currValue) != null) {
            //BigDecimal is used for precision reasons
            BigDecimal tileValue = new BigDecimal(currValue);
            BigDecimal tempNumber = new BigDecimal(currentString);

            //if current value is add, then apply addition with current tile's value
            if(currOperator.equals("+"))
                tempNumber = tempNumber.add(tileValue);
            //if current value is subtraction, then apply subtraction with current tile's value
            else if(currOperator.equals("-"))
                tempNumber = tempNumber.subtract(tileValue);
            //if current value is multiply, then apply multiplication with current tile's value
            else if(currOperator.equals("*"))
                tempNumber = tempNumber.multiply(tileValue);
            //if current value is divide, then apply division with current tile's value
            else if(currOperator.equals("/"))
                tempNumber = tempNumber.divide(tileValue, maxStringLength + 3, RoundingMode.HALF_UP); //round using HALF_UP to make a decimal of 5 round up
            else if(currOperator.equals("%"))
                tempNumber = tempNumber.remainder(tileValue);

            int accNegative = 0;        //set a variable that will accommodate string size when the number is negative

            //set accNegative to 1 if the resulting value is negative
            if(tempNumber.compareTo(BigDecimal.ZERO) < 0)
                accNegative = 1;

            //set the maximum number of decimal numbers
            DecimalFormat df = new DecimalFormat("#");
            df.setMaximumFractionDigits(maxStringLength-1+accNegative);

            //if the resulting number is between -1 and 1
            if(tempNumber.abs().compareTo(BigDecimal.ONE)<0) {
                //if the resulting number is 0 or -0, return 0
                if(df.format((tempNumber)).equals("0") || df.format((tempNumber)).equals("-0"))
                    return "0";
                //if the resulting number is less than zero, add the zero between the negative and decimal
                else if (tempNumber.compareTo(BigDecimal.ZERO)<0)
                    return "-0" + df.format(tempNumber).substring(1);
                //otherwise add the 0 before the decimal
                else
                    return "0" + df.format(tempNumber);
            }
            else if(df.format(tempNumber).length() > maxStringLength+accNegative) {
                //if the resulting number is greater than the max string length, return the original string as resulting string is too big
                if(tempNumber.toBigInteger().toString().length() > maxStringLength+accNegative)
                    return currentString;
                //if the resulting string is equal to the max string length, then return the resulting string
                else if(tempNumber.toBigInteger().toString().length() == maxStringLength + accNegative)
                    return tempNumber.toBigInteger().toString();
                else {
                    //if the resulting number is a whole number, return the resulting number as is
                    if(tempNumber.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0)
                        return tempNumber.toBigInteger().toString();
                    //otherwise return the resulting number up to the maximum string length
                    else
                        return df.format(tempNumber).substring(0, maxStringLength + 1 + accNegative);
                }
            } else {
                //if no numbers after the decimal, return as a whole number
                if(tempNumber.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0)
                    return tempNumber.toBigInteger().toString();
                else
                    //return resulting number
                    return df.format(tempNumber);
            }
        }
        else {
            //if the digit counter was large rthan 0, return the substring of the current player string from the index minus the digit counter value up to the digit counter value
            if(digitCounter>0)
                return currentString.substring(index - digitCounter, digitCounter);
            else
                return "";
        }
    }

    //apply the string manipulation on any letters in the player string
    private String applyStringManipulation(boolean isString, String currValue) {
        StringBuilder tempString = new StringBuilder();     //instantiate a temporary string to store the value of the resulting string as it gets created

        //if the player's string is a string that contains any letters and the value of the tile is a letter, proceed with string manipulation
        if(isString && Character.isLetter(currValue.charAt(0))) {
            if (currOperator.equals("+")) {             //if the current operation is addition
                //return the player string as is if it's already reached max length
                if (currentString.length() >= maxStringLength)
                    tempString = new StringBuilder(currentString);
                //otherwise add the tile value to the end of the string
                else
                    tempString = new StringBuilder(currentString + currValue);
            } else if (currOperator.equals("-")) {      //if the current operation is subtraction
                int letterIndex = -1;   //keep track of the index of the string

                //loop through the string character by character to see if any match the value on the current tile, if so store
                //the index in letterIndex (will get the last instance of the letter occurring in the string)
                for (int i = 0; i < currentString.length(); i++) {
                    if (Character.toString(currentString.charAt(i)).equals(currValue))
                        letterIndex = i;
                }

                //if letterIndex is greater than zero, then delete the character in the string at that index
                if (letterIndex >= 0)
                    tempString = new StringBuilder(currentString.substring(0, letterIndex) + currentString.substring(letterIndex + 1));
                else    //if letterIndex is less than zero, then there was no instance of that letter in the string
                    tempString = new StringBuilder(currentString);
            } else if (currOperator.equals("*")) {          //if the current operation is multiplication
                //return the player string as is if it's already reached max length
                if (currentString.length() >= maxStringLength)
                    tempString = new StringBuilder(currentString);
                else {
                    boolean addMore = true; //set a boolean to keep track of whether or not we can keep adding more characters to the string as we iterate through it

                    //loop through the string and if there is a space detected, add the current tile's value before the space (end of the "word")
                    for (int i = 0; i < currentString.length(); i++) {
                        if (currentString.charAt(i) == ' ' && addMore) {
                            tempString.append(currValue);

                            //if more letters can't be added due to reaching the length max already, addMore is set to false
                            if (tempString.toString().length() + currentString.substring(i, currentString.length() - i).length() >= maxStringLength)
                                addMore = false;

                            //continue to add the rest of the characters we need to from the player's string
                            tempString.append(currentString.charAt(i));
                        } else      //if not a space or can't add more, just add the next character from the player's string
                            tempString.append(currentString.charAt(i));
                    }
                }

                //if the last character in currentString is not a space and we haven't reached the maximum string length yet, then add the current tile's value to the end of the string
                if (currentString.charAt(currentString.length() - 1) != ' ' && tempString.length() < maxStringLength)
                    tempString.append(currValue);
            } else if (currOperator.equals("/")) {      //if the current operation is divide
                //iterate through each character in the player's string and if that character matches the current tile's character, remove it from the string
                for (int i = 0; i < currentString.length(); i++) {
                    if (!Character.toString(currentString.charAt(i)).equals(currValue))
                        tempString.append(currentString.charAt(i));
                }
            } else if (currOperator.equals("%")) {      //if the current operation is mod
                //go through each character of the player's string and mod it by the ascii value of the current tile's value
                for (int i = 0; i < currentString.length(); i++) {
                    if (Character.isLetter(currentString.charAt(i))) {
                        int charValue;

                        if(currValue.charAt(0) - 65 == 0)
                           charValue  = 65;     //When the currValue is 'A', modding will always return A
                        else
                            charValue = (currentString.charAt(i) - 65) % (currValue.charAt(0) - 65) + 65;

                        //append the resulting character
                        if (charValue > 64 && charValue < 91)
                            tempString.append((char) charValue);
                    } else
                        tempString.append(currentString.charAt(i));     //append character as is if not a letter
                }
            }

            //return the resulting string
            return tempString.toString();
        }
        else
            return currentString;   //return player string untouched as string manipulation couldn't occur
    }

    //swaps any letters for numbers in the player's current string and vice versa
    private String applyLetterNumberSwapAction() {
        StringBuilder tempString = new StringBuilder();

        //cycle through each letter in the player string
        for(int i=0; i<currentString.length(); i++) {
            //if the character is a letter, swap it to a number (A=0, B=1, C=2, etc.)
            if(Character.isLetter(currentString.charAt(i)))
                tempString.append((int)(currentString.charAt(i)-65));
            //if the character is a digit, swap it to a letter
            else if(Character.isDigit(currentString.charAt(i))) {
                //if the current character is not a zero and the next character can be coupled with the current character to form a two-digit number
                //less than 26 (to ensure we get all letters of the alphabet), then take the two digits and convert to corresponding letter
                if(currentString.charAt(i) != '0' && i < currentString.length()-1 && Character.isDigit(currentString.charAt(i+1)) &&
                        (((int)currentString.charAt(i)-48) * 10 + (int)currentString.charAt(i+1)-48) <= 25) {
                    tempString.append((char)((((int)currentString.charAt(i)-48) * 10 + (int)currentString.charAt(i+1)-48)+65));
                    i++;
                }
                else        //otherwise take the single digit and convert to corresponding letter
                    tempString.append((char)(currentString.charAt(i)+17));
            }
            else
                tempString.append(currentString.charAt(i));     //if neither a letter or digit, append to the tempString (i.e. negative symbol or space or decimal)
        }

        return tempString.toString();       //return th resulting string
    }

    //swaps any occurrence of the two characters in the tile with each other in the player string
    private String applySwapAction(Tile swapTile) {
        StringBuilder tempString = new StringBuilder();

        //check each character in the string if it matches either character in the tile and swap it for the other
        for(int i=0; i<currentString.length(); i++) {
            //if character matches value 1, swap with value 2
            if(Character.toString(currentString.charAt(i)).equals(swapTile.getSwapVal1()))
                tempString.append(swapTile.getSwapVal2());
            //if character matches value 2, swap with value 1
            else if(Character.toString(currentString.charAt(i)).equals(swapTile.getSwapVal2()))
                tempString.append(swapTile.getSwapVal1());
            else    //otherwise keep the original character
                tempString.append(currentString.charAt(i));
        }

        //return the resulting string
        return tempString.toString();
    }

    //Used to check if the string can be parsed into an integer
    private Integer tryParse(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    //Displays the player's current string
    public void displayCurrentString() {
        currStringTiles = new LinkedList<Tile>();

        //if the string is 1 or more characters
        if(currentString.length()>0) {
            float scale = 0.5f;

            //for each character in the string, position it relative to the length of the string and the center of the screen to position it appropriately
            for (int i = 0; i < currentString.length(); i++) {
                if (currentString.charAt(i) != ' ') {
                    int x = (int) ((gp.screenWidth / 2.0f) + gp.tileSize * scale / 1.5 * i - (gp.tileSize * scale / 1.5 * currentString.length() / 2.0f));
                    int y = (gp.screenHeight / 2) + gp.tileSize * level.getLevelHeight() + gp.tileSize / 2;
                    if (level.getLevelHeight() % 2 == 1)
                        y = (gp.screenHeight / 2) + gp.tileSize * level.getLevelHeight();

                    currStringTiles.add(new Tile(currentString.charAt(i), "" + currentString.charAt(i), gp.tileSize, x, y));
                    currStringTiles.getLast().scale(scale);
                }
            }
        }
    }

    //draw the player's current animation frame and current animation frame of the current string's characters
    public void draw(Graphics2D g2D) {
        //draw sprite animation here
        g2D.drawImage((Image) animation.getCurrentFrame(), (int) xPos, (int) yPos, null);

        for(int i=0; i< currStringTiles.size(); i++)
            currStringTiles.get(i).draw(g2D);
    }

    //update the animations of the player and the current string's characters
    //also updates players movement
    public void update() {
        animation.update();

        for(int i=0; i< currStringTiles.size(); i++)
            currStringTiles.get(i).update();

        if(isMovingX || isMovingY)
            lerpMove(); //if player is currently moving the y or x direction, continue to process the lerp
    }
}
