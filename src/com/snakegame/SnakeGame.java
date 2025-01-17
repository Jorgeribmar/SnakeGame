package com.snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SnakeGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class GamePanel extends JPanel implements ActionListener {
    private final int TILE_SIZE = 30;
    private final int GAME_WIDTH = 600;
    private final int GAME_HEIGHT = 600;
    private final int ALL_TILES = (GAME_WIDTH * GAME_HEIGHT) / (TILE_SIZE * TILE_SIZE);

    private final int[] x = new int[ALL_TILES];
    private final int[] y = new int[ALL_TILES];

    private int snakeLength;
    private int foodX;
    private int foodY;
    private int score;

    private boolean running;
    private boolean left = false;
    private boolean right = true;
    private boolean up = false;
    private boolean down = false;

    private Timer timer;

    public GamePanel() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyHandler());
        initGame();
    }

    private void initGame() {
        snakeLength = 3;
        for (int i = 0; i < snakeLength; i++) {
            x[i] = 100 - i * TILE_SIZE;
            y[i] = 100;
        }
        spawnFood();
        running = true;
        timer = new Timer(150, this);
        timer.start();
    }

    private void spawnFood() {
        Random random = new Random();
        foodX = random.nextInt(GAME_WIDTH / TILE_SIZE) * TILE_SIZE;
        foodY = random.nextInt(GAME_HEIGHT / TILE_SIZE) * TILE_SIZE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            g.setColor(Color.RED);
            g.fillRect(foodX, foodY, TILE_SIZE, TILE_SIZE);

            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                }
                g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
            }

            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 10, 10);
        } else {
            gameOver(g);
        }
    }

    private void move() {
        for (int i = snakeLength; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (left) {
            x[0] -= TILE_SIZE;
        }
        if (right) {
            x[0] += TILE_SIZE;
        }
        if (up) {
            y[0] -= TILE_SIZE;
        }
        if (down) {
            y[0] += TILE_SIZE;
        }
    }

    private void checkCollision() {
        for (int i = snakeLength; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        if (x[0] < 0 || x[0] >= GAME_WIDTH || y[0] < 0 || y[0] >= GAME_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    private void checkFoodCollision() {
        if (x[0] == foodX && y[0] == foodY) {
            snakeLength++;
            score++;
            spawnFood();
        }
    }

    private void gameOver(Graphics g) {
        String message = "Game Over! Score: " + score;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString(message, (GAME_WIDTH - metrics.stringWidth(message)) / 2, GAME_HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFoodCollision();
            checkCollision();
        }
        repaint();
    }

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT && !right) {
                left = true;
                up = false;
                down = false;
            }

            if (key == KeyEvent.VK_RIGHT && !left) {
                right = true;
                up = false;
                down = false;
            }

            if (key == KeyEvent.VK_UP && !down) {
                up = true;
                left = false;
                right = false;
            }

            if (key == KeyEvent.VK_DOWN && !up) {
                down = true;
                left = false;
                right = false;
            }
        }
    }
}
