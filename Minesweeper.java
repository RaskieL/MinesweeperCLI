
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Clock;


class Minesweeper {
    public static void main(String[] args) {
        if(args.length != 1){
            System.err.println("Usage: Minesweeper grid_size");
            return;
        }

        int gridSize;

        try {
            gridSize = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid argument format: must be Integer");
            return;
        }

        Grid grid = new Grid(gridSize);
        Player player = new Player("Maiken");


        try {
            BufferedWriter fs = new BufferedWriter(new FileWriter("currentGameSpoiler.txt"));
            fs.write(grid.toString());
            fs.close();
        } catch (IOException e) {
            System.err.println("IOException: writing to file error");
            System.exit(-1);
        }

        Clock clock = Clock.systemDefaultZone();
        long startTime = clock.millis();
        while (true) { 
            System.out.println(grid.displayPlayGrid());
            switch(player.handlePlayerInput(grid)){
                case OK:
                    continue;
                case PASS:
                    continue;
                case DEATH:
                System.out.println("YOU LOSE !");
                System.exit(0);

                case RESET:
                    grid = new Grid(gridSize);
                    continue;
                case WIN:
                System.out.println(String.format("YOU WIN %s !%nScore: %d", player.getName(), (grid.getPlayerScore() - ((clock.millis() - startTime) / 1000))));
                System.exit(0);
            }
        }
        
    }
}
