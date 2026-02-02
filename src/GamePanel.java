package game;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends Canvas implements Runnable, KeyListener, MouseListener {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final double FOV = Math.PI / 3;

    private Thread thread;
    private boolean running = true;

    private game.Player player;
    private game.Map map;

    private BufferedImage wallTexture;
    private BufferedImage doorTexture;

    private List<game.Enemy> enemies = new ArrayList<>();
    private double[] zBuffer = new double[WIDTH];

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);

        player = new game.Player(3.5, 3.5, 0);

        try {
            map = new game.Map("/levels/level1.txt");
            wallTexture = ImageIO.read(getClass().getResource("/textures/wall.png"));
            doorTexture = ImageIO.read(getClass().getResource("/textures/door.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            update();
            render();
            try { Thread.sleep(16); } catch (Exception ignored) {}
        }
    }

    private void update() {
        player.update(map);
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics2D g = (Graphics2D) bs.getDrawGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        castRays(g);

        g.dispose();
        bs.show();
    }

    private void castRays(Graphics2D g) {
        for (int x = 0; x < WIDTH; x++) {
            double rayAngle = player.angle - FOV / 2 + (double) x / WIDTH * FOV;

            double rx = player.x;
            double ry = player.y;
            double dist = 0;
            char hitTile = '0';

            while (dist < 20) {
                rx += Math.cos(rayAngle) * 0.02;
                ry += Math.sin(rayAngle) * 0.02;
                dist += 0.02;

                hitTile = map.getTile(rx, ry);
                if (hitTile == '1' || hitTile == 'D') break;
            }

            dist *= Math.cos(rayAngle - player.angle);
            zBuffer[x] = dist;

            int wallHeight = (int) (HEIGHT / dist);
            int yStart = HEIGHT / 2 - wallHeight / 2;

            BufferedImage tex = (hitTile == 'D') ? doorTexture : wallTexture;
            int texX = (int) ((rx + ry) * tex.getWidth()) % tex.getWidth();

            g.drawImage(
                    tex,
                    x,
                    yStart,
                    x + 1,
                    yStart + wallHeight,
                    texX,
                    0,
                    texX + 1,
                    tex.getHeight(),
                    null
            );
        }
    }

    private void tryOpenDoor() {
        double tx = player.x + Math.cos(player.angle);
        double ty = player.y + Math.sin(player.angle);

        if (map.isDoor(tx, ty)) {
            map.openDoor(tx, ty);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_E) {
            tryOpenDoor();
        }
        player.keyPressed(e);
    }

    @Override public void keyReleased(KeyEvent e) { player.keyReleased(e); }
    @Override public void keyTyped(KeyEvent e) {}

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
