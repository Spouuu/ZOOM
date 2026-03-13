package game;

import javax.swing.JFrame;
import javax.imageio.ImageIO;
import java.awt.Image;

public class Game {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    public Game() {
        JFrame frame = new JFrame("Zoom");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        try {
            Image icon = ImageIO.read(getClass().getResource("/logo.png"));
            frame.setIconImage(icon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        game.GamePanel panel = new game.GamePanel();
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        panel.start();
    }
}