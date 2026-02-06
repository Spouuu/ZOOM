package game;

public class AmmoPickup {
    public double x, y;
    public game.AmmoType type;
    public int amount;
    public boolean taken = false;

    public AmmoPickup(double x, double y, game.AmmoType type, int amount) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.amount = amount;
    }
}
