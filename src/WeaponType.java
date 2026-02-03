package game;

public enum WeaponType {
    PISTOL   (25, 400, 1),
    SHOTGUN  (10, 900, 6),
    CHAINGUN (15, 100, 1);

    public final int damage;
    public final int cooldown;
    public final int pellets;

    WeaponType(int damage, int cooldown, int pellets) {
        this.damage = damage;
        this.cooldown = cooldown;
        this.pellets = pellets;
    }
}
