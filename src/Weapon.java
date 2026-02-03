package game;

public class Weapon {

    public game.WeaponType type;
    public int damage;
    public long fireDelay;      // ms
    public double spread;       // rozrzut
    public int pellets;         // ilość pocisków
    public long lastShot = 0;

    public Weapon(game.WeaponType type) {
        this.type = type;

        switch (type) {
            case PISTOL -> {
                damage = 20;
                fireDelay = 400;
                spread = 0.01;
                pellets = 1;
            }
            case SHOTGUN -> {
                damage = 10;
                fireDelay = 800;
                spread = 0.08;
                pellets = 6;
            }
            case CHAINGUN -> {
                damage = 8;
                fireDelay = 120;
                spread = 0.02;
                pellets = 1;
            }
        }
    }

    public boolean canShoot() {
        return System.currentTimeMillis() - lastShot >= fireDelay;
    }
}
