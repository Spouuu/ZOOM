package game;

public enum EnemyType {
    GUARD(300, 20, 1000, "/sprites/guard.png"),
    ELITE(600, 40, 600, "/sprites/elite.png");


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

