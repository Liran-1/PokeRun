package com.example.pokerun;

import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.Arrays;
import java.util.Random;

public class GameManager {

    public static final int EMPTY_CODE = 0;
    public static final int PLAYER_CODE = 1;
    public static final int OBSTACLE_CODE = 2;

    private int score = 0;
    private int lives;
    private int hits = 0;
    private final int lanes = 3;
    private final int rows = 6;
    private int[][] currentState = new int[rows][lanes];
    private int currentPlayerIndex = (((lanes / 2) % 2 == 1) ? (lanes + 1) / 2 : lanes / 2);

    private int obstacleCounter = lanes - 1;        // to make sure user isn't blocked
    private boolean hit = false;

    public GameManager(int lives) {
        this.lives = lives;
        initGameMat();
    }

    private void initGameMat() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < lanes; j++)
                currentState[i][j] = EMPTY_CODE;
        currentState[rows - 1][currentPlayerIndex] = PLAYER_CODE;
    }

    public int[][] setNextState() {
        Random r = new Random();
        for (int i = rows - 1; i > 0; i--) {        // move objs forward
            for (int j = 0; j < lanes; j++) {
                currentState[i][j] = currentState[i - 1][j];
            }
        }
        if(currentState[rows - 1][currentPlayerIndex] == OBSTACLE_CODE){    //obj code overrides
            setHit(true);                                                   //player code
        }
        currentState[rows - 1][currentPlayerIndex] = PLAYER_CODE;           //put player code back
        Arrays.fill(currentState[0], EMPTY_CODE);   // clean first row
        if (obstacleCounter > 0) {
            int rand = r.nextInt(lanes);
            currentState[0][rand] = OBSTACLE_CODE;
        }
        obstacleCounter--;
        if (obstacleCounter == -1)
            obstacleCounter = lanes - 1;
        return currentState;
    }

    public int[][] getCurrentState() {
        return currentState;
    }

    public int[][] movePlayerRight() {
        if (currentState[rows - 1][lanes - 1] == PLAYER_CODE)
            return currentState;

        currentState[rows - 1][currentPlayerIndex] = EMPTY_CODE;
        currentState[rows - 1][currentPlayerIndex + 1] = PLAYER_CODE;
        currentPlayerIndex += 1;
        return currentState;
    }

    public int[][] movePlayerLeft() {
        if (currentState[rows - 1][0] == PLAYER_CODE)
            return currentState;

        currentState[rows - 1][currentPlayerIndex] = EMPTY_CODE;
        currentState[rows - 1][currentPlayerIndex - 1] = PLAYER_CODE;
        currentPlayerIndex -= 1;
        return currentState;
    }

    public boolean isLose(Vibrator v) {
        if (lives == 0)
            return true;
        return false;
    }

    public boolean checkHit(Vibrator v) {
            if(isHit()){
                if (hits < lives)
                    hits++;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
                return true;
            }
        return false;
    }

    public int getLives() {
        return lives;
    }

    public int getRows() {
        return rows;
    }

    public int getLanes() {
        return lanes;
    }

    public int getHits() {
        return hits;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
