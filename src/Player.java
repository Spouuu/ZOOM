package game;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;

public class Player {

    public double x, y;
    public double angle;

    public int hp = 100;
    public int maxHp = 100;
    public int ammo = 50;
    public boolean shooting = false;
    public long shootAnimTime = 0;
    public boolean hasKey = false;
    public long lastShotTime = 0;
    private static final long SHOOT_COOLDOWN = 250;
    public int level = 1;       // aktualny level
    public int xp = 0;          // aktualne XP
    public int xpToNextLevel = 100; // XP wymagane do następnego levelu
    public game.Weapon currentWeapon = new game.Weapon(game.WeaponType.PISTOL);



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
    private game.Enemy castShot(double rayAngle, List<game.Enemy> enemies) {
        game.Enemy closest = null;
        double minDist = Double.MAX_VALUE;

        for (game.Enemy e : enemies) {
            if (!e.alive) continue;

            double dx = e.x - x;
            double dy = e.y - y;
            double dist = Math.hypot(dx, dy);

            double a = Math.atan2(dy, dx) - rayAngle;
            if (Math.abs(a) < 0.1 && dist < minDist) {
                closest = e;
                minDist = dist;
            }
        }
        return closest;
    }


    public void shoot(List<game.Enemy> enemies) {
        if (!canShoot()) return;

        lastShotTime = System.currentTimeMillis();

        if (currentWeapon.type == game.WeaponType.SHOTGUN) {
            for (int i = 0; i < 6; i++) {
                double spread = (Math.random() - 0.5) * 0.4;
                double rayAngle = angle + spread;

                game.Enemy hit = castShot(rayAngle, enemies);
                if (hit != null) {
                    hit.takeDamage(currentWeapon.damage, rayAngle);
                }
            }
        } else {
            game.Enemy hit = castShot(angle, enemies);
            if (hit != null) {
                hit.takeDamage(currentWeapon.damage, angle);
            }
        }

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
