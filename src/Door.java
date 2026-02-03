package game;

public class Door {
    public int tileX, tileY;

    public double offsetX = 0.0;   // przesunięcie w lewo
    public boolean opening = false;
    public boolean open = false;

    public static final double SPEED = 0.02;

    public Door(int x, int y) {
        this.tileX = x;
        this.tileY = y;
    }

    public void update() {
        if (opening && !open) {
            offsetX -= SPEED;

            if (offsetX <= -1.0) {
                open = true;
                opening = false;
            }
        }
    }
}
