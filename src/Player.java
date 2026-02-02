package game;

import java.awt.event.KeyEvent;

public class Player {

    public double x, y;
    public double angle;

    public int hp = 100;
    public int ammo = 50;
    public boolean hasKey = false;
    private long lastShotTime = 0;
    private static final long SHOOT_COOLDOWN = 2000; // 3 sekundy



    private boolean forward, back, left, right;

    public Player(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void update(game.Map map) {
        double speed = 0.05;

        double nx = x;
        double ny = y;

        if (forward) {
            nx += Math.cos(angle) * speed;
            ny += Math.sin(angle) * speed;
        }
        if (back) {
            nx -= Math.cos(angle) * speed;
            ny -= Math.sin(angle) * speed;
        }
        if (left) angle -= 0.04;
        if (right) angle += 0.04;

        if (!map.isWall(nx, ny)) {
            x = nx;
            y = ny;
        }
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }
    public boolean canShoot() {
        return System.currentTimeMillis() - lastShotTime >= SHOOT_COOLDOWN;
    }

    public void markShot() {
        lastShotTime = System.currentTimeMillis();
    }


    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) forward = true;
        if (e.getKeyCode() == KeyEvent.VK_S) back = true;
        if (e.getKeyCode() == KeyEvent.VK_A) left = true;
        if (e.getKeyCode() == KeyEvent.VK_D) right = true;
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) forward = false;
        if (e.getKeyCode() == KeyEvent.VK_S) back = false;
        if (e.getKeyCode() == KeyEvent.VK_A) left = false;
        if (e.getKeyCode() == KeyEvent.VK_D) right = false;
    }
}
