package game;

public enum EnemyType {
    GUARD(100, 10, 2000, "/sprites/guard.png"),
    ELITE(200, 20, 1500, "/sprites/elite.png");


    public final int hp;
    public final int damage;
    public final long shootCooldown;
    public final String texturePath;

    EnemyType(int hp, int damage, long shootCooldown, String texturePath) {
        this.hp = hp;
        this.damage = damage;
        this.shootCooldown = shootCooldown;
        this.texturePath = texturePath;
    }
}

