package game;

public class HealthPickup {
    public double x, y;
    public int amount;
    public boolean taken = false;

    public HealthPickup(double x, double y, int amount) {
        this.x = x;
        this.y = y;
        this.amount = amount;
    }
}
