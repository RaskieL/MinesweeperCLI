import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Grid {

    private final long seed;
    private final int size;
    private int totalBombs;
    private int totalFlags;

    private boolean[][] bombMap;
    private byte[][] proximityMap;
    private boolean[][] isDiscoveredMap;
    private boolean[][] flagMap;

    Grid(int size, long seed){
        this.size = size;
        this.seed = seed;
        this.totalBombs = (int)Math.pow(size, 2) / 6;

        this.bombMapInit();
    }

    private boolean hasPlayerWon(){
        for (int y = 0; y < size; y++){
            for(int x = 0; x < size; x++) {
                if(!isDiscoveredMap[y][x] != bombMap[y][x]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getPlayerScore(){
        if(hasPlayerWon()) return (int)Math.pow(this.totalBombs, 2);
        int score = 0;

        for (int y = 0; y < size; y++){
            for(int x = 0; x < size; x++) {
                if(bombMap[y][x] && (flagMap[y][x] == bombMap[y][x])) {
                    score++;
                }
            }
        }
        return (int)Math.pow(score, 2);
    }

    private void bombMapInit(){
        Random rnd = new Random(seed);
        bombMap = new boolean[size][size];
        int nBomb = 0;

        while(nBomb < totalBombs) {
            // Placing bombs on the grid
            for(int y = 0; y < this.size; y++){
                for( int x = 0; x < this.size; x++){
                    if(rnd.nextFloat() >= 0.95){
                        bombMap[y][x] = true;
                        if(nBomb++ >= totalBombs) break;
                    }
                }
                if(nBomb >= totalBombs) break;
            }
        }
        totalBombs = nBomb;

        proximityMap = new byte[size][size];
        // Place proximity bomb tellers
        for(int y = 0; y < this.size; y++){
            for( int x = 0; x < this.size; x++){
                if(bombMap[y][x]) continue;

                byte n = 0;

                // check grand coté gauche
                if(x - 1 >= 0) {
                    if(bombMap[y][x-1]) n++;
                    if(y - 1 >= 0 && bombMap[y - 1][x - 1]) n++;
                    if(y + 1 < size && bombMap[y + 1][x - 1]) n++;
                }

                // check grand coté droit
                if(x + 1 < size) {
                    if(bombMap[y][x+1]) n++;
                    if(y - 1 >= 0 && bombMap[y - 1][x + 1]) n++;
                    if(y + 1 < size && bombMap[y + 1][x + 1]) n++;
                }

                // check bas et haut
                if(y - 1 >= 0 && bombMap[y - 1][x]) n++;
                if(y + 1 < size && bombMap[y + 1][x]) n++;

                proximityMap[y][x] = n;
            }
        }

        isDiscoveredMap = new boolean[size][size];
        flagMap = new boolean[size][size];
    }

    @Override
    public String toString(){
        StringBuilder grid = new StringBuilder();
        grid.append(String.format("SEED: %d %n", seed));
        grid.append("   ");
        for(int k = 0; k < size; k++){
            grid.append(String.format("  %s%d", k >= 10 ? "" : " ", k));
        }
        grid.append(String.format("\n   %s", size >= 10 ? " " : ""));
        for(int k = 0; k < size * 4 + 1; k++){
            grid.append("-");
        }
        grid.append("\n");
        for(int y = 0; y < size; y++){
            grid.append(String.format(" %d%s |", y, y < 10 ? " " : ""));
            for(int x = 0; x < size; x++){
                if(bombMap[y][x]){
                    grid.append(" B ");
                }else{
                    grid.append(String.format(" %s ", Byte.toString((proximityMap[y][x]))));
                }
                grid.append("|");
            }
            grid.append(String.format("\n   %s", size >= 10 ? " " : ""));
            for(int k = 0; k < size * 4 + 1; k++){
                grid.append("-");
            }
            grid.append("\n");
        }
        return grid.toString();
    }

    public String displayPlayGrid(){
        StringBuilder grid = new StringBuilder();

        grid.append(String.format("SEED: %d %n", seed));
        grid.append("   ");
        for(int k = 0; k < size; k++){
            grid.append(String.format("  %s%d", k >= 10 ? "" : " ", k));
        }
        grid.append(String.format("\n   %s", size >= 10 ? " " : ""));
        for(int k = 0; k < size * 4 + 1; k++){
            grid.append("-");
        }
        grid.append("\n");
        for(int y = 0; y < size; y++){
            grid.append(String.format(" %d%s |", y, y < 10 ? " " : ""));
            for(int x = 0; x < size; x++){
                if(!bombMap[y][x] && isDiscoveredMap[y][x]){
                    grid.append(String.format(" %s ", Byte.toString((proximityMap[y][x]))));
                }else if(flagMap[y][x]){
                    grid.append(String.format(" F "));
                }else{
                    grid.append(String.format(" # "));
                }
                grid.append("|");
            }
            grid.append(String.format("\n   %s", size >= 10 ? " " : ""));
            for(int k = 0; k < size * 4 + 1; k++){
                grid.append("-");
            }
            grid.append("\n");
        }
        grid.append(String.format("Total bombs %d %n", totalBombs));
        grid.append(String.format("Number of flags placed %d", totalFlags));
        return grid.toString();
    }

    public GameCode playerDiscoversTile(int x, int y){
        if(bombMap[y][x]) return GameCode.DEATH;
        if(flagMap[y][x]) return GameCode.PASS;

        int n =  5 + (int)(Math.random() * ((int)Math.pow(size, 2) / 6));
        List<int[]> searched = new ArrayList<>();
        Queue<int[]> toSearch = new ArrayDeque<>();
        toSearch.add(new int[]{x, y});

        do {
            final int[] a = toSearch.poll();
            int _x = a[0];
            int _y = a[1];
            // check grand coté gauche
            if(_x - 1 >= 0) {
                if(!bombMap[_y][_x-1] && !flagMap[_y][_x - 1] && !isDiscoveredMap[_y][_x - 1]) toSearch.add(new int[]{_x - 1, _y});
                if(_y - 1 >= 0 && !bombMap[_y - 1][_x - 1] && !flagMap[_y - 1][_x - 1] && !isDiscoveredMap[_y - 1][_x - 1]) toSearch.add(new int[]{_x - 1, _y - 1});
                if(_y + 1 < size && !bombMap[_y + 1][_x - 1] && !flagMap[_y + 1][_x - 1] && !isDiscoveredMap[_y + 1][_x - 1]) toSearch.add(new int[]{_x - 1, _y + 1});
            }

            // check grand coté droit
            if(_x + 1 < size) {
                if(!bombMap[_y][_x+1] && !flagMap[_y][_x + 1] && !isDiscoveredMap[_y][_x + 1]) toSearch.add(new int[]{_x + 1, _y});
                if(_y - 1 >= 0 && !bombMap[_y - 1][_x + 1] && !flagMap[_y - 1][_x + 1] && !isDiscoveredMap[_y - 1][_x + 1]) toSearch.add(new int[]{_x + 1, _y - 1});
                if(_y + 1 < size && !bombMap[_y + 1][_x + 1] && !flagMap[_y + 1][_x + 1] && !isDiscoveredMap[_y + 1][_x + 1]) toSearch.add(new int[]{_x + 1, _y + 1});
            }

            // check bas et haut
            if(_y - 1 >= 0 && !bombMap[_y - 1][_x] && !flagMap[_y - 1][_x] && !isDiscoveredMap[_y - 1][_x]) toSearch.add(new int[]{_x, _y - 1});
            if(_y + 1 < size && !bombMap[_y + 1][_x] && !flagMap[_y + 1][_x] && !isDiscoveredMap[_y + 1][_x]) toSearch.add(new int[]{_x, _y + 1});

            this.setDiscoverTile(_x,_y, true);
            searched.add(a);
        } while (searched.size() < n && !toSearch.isEmpty());
        
        if(hasPlayerWon()) return GameCode.WIN;

        return GameCode.OK;
    }

    public void setDiscoverTile(int x, int y, boolean val){
        this.isDiscoveredMap[y][x] = val;
    }

    public void setFlag(int x, int y){
        if(isDiscoveredMap[y][x]) return;

        if(this.flagMap[y][x]) totalFlags--;
        else totalFlags++;

        this.flagMap[y][x] = !this.flagMap[y][x];
    }

    public long getSeed() {
        return seed;
    }

    public int getSize() {
        return size;
    }

    public int getTotalBombs() {
        return totalBombs;
    }

    public void setTotalBombs(int totalBombs) {
        this.totalBombs = totalBombs;
    }

    public int getTotalFlags() {
        return totalFlags;
    }

    public void setTotalFlags(int totalFlags) {
        this.totalFlags = totalFlags;
    }

    public boolean[][] getBombMap() {
        return bombMap;
    }

    public void setBombMap(boolean[][] bombMap) {
        this.bombMap = bombMap;
    }

    public byte[][] getProximityMap() {
        return proximityMap;
    }

    public void setProximityMap(byte[][] proximityMap) {
        this.proximityMap = proximityMap;
    }

    public boolean[][] getIsDiscoveredMap() {
        return isDiscoveredMap;
    }

    public void setIsDiscoveredMap(boolean[][] isDiscoveredMap) {
        this.isDiscoveredMap = isDiscoveredMap;
    }

    public boolean[][] getFlagMap() {
        return flagMap;
    }

    public void setFlagMap(boolean[][] flagMap) {
        this.flagMap = flagMap;
    }
}
