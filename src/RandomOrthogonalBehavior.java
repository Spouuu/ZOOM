package game;

import java.util.Random;

public class RandomOrthogonalBehavior implements game.Behavior {

    private double dx = 1, dy = 0;
    private long lastChange = 0;
    private static final long CHANGE_COOLDOWN = 1000;
    private Random rand = new Random();

    @Override
    public void update(game.Enemy enemy, game.Player player, game.Map map) {
        long now = System.currentTimeMillis();

        if (now - lastChange > CHANGE_COOLDOWN) {
            int d = rand.nextInt(4);
            dx = dy = 0;
            if (d == 0) dx = 1;
            if (d == 1) dx = -1;
            if (d == 2) dy = 1;
            if (d == 3) dy = -1;
            lastChange = now;
        }

        double radius = 0.3;

        double nx = enemy.x + dx * enemy.speed;
        double ny = enemy.y + dy * enemy.speed;

        // kolizja X
        if (!map.isWall(nx + Math.signum(dx) * radius, enemy.y)) {
            enemy.x = nx;
        }

        // kolizja Y
        if (!map.isWall(enemy.x, ny + Math.signum(dy) * radius)) {
            enemy.y = ny;
        }
    }

}
