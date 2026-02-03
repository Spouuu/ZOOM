package game;

public class Enemy {

    public double x, y;
    public boolean alive = true;
    public double speed = 0.02;

    private int health;
    private game.Behavior behavior;
    private game.EnemyType type;
    private long lastShot = 0;
    private static final double PANIC_HP_RATIO = 0.3;

    private long panicBurstEnd = 0;
    private static final long PANIC_BURST_TIME = 800;
    private long lastShotTime = 0;
    private static final long SHOOT_COOLDOWN = 6000;

// ms


    // ANIMACJA
    public enum State {
        IDLE, SHOOT, HIT, DEAD
    }

    private State state = State.IDLE;
    private long stateTime = 0;

    public Enemy(double x, double y, game.Behavior behavior, game.EnemyType type) {
        this.x = x;
        this.y = y;
        this.behavior = behavior;
        this.type = type;
        this.health = type.hp;
    }
    public boolean canSeePlayer(game.Player player, game.Map map) {
        double dx = player.x - x;
        double dy = player.y - y;
        double distance = Math.hypot(dx, dy);

        // krok co 0.05 jednostki wzdłuż linii do gracza
        double stepX = dx / distance * 0.05;
        double stepY = dy / distance * 0.05;

        double checkX = x;
        double checkY = y;

        for (double i = 0; i < distance; i += 0.05) {
            checkX += stepX;
            checkY += stepY;
            if (map.isWall(checkX, checkY)) {
                return false; // ściana blokuje widoczność
            }
        }

        return true; // nic nie blokuje, gracz jest widoczny
    }



    private double normalizeAngle(double a) {
        while (a < -Math.PI) a += Math.PI * 2;
        while (a >  Math.PI) a -= Math.PI * 2;
        return a;
    }

    public void update(game.Player player, game.Map map) {
        if (!alive) return;

        behavior.update(this, player, map);

        // powrót do IDLE po animacji
        if (state != State.IDLE && System.currentTimeMillis() - stateTime > 150) {
            state = State.IDLE;
        }

        // sprawdzanie widoczności i strzelania
        if (canSeePlayer(player, map)) {
            tryShoot(player, map);
        }
    }

    public void shootPlayer(game.Player player) {
        double dist = Math.hypot(player.x - x, player.y - y);
        if (dist < 5.0) {
            player.takeDamage(type.damage);  // use EnemyType damage
        }
    }



    public boolean canShoot() {
        return System.currentTimeMillis() - lastShotTime >= SHOOT_COOLDOWN;
    }

    public void markShot() {
        lastShotTime = System.currentTimeMillis();
    }

    public void tryShoot(game.Player player, game.Map map) {
        if (!alive) return;

        long now = System.currentTimeMillis();
        boolean panic = health < type.hp * PANIC_HP_RATIO;

        long cooldown = panic ? type.shootCooldown / 3 : type.shootCooldown;
        if (now - lastShot < cooldown) return;
        if (!hasLineOfSight(player, map)) return;

        lastShot = now;
        state = State.SHOOT;
        stateTime = now;

        double dx = player.x - x;
        double dy = player.y - y;
        double distance = Math.hypot(dx, dy);

        double baseSpread = 0.05 + distance * 0.03;
        double spread = panic ? baseSpread * 3.5 : baseSpread;

        double angleToPlayer = Math.atan2(dy, dx);
        double shotAngle = angleToPlayer + (Math.random() - 0.5) * spread;

        double rx = x;
        double ry = y;

        for (double d = 0; d < distance; d += 0.05) {
            rx += Math.cos(shotAngle) * 0.05;
            ry += Math.sin(shotAngle) * 0.05;

            if (map.isWall(rx, ry)) return;

            if (Math.hypot(player.x - rx, player.y - ry) < 0.3) {
                player.takeDamage(type.damage);
                return;
            }
        }
    }


    public void takeDamage(int dmg, double hitAngle) {
        health -= dmg;
        state = State.HIT;
        stateTime = System.currentTimeMillis();

        double knockback = 0.15;
        x -= Math.cos(hitAngle) * knockback;
        y -= Math.sin(hitAngle) * knockback;

        if (health <= 0) {
            alive = false;
            state = State.DEAD;
        }
    }


    private boolean hasLineOfSight(game.Player p, game.Map map) {
        double dx = p.x - x;
        double dy = p.y - y;
        double dist = Math.hypot(dx, dy);

        for (double i = 0; i < dist; i += 0.05) {
            double px = x + dx / dist * i;
            double py = y + dy / dist * i;
            if (map.isWall(px, py)) return false;
        }
        return true;
    }

    public State getState() {
        return state;
    }

    public game.EnemyType getType() {
        return type;
    }
}
