package game;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Map {

    private char[][] grid;
    public int width;
    public int height;

    public Map(String path) throws Exception {
        load(path);
    }

    private void load(String path) throws Exception {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(path))
        );

        String line;
        int rows = 0;
        int cols = 0;

        while ((line = br.readLine()) != null) {
            cols = line.length();
            rows++;
        }
        br.close();

        grid = new char[rows][cols];
        height = rows;
        width = cols;

        br = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream(path))
        );

        int y = 0;
        while ((line = br.readLine()) != null) {
            for (int x = 0; x < line.length(); x++) {
                grid[y][x] = line.charAt(x);
            }
            y++;
        }
        br.close();
    }

    public boolean isWall(double x, double y) {
        int mx = (int) x;
        int my = (int) y;

        if (mx < 0 || my < 0 || my >= height || mx >= width) {
            return true;
        }

        return grid[my][mx] == '1' || grid[my][mx] == 'D';
    }

    public boolean isDoor(double x, double y) {
        int mx = (int) x;
        int my = (int) y;

        if (mx < 0 || my < 0 || my >= height || mx >= width) {
            return false;
        }

        return grid[my][mx] == 'D';
    }

    public void openDoor(double x, double y) {
        int mx = (int) x;
        int my = (int) y;

        if (mx < 0 || my < 0 || my >= height || mx >= width) return;

        if (grid[my][mx] == 'D') {
            grid[my][mx] = '0';
        }
    }

    public char getTile(double x, double y) {
        int mx = (int) x;
        int my = (int) y;

        if (mx < 0 || my < 0 || my >= height || mx >= width) {
            return '1';
        }

        return grid[my][mx];
    }
}
