package game;

public class WeaponPickup {
    public double x, y;
    public game.WeaponType type;
    public boolean taken = false;

    public WeaponPickup(double x, double y, game.WeaponType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
