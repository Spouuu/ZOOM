package game;

import javax.swing.JFrame;

public class Game {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public Game() {
        JFrame frame = new JFrame("Wolfenstein-like");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        game.GamePanel panel = new game.GamePanel();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.start();
    }
}
