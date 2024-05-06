
import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        // first we need to create the frame of the game
        JFrame frame = new JFrame(); // -> this is the class to create a frame
        frame.setBounds(10, 10, 700, 600);
        frame.setTitle("Breakout Ball");
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // to close the program after u close the window
        //=============================================================
        Gameplay gameplay = new Gameplay();
        frame.add(gameplay);
    }
}