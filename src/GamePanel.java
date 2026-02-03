package game;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;


import static game.Enemy.State.DEAD;

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
    private BufferedImage eliteShoot;
    private BufferedImage eliteHit;
    private BufferedImage eliteDead;
    private BufferedImage eliteIdle;
    private BufferedImage guardShoot;
    private BufferedImage guardHit;
    private BufferedImage guardIdle;
    private BufferedImage guardDead;

    private BufferedImage pistolIdle;
    private BufferedImage pistolShoot;
    private BufferedImage currentWeaponSprite;
    private BufferedImage shotgunIdle;
    private BufferedImage shotgunShoot;



    private List<game.Enemy> enemies = new ArrayList<>();
    private double[] zBuffer = new double[WIDTH];

    private Color lerpColor(Color c1, Color c2, double t) {
        t = Math.max(0, Math.min(1, t));
        int r = (int)(c1.getRed()   * (1 - t) + c2.getRed()   * t);
        int g = (int)(c1.getGreen() * (1 - t) + c2.getGreen() * t);
        int b = (int)(c1.getBlue()  * (1 - t) + c2.getBlue()  * t);
        return new Color(r, g, b);
    }


    public GamePanel() {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);

        player = new game.Player(3, 1, 0); // start na pustym kafelku

        player.hp = player.maxHp = 100;


        try {
            map = new game.Map("/levels/level1.txt");
            enemies = map.getEnemies();

            // Wall/door textures
            wallTexture = ImageIO.read(getClass().getResource("/textures/wall.png"));
            doorTexture = ImageIO.read(getClass().getResource("/textures/door.png"));

            // Guard sprites
            guardIdle = ImageIO.read(getClass().getResource("/sprites/guard-idle.png"));
            guardShoot = ImageIO.read(getClass().getResource("/sprites/guard-shoot.png"));
            guardHit   = ImageIO.read(getClass().getResource("/sprites/guard-hit.png"));
            guardDead  = ImageIO.read(getClass().getResource("/sprites/guard-dead.png"));

            // Elite sprites
            eliteIdle = ImageIO.read(getClass().getResource("/sprites/elite-idle.png"));
            eliteShoot = ImageIO.read(getClass().getResource("/sprites/elite-shoot.png"));
            eliteHit   = ImageIO.read(getClass().getResource("/sprites/elite-hit.png"));
            eliteDead  = ImageIO.read(getClass().getResource("/sprites/elite-dead.png"));

            //Gun sprites
            pistolIdle  = ImageIO.read(getClass().getResource("/sprites/weapons/pistol-idle.png"));
            pistolShoot = ImageIO.read(getClass().getResource("/sprites/weapons/pistol-shoot.png"));
            shotgunIdle  = ImageIO.read(getClass().getResource("/sprites/weapons/shotgun-idle.png"));
            shotgunShoot = ImageIO.read(getClass().getResource("/sprites/weapons/shotgun-shoot.png"));


            currentWeaponSprite = pistolIdle;


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
        map.update();          // 👈 TO MUSI BYĆ
        player.update(map);

        for (game.Enemy e : enemies) {
            e.update(player, map);
        }
    }

    private void drawWeapon(Graphics2D g) {
        int weaponWidth = 256;
        int weaponHeight = 192;

        if (player.currentWeapon.type == game.WeaponType.SHOTGUN) {
            weaponWidth = 320;
            weaponHeight = 200;
        }

        int x = WIDTH / 2 - weaponWidth / 2;
        int y = HEIGHT - weaponHeight;

        g.drawImage(currentWeaponSprite, x, y, weaponWidth, weaponHeight, null);
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
        drawEnemies(g);

        drawHUD(g);  // <-- dodajemy HUD z HP i level

        g.dispose();
        bs.show();
    }
    private void drawEnemies(Graphics2D g) {
        for (game.Enemy e : enemies) {

            if (!e.alive && e.getState() != DEAD) continue;

            double dx = e.x - player.x;
            double dy = e.y - player.y;
            double dist = Math.hypot(dx, dy);
            double angle = Math.atan2(dy, dx) - player.angle;

            while (angle < -Math.PI) angle += 2 * Math.PI;
            while (angle > Math.PI) angle -= 2 * Math.PI;

            if (Math.abs(angle) > FOV / 2) continue;

            int sx = (int) ((angle + FOV / 2) / FOV * WIDTH);
            if (sx < 0 || sx >= WIDTH) continue;

            // Sprawdzenie z zBuffer
            if (dist > zBuffer[sx]) continue;

            int size = Math.max(1, (int)(HEIGHT / dist));
            int sy = HEIGHT / 2 - size / 2;

            BufferedImage tex;

            if (e.getType() == game.EnemyType.ELITE) {

                tex = switch (e.getState()) {
                    case SHOOT -> eliteShoot;
                    case HIT   -> eliteHit;
                    case DEAD  -> eliteDead;
                    default    -> eliteIdle;
                };
            } else {
                tex = switch (e.getState()) {
                    case SHOOT -> guardShoot;
                    case HIT   -> guardHit;
                    case DEAD  -> guardDead;
                    default    -> guardIdle;
                };
            }

            g.drawImage(tex, sx - size/2, sy, size, size, null);
        }
        drawHUD(g);
        drawWeapon(g);

    }
    private void drawHUD(Graphics2D g) {
        int barWidth = 200;
        int barHeight = 20;
        int margin = 20;

        int x = margin;
        int y = HEIGHT - barHeight - margin;

        // --- PASEK ŻYCIA ---
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, barWidth, barHeight);

        // Poprawne dzielenie dla procentu HP
        double hpRatio = (double) player.hp / player.maxHp;
        hpRatio = Math.max(0, Math.min(1.0, hpRatio));

        Color startColor = Color.GREEN;
        Color midColor   = Color.YELLOW;
        Color endColor   = Color.RED;

        Color fillColor;
        if (hpRatio > 0.5) {
            double t = (hpRatio - 0.5) * 2; // 0..1
            fillColor = lerpColor(midColor, startColor, t);
        } else {
            double t = hpRatio * 2; // 0..1
            fillColor = lerpColor(endColor, midColor, t);
        }

        g.setColor(fillColor);
        g.fillRect(x, y, (int)(barWidth * hpRatio), barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(x, y, barWidth, barHeight);

        g.setColor(Color.WHITE);
        g.drawString("HP: " + player.hp + "/" + player.maxHp, x + barWidth + 10, y + barHeight - 5);

        // --- PASEK XP ---
        int xpBarHeight = 10;
        int xpY = y + barHeight + 5;

        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, xpY, barWidth, xpBarHeight);

        double xpRatio = (double) player.xp / player.xpToNextLevel;
        xpRatio = Math.max(0, Math.min(1.0, xpRatio));

        g.setColor(Color.CYAN);
        g.fillRect(x, xpY, (int)(barWidth * xpRatio), xpBarHeight);

        g.setColor(Color.WHITE);
        g.drawRect(x, xpY, barWidth, xpBarHeight);

        // Poziom gracza
        g.setColor(Color.WHITE);
        g.drawString("Level: " + player.level, x, y - 5);
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

                char tile = map.getTile(rx, ry);

                if (tile != '0' && tile != 'D') {
                    hitTile = tile;   // ściana
                    break;
                }

                game.Door door = map.getDoor(rx, ry);
                if (door != null) {
                    hitTile = 'D';
                    break;
                }
            }


            dist *= Math.cos(rayAngle - player.angle);
            zBuffer[x] = dist;

            int wallHeight = (int) (HEIGHT / dist);
            int yStart = HEIGHT / 2 - wallHeight / 2;

            BufferedImage tex = (hitTile == 'D') ? doorTexture : wallTexture;
            int texX;
            if (hitTile == 'D') {
                game.Door door = map.getDoor(rx, ry);
                double localX = rx - (door.tileX + door.offsetX);
                texX = (int)(localX * doorTexture.getWidth());
                texX = Math.max(0, Math.min(texX, doorTexture.getWidth() - 1));
            } else {
                texX = (int)((rx + ry) * wallTexture.getWidth()) % wallTexture.getWidth();
            }


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

        game.Door door = map.getDoor(tx, ty);
        if (door != null) {
            map.openDoor(tx, ty);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_E) {
            tryOpenDoor();
        }
        player.keyPressed(e);
        if (e.getKeyCode() == KeyEvent.VK_1) {
            player.currentWeapon = new game.Weapon(game.WeaponType.PISTOL);
            currentWeaponSprite = pistolIdle;
        }

        if (e.getKeyCode() == KeyEvent.VK_2) {
            player.currentWeapon = new game.Weapon(game.WeaponType.SHOTGUN);
            currentWeaponSprite = shotgunIdle;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && player.canShoot()) {
            player.shoot(enemies);

            if (player.currentWeapon.type == game.WeaponType.PISTOL) {
                currentWeaponSprite = pistolShoot;
            } else if (player.currentWeapon.type == game.WeaponType.SHOTGUN) {
                currentWeaponSprite = shotgunShoot;
            }

            new Timer(120, ev -> {
                if (player.currentWeapon.type == game.WeaponType.PISTOL)
                    currentWeaponSprite = pistolIdle;
                else
                    currentWeaponSprite = shotgunIdle;

                ((Timer) ev.getSource()).stop();
            }).start();
        }

    }



    @Override public void keyReleased(KeyEvent e) { player.keyReleased(e); }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseClicked(MouseEvent e) {
        // nieużywane
    }

}
