import java.util.Scanner;

public class Player {
    private final String name;
    private int score;
    private final Scanner scanner;

    public Player(String name){
        this.name = name;
        score = 0;
        scanner = new Scanner(System.in);
    }

    public String getName(){
        return this.name;
    }

    public GameCode handlePlayerInput(Grid grid){
        System.out.println("Enter your command : ");
        String[] parsed = scanner.nextLine().split(" ");
        int x;
        int y;
        switch(parsed[0].toLowerCase()){
            case "f":
                if(parsed.length < 3) return GameCode.PASS;
                try {
                    x = Integer.parseInt(parsed[1]);
                    y = Integer.parseInt(parsed[2]);
                } catch (NumberFormatException e) {
                    return GameCode.PASS;
                }
                grid.setFlag(x, y);
            return GameCode.OK;

            case "d":
                if(parsed.length < 3) return GameCode.PASS;
                    try {
                        x = Integer.parseInt(parsed[1]);
                        y = Integer.parseInt(parsed[2]);
                    } catch (NumberFormatException e) {
                        return GameCode.PASS;
                    }
                    return grid.playerDiscoversTile(x, y);

            case "r":
            return GameCode.RESET;

            default:
            return GameCode.PASS;
        }
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
