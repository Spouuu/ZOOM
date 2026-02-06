package game;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Map {
    private char[][] tiles;
    private int width, height;
    private List<game.Enemy> enemies = new ArrayList<>();
    private List<game.Door> doors = new ArrayList<>();


    public Map(String path) throws Exception {
        InputStream is = getClass().getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }
        height = lines.size();
        width = lines.get(0).length();
        tiles = new char[width][height];
        for (int y = 0; y < height; y++) {
            String row = lines.get(y);
            for (int x = 0; x < width; x++) {
                char c = row.charAt(x);

                // DOMYŚLNIE PODŁOGA
                tiles[x][y] = '0';

                if (c == '1') {
                    tiles[x][y] = '1';
                }

                if (c == 'D') {
                    doors.add(new game.Door(x, y));
                    tiles[x][y] = 'D';
                }

                if (c == 'S') {
                    weaponPickups.add(
                            new game.WeaponPickup(x + 0.5, y + 0.5, game.WeaponType.SHOTGUN)
                    );
                }

                if (c == 'B') {
                    ammoPickups.add(
                            new game.AmmoPickup(x + 0.5, y + 0.5, game.AmmoType.BULLETS, 20)
                    );
                }

                if (c == 'A') {
                    ammoPickups.add(
                            new game.AmmoPickup(x + 0.5, y + 0.5, game.AmmoType.SHELLS, 8)
                    );
                }

                if (c == 'G') {
                    enemies.add(new game.Enemy(x + 0.5, y + 0.5,
                            new game.RandomOrthogonalBehavior(), game.EnemyType.GUARD));
                }

                if (c == 'E') {
                    enemies.add(new game.Enemy(x + 0.5, y + 0.5,
                            new game.RandomOrthogonalBehavior(), game.EnemyType.ELITE));
                }
            }
        }

    }
   private List<game.AmmoPickup> ammoPickups = new ArrayList<>();

    public List<game.AmmoPickup> getAmmoPickups() {
        return ammoPickups;
    }

    public char getTile(double x, double y) {
        int ix = (int)x;
        int iy = (int)y;
        if (ix < 0 || iy < 0 || ix >= width || iy >= height) return '1';
        return tiles[ix][iy];
    }

    public boolean isWall(double x, double y) {
        char c = getTile(x, y);
        return c == '1';
    }
    public game.Door getDoor(double x, double y) {
        int ix = (int) x;
        int iy = (int) y;

        for (game.Door d : doors) {
            if (d.tileX == ix && d.tileY == iy && !d.open) {
                return d;
            }
        }
        return null;
    }

    public boolean isDoor(double x, double y) {
        game.Door d = getDoor(x, y);
        return d != null && d.offsetX > -0.95;
    }


    private List<game.WeaponPickup> weaponPickups = new ArrayList<>();

    public List<game.WeaponPickup> getWeaponPickups() {
        return weaponPickups;
    }



    public void openDoor(double x, double y) {
        game.Door d = getDoor(x, y);
        if (d != null && !d.open && !d.opening) {
            d.opening = true;
        }
    }
    public void update() {
        for (game.Door d : doors) {
            d.update();

            if (d.open) {
                tiles[d.tileX][d.tileY] = '0'; // zwolnienie przejścia
            }
        }
    }


    public List<game.Enemy> getEnemies() {
        return enemies;
    }
}
