/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.killthejob;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Juan
 */
public class ScreenInformation {

    private AssetManager assetManager;
    private Node guiNode;
    private int screenWidth, screenHeight;
    private final int fontSize = 30;
    public int lives;
    public int score;
    private int scoreForExtraLife;
    private BitmapFont guiFont;
    private BitmapText livesText;
    private BitmapText scoreText;
    private BitmapText multiplierText;
    private Node gameOverNode;
    private final String LIVES="Vies: ";
    private final String SCORE="Points: ";
    private final String GAME_OVER="Game Over";
    private final String FINAL_SCORE="Résultat Final ";
    private final String HIGHT_SCORE="Meillieur Score ";

    public ScreenInformation(AssetManager assetManager, Node guiNode, int screenWidth, int screenHeight) {
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setupText();
       
    }

    private void setupText() {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        livesText = new BitmapText(guiFont, false);
        livesText.setLocalTranslation(30, screenHeight - 30, 0);
        livesText.setSize(fontSize);
        livesText.setText(LIVES + lives);
        livesText.setColor(ColorRGBA.Black);
        guiNode.attachChild(livesText);

        scoreText = new BitmapText(guiFont, true);
        scoreText.setLocalTranslation(screenWidth - 200, screenHeight - 30, 0);
        scoreText.setSize(fontSize);
        scoreText.setText(SCORE + score);
        scoreText.setColor(ColorRGBA.Black);
        guiNode.attachChild(scoreText);

        
    }

    public void reset() {
        score = 0;

        lives = 4;


        scoreForExtraLife = 2000;
        updateHUD();
    }

    private void updateHUD() {
        livesText.setText(LIVES + lives);
        scoreText.setText(SCORE + score);

    }

    public void addPoints(int basePoints) {
        score += basePoints;
        if (score >= scoreForExtraLife) {
            scoreForExtraLife += 2000;
            lives++;
        }

        updateHUD();
    }

    public boolean removeLife() {
        if (lives == 0) {
            return false;
        }
        lives--;
        updateHUD();
        return true;
    }

    public void update() {



        updateHUD();


    }

    public void endGame() {
        // init gameOverNode
        gameOverNode = new Node();
        gameOverNode.setLocalTranslation(screenWidth / 2 - 180, screenHeight / 2 + 100, 0);
        guiNode.attachChild(gameOverNode);

        // check highscore
        int highscore = loadHighscore();
        if (score > highscore) {
            saveHighscore();
        }

        // init and display text
        BitmapText gameOverText = new BitmapText(guiFont, false);
        gameOverText.setLocalTranslation(0, 0, 0);
        gameOverText.setSize(fontSize);
        gameOverText.setText(GAME_OVER);
        gameOverText.setColor(ColorRGBA.Black);
        gameOverNode.attachChild(gameOverText);

        BitmapText yourScoreText = new BitmapText(guiFont, false);
        yourScoreText.setLocalTranslation(0, -50, 0);
        yourScoreText.setSize(fontSize);
        yourScoreText.setText(FINAL_SCORE + score);
        yourScoreText.setColor(ColorRGBA.Black);
        gameOverNode.attachChild(yourScoreText);

        BitmapText highscoreText = new BitmapText(guiFont, false);
        highscoreText.setLocalTranslation(0, -100, 0);
        highscoreText.setSize(fontSize);
        highscoreText.setText(HIGHT_SCORE + highscore);
        highscoreText.setColor(ColorRGBA.Black);
        gameOverNode.attachChild(highscoreText);
    }

    private int loadHighscore() {
        return 0;
                
    }

    private void saveHighscore() {
        
    }
}