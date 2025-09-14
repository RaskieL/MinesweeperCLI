import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bot {
    private final int size;
    private Grid grid;
    private final Random rnd;

    public Bot(Grid grid){
        this.grid = grid;
        rnd = new Random();
        size = grid.getSize();
    }

    private GameCode doRandomDiscover(){
        int x;
        int y;
        do { 
            x = rnd.nextInt(size);
            y = rnd.nextInt(size);
        } while (grid.getIsDiscoveredMap()[y][x] || grid.getFlagMap()[y][x]);

        return grid.playerDiscoversTile(x, y);
    }

    private boolean  findCertainBombs(){
        final boolean[][] isDiscoveredMap = grid.getIsDiscoveredMap();
        final boolean[][] flagMap = grid.getFlagMap();
        final byte[][] proximityMap = grid.getProximityMap();

        boolean managedTo = false;

        for(int y = 0; y < size; y++) {
            for(int x = 0; x < size; x++){
                if(!grid.getIsDiscoveredMap()[y][x] || proximityMap[y][x] == 0) continue;

                int discoveredProx = proximityMap[y][x];
                int n = 0;
                List<int[]> buffer = new ArrayList<>();

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue;

                        int nx = x + dx;
                        int ny = y + dy;
                        if (nx >= 0 && nx < size && ny >= 0 && ny < size) {
                            if(flagMap[ny][nx]){
                                n++;
                                continue;
                            }
                            if (!isDiscoveredMap[ny][nx]) {
                                buffer.add(new int[]{ny, nx});
                                if(n++ > discoveredProx) break;
                            }
                        }
                    }
                    if(n > discoveredProx) break;
                }
                if(n == discoveredProx){
                    for(int[] toFlag : buffer){
                        grid.setFlag(toFlag[1], toFlag[0]);
                    }
                    if(!buffer.isEmpty()) managedTo = true;
                }
            }
        }
        return managedTo;
    }

    public GameCode decide(){
        if(!findCertainBombs()){
            return doRandomDiscover();
        }

        return GameCode.OK;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

}
