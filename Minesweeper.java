
import java.io.*;
import java.time.Clock;
import java.util.Random;


class Minesweeper {
    public static void main(String[] args) {
        Random rnd = new Random();
        long seed = rnd.nextLong();
        boolean botPlays;
        Player player;
        Bot bot;

        if(args.length < 2){
            System.err.println("Usage: Minesweeper botPlays:[0 | 1] gridSize:[integer] OPTIONAL:seed:[any]");
            return;
        }

        try {
            botPlays = Integer.parseInt(args[1]) > 0;
        } catch (NumberFormatException e) {
            System.err.println("Invalid botPlays argument format: must be Integer");
            return;
        }

        int gridSize;

        try {
            gridSize = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid gridSize argument format: must be Integer");
            return;
        }

        if(args.length >= 3){
            try {
                seed = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                String s = "";
                for(String c : args[1].split("")){
                    s += (int)(c.toCharArray()[0] - '0');
                    if(s.length() > 6){
                        s = String.valueOf(Long.parseLong(s) % Integer.MAX_VALUE);
                    }
                }
                seed = Long.parseLong(s);
            }
        }

        var grid = new Grid(gridSize, seed);
        player = new Player("Maiken");
        bot = new Bot(grid);


        printSolutionToFile(grid);

        Clock clock = Clock.systemDefaultZone();
        long startTime = clock.millis();

        System.out.println(String.format("Bot plays %b", botPlays));

        while (true) { 
            System.out.println(grid.displayPlayGrid());
            switch(botPlays ? bot.decide() : player.handlePlayerInput(grid)){
                case OK:
                    continue;
                case PASS:
                    System.out.println("Invalid entry, passing.");
                    continue;
                case DEATH:
                System.out.println("YOU LOSE !");
                System.exit(0);

                case RESET:
                    printSolutionToFile(grid);
                    seed = rnd.nextInt();
                    startTime = clock.millis();
                    grid = new Grid(gridSize, seed);
                    if(botPlays) bot.setGrid(grid);
                    continue;
                case WIN:
                System.out.println(String.format("YOU WIN %s !%nScore: %d", player.getName(), (grid.getPlayerScore() - ((clock.millis() - startTime) / 2000))));
                System.exit(0);
            }
        }
        
    }

    public static void printSolutionToFile(Grid grid){
        try {
            try (BufferedWriter fs = new BufferedWriter(new FileWriter("currentGameSpoiler.txt"))) {
                fs.write(grid.toString());
            }
        } catch (IOException e) {
            System.err.println("IOException: writing to file error");
            System.exit(-1);
        }
    }
}

