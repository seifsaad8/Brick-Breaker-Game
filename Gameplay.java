import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Gameplay extends JPanel implements ActionListener, KeyListener {
    private boolean play = false; // because we don't need to start the game immediately
    private boolean ballReleased = false; // Track if the ball has been released
    private int score = 0; // -> initial score
    private int totalBricks = 36;

    // Timer to set the speed of the ball
    private Timer timer;
    private int delay = 8;

    // Position of the paddle
    private int playerX = 310;

    // Position of the ball (initially on top of the paddle)
    private int ballposX = playerX + 45;
    private int ballposY = 530; // Just above the paddle
    private int ballXdir = -1;
    private int ballYdir = -2; // Initial direction: move upward

    // Object of the map generator in the game play
    private MapGenerator map;

    public Gameplay() {
        map = new MapGenerator(4, 9);
        addKeyListener(this); // to detect keys
        setFocusable(true);// to accept keyboard keys
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start(); // to start the game cycle
    }

    // Paint method to draw components
    public void paint(Graphics g) {
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(1, 1, 692, 592);

        // Borders
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, 3, 592);
        g.fillRect(0, 0, 692, 3);
        g.fillRect(681, 0, 3, 592);

        // The paddle
        g.setColor(Color.GREEN);
        g.fillRect(playerX, 550, 100, 8);

        // The ball
        g.setColor(Color.YELLOW);
        g.fillOval(ballposX, ballposY, 20, 20);

        // Drawing map
        map.draw((Graphics2D) g);

        // The scores
        g.setColor(Color.WHITE);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("" + score, 590, 30);

        // When you won the game
        if (totalBricks <= 0) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("You Won", 260, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press (Enter) to Restart", 230, 350);
        }

        // When you lose the game
        if (ballposY > 570) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 30));
            g.drawString("Game Over, Scores: " + score, 190, 300);

            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 20));
            g.drawString("Press (Enter) to Restart", 230, 350);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        // When right key is pressed
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 600) {
                playerX = 600;
            } else {
                moveRight();
            }
        }

        // When left key is pressed
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveLeft();
            }
        }

        // If enter key is pressed
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                // To restart the game
                play = true;
                ballposX = playerX + 45; // Reset ball position above the paddle
                ballposY = 530;
                ballXdir = -1;
                ballYdir = -2;
                score = 0;
                totalBricks = 36;
                map = new MapGenerator(4, 9);
                ballReleased = false; // Reset ball released state

                repaint();
            }
        }
    }

    public void moveLeft() {
        play = true;
        playerX -= 15;
    }

    public void moveRight() {
        play = true;
        playerX += 15;
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void actionPerformed(ActionEvent e) {
        // Movement of the ball
        if (play) {
            if (!ballReleased) {
                // Set ballReleased to true to start ball movement after the game starts
                ballReleased = true;
                return;
            }

            ballposX += ballXdir;
            ballposY += ballYdir;

            // When the ball hits the wall
            if (ballposX < 0 || ballposX > 670) {
                ballXdir = -ballXdir;
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir;
            }

            // Collision between ball and paddle
            Rectangle paddleRect = new Rectangle(playerX, 550, 100, 8);
            if (new Rectangle(ballposX, ballposY, 20, 20).intersects(paddleRect)) {
                int paddleCenterX = playerX + 50; // X-coordinate of the center of the paddle
                int ballCenterX = ballposX + 10; // X-coordinate of the center of the ball

                if (ballCenterX < paddleCenterX) {
                    ballXdir = -1; // Move ball to the left
                } else if (ballCenterX > paddleCenterX) {
                    ballXdir = 1; // Move ball to the right
                } else {
                    // You can handle the middle section collision differently if needed
                }
                ballYdir = -ballYdir; // Example, adjust as needed
            }

            // Check map collision with the ball
            A:
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickWidth + 80;
                        int brickY = i * map.brickHeight + 50;
                        int brickWidth = map.brickWidth;
                        int brickHeight = map.brickHeight;

                        Rectangle brickRect = new Rectangle(brickX, brickY, brickWidth, brickHeight);
                        Rectangle ballRect = new Rectangle(ballposX, ballposY, 20, 20);

                        if (ballRect.intersects(brickRect)) {
                            map.setBrickValue(0, i, j);
                            score += 5;
                            totalBricks--;

                            // When ball hits top or bottom of brick
                            if (ballposX + 19 <= brickRect.x || ballposX + 1 >= brickRect.x + brickRect.width) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }

                            break A;
                        }
                    }
                }
            }
            repaint();
        }
    }

}
