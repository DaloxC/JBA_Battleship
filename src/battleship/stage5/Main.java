/* It's time to create your own version and have fun with your friends in the famous "Battleship Game".

Requirements:
The game must have some basic rules for the playing field and the location of the ships.
The game must be playable by 2 opponents.
The game must allow you to see whether you land the shots that are projected.
The game must have notifications of events and modifications on the playing field.
It is essential that the structure of the game is reusable and generic for this type of game.

STAGE 5
DaloxC
*/
package battleship.stage5;

import java.util.*;

class WrongLengthException extends Exception {
}

class WrongLocationException extends Exception {
}

class TooCloseException extends Exception {
}

/**
 * LOGIC
 */

// CLASS to SHIFT
class Shift {
    final int x;
    final int y;

    //Block to initialize-constructor the shift.
    Shift(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

// CLASS to SHIP
class Ship {
    final String name;
    final int size;

    //Block to initialize-constructor the ship.
    Ship(String name, int size) {
        this.name = name;
        this.size = size;
    }
}

// CLASS to COORDINATE
class Coordinate {
    final int x;
    final int y;

    // Block to coordinate reading.
    Coordinate(String input) throws WrongLocationException {
        int x = input.endsWith("10") ? 9 : input.charAt(1) - 49;
        int y = input.charAt(0) - 65;

        if (x > 9 || y < 0 || y > 9 || (input.length() == 3 && !input.endsWith("10"))) {
            throw new WrongLocationException();
        }

        this.x = x;
        this.y = y;
    }

    // Block to READ COORDINATE * exception
    static Coordinate readCoordinate() throws WrongLocationException {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        return new Coordinate(input);
    }
}

// CLASS to POSITION
class Position {
    final Coordinate start;
    final Coordinate stop;

    // Block to initialize the positions.
    Position(String start, String stop) throws WrongLocationException {
        Coordinate firstCoordinate = new Coordinate(start);
        Coordinate secondCoordinate = new Coordinate(stop);

        if (firstCoordinate.x < secondCoordinate.x || firstCoordinate.y < secondCoordinate.y) {
            this.start = firstCoordinate;
            this.stop = secondCoordinate;
        } else {
            this.start = secondCoordinate;
            this.stop = firstCoordinate;
        }
    }

    //Block to READ POSITION
    static Position readPosition() throws WrongLocationException {
        Scanner scanner = new Scanner(System.in);
        String start = scanner.next();
        String stop = scanner.next();
        return new Position(start, stop);
    }
}

// CLASS to PLAYER
class Player {
    //Block to define variables & array with ships
    final String name;
    final String[][] board = new String[10][];
    final String[][] opponent_view = new String[10][];
    final String[] ROW_KEYS = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    final Ship[] SHIPS = {
            new Ship("Aircraft Carrier", 5),
            new Ship("Battleship", 4),
            new Ship("Submarine", 3),
            new Ship("Cruiser", 3),
            new Ship("Destroyer", 2)
    };

    //Block to initialize-Constructor Player
    Player(String name) {
        this.name = name;

        for (int y = 0; y < 10; y++) {
            String[] row = new String[10];
            Arrays.fill(row, "~");
            board[y] = row;
        }

        for (int y = 0; y < 10; y++) {
            String[] row = new String[10];
            Arrays.fill(row, "~");
            opponent_view[y] = row;
        }
    }

    // Block to PRINT BOARD
    void printBoard(String[][] board) {

        //Block to print the unit playing field.
        System.out.print(" ");
        for (int x = 1; x < 11; x++) {
            System.out.print(" " + x);
        }
        System.out.println();

        for (int y = 0; y < 10; y++) {
            System.out.print(ROW_KEYS[y]);
            for (int x = 0; x < 10; x++) {
                String cell = board[y][x];
                System.out.print(" " + cell);
            }
            System.out.println();
        }
        System.out.println();
    }

    // Block to PRINT BOARDS
    void printBoards() {

        //Block to print all the playing field.
        printBoard(opponent_view);
        System.out.println("---------------------");
        printBoard(board);
    }

    // Block to SIZE VALID
    private boolean sizeValid(Position position, int size) {
        if (position.start.x == position.stop.x) {
            return size == position.stop.y - position.start.y + 1;
        } else if (position.start.y == position.stop.y) {
            return size == position.stop.x - position.start.x + 1;
        } else {
            return false;
        }
    }

    // Block to COLLISIONS
    private boolean Collisions(Position position, int size) {
        int start_x = position.start.x;
        int start_y = position.start.y;
        int stop_x = position.stop.x;
        int stop_y = position.stop.y;

        assert start_x == stop_x || start_y == stop_y;
        assert stop_x - start_x + 1 == size || stop_y - start_y + 1 == size;

        if (start_x == stop_x) {
            for (int y = start_y; y != stop_y + 1; y++) {
                if (approach(start_x, y)) {
                    return false;
                }
            }
        } else {
            for (int x = start_x; x != stop_x + 1; x++) {
                if (approach(x, start_y)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Block to APPROACH
    private boolean approach(int x, int y) {
        //Block to validate in the following methods the "O" near the point & the exceptions.
        Shift[] shifts = {
                new Shift(-1, 1),
                new Shift(0, 1),
                new Shift(1, 1),
                new Shift(1, 0),
                new Shift(1, -1),
                new Shift(0, -1),
                new Shift(-1, -1),
                new Shift(-1, 0)
        };

        for (Shift shift : shifts) {
            try {
                if (board[y + shift.y][x + shift.x].equals("O")) {
                    return true;
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }

        return false;
    }

    // Block to PLACE SHIP
    void placeShip(int size) throws WrongLocationException, TooCloseException, WrongLengthException {
        Position position = Position.readPosition();

        if (!sizeValid(position, size)) {
            throw new WrongLengthException();
        }

        if (!Collisions(position, size)) {
            throw new TooCloseException();
        }

        int start_x = position.start.x;
        int start_y = position.start.y;
        int stop_x = position.stop.x;
        int stop_y = position.stop.y;

        assert start_x == stop_x || start_y == stop_y;
        assert stop_x - start_x + 1 == size || stop_y - start_y + 1 == size;

        if (start_x == stop_x) {
            for (int y = start_y; y != stop_y + 1; y++) {
                board[y][start_x] = "O";
            }
        } else {
            for (int x = start_x; x != stop_x + 1; x++) {
                board[start_y][x] = "O";
            }
        }
    }

    // Block to PLACE SHIPS
    void placeShips() {
        System.out.println(name + ", place your ships on the game field");
        System.out.println();
        printBoard(board);

        for (Ship ship : SHIPS) {
            System.out.printf("Enter the coordinates of the %s (%d cells):", ship.name, ship.size);
            System.out.println();
            System.out.println();

            while (true) {
                try {
                    placeShip(ship.size);
                    break;
                } catch (WrongLengthException e) {
                    System.out.println();
                    System.out.printf("Error! Wrong length of the %s! Try again:", ship.name);
                    System.out.println();
                } catch (WrongLocationException e) {
                    System.out.println();
                    System.out.println("Error! Wrong ship location! Try again:");
                } catch (TooCloseException e) {
                    System.out.println();
                    System.out.println("Error! You placed it too close to another one. Try again:");
                }
                System.out.println();
            }

            System.out.println();
            printBoard(board);
        }
    }

    Coordinate fire() {
        Coordinate coordinate;

        // SHIFT CHANGE!
        printBoards();
        System.out.println(name + ", it's your turn:");
        System.out.println();

        while (true) {
            try {
                coordinate = Coordinate.readCoordinate();
                break;
            } catch (WrongLocationException e) {
                System.out.println();
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                System.out.println();
            }
        }

        System.out.println();

        return coordinate;
    }

    // Block to SHIP AFLOAT * validate.
    boolean shipAfloat(Coordinate coordinate) {
        return approach(coordinate.x, coordinate.y);
    }

    // Block to HAS SHIPS * count.
    public boolean hasShips() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (board[y][x].equals("O")) {
                    return true;
                }
            }
        }
        return false;
    }
}

/**
 * IMPLEMENTATION
 */
//  CLASS to GAME
class Game {
    Player player1;
    Player player2;

    // Block to initialize game with two players.
    Game() {
        player1 = new Player("Player 1");
        player2 = new Player("Player 2");
    }

    // Block to PASS TURN
    void passTurn() {
        System.out.println("Press Enter and pass the move to another player");
        System.out.println("...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    // Block to PLACE SHIPS
    void placeShips() {
        player1.placeShips();
        passTurn();

        player2.placeShips();
        passTurn();
    }

    // Block to FIRE
    void fire(Player fromPlayer, Player toPlayer) {
        Coordinate coordinate = fromPlayer.fire();

        if (!toPlayer.board[coordinate.y][coordinate.x].equals("~")) {
            toPlayer.board[coordinate.y][coordinate.x] = "X";
            fromPlayer.opponent_view[coordinate.y][coordinate.x] = "X";

            if (toPlayer.shipAfloat(coordinate)) {
                System.out.println("You hit a ship!");
            } else if (toPlayer.hasShips()) {
                System.out.println("You sank a ship!");
            } else {
                System.out.print("You sank the last ship. You won. Congratulations!");
                System.exit(0);
            }
        } else {
            toPlayer.board[coordinate.y][coordinate.x] = "M";
            fromPlayer.opponent_view[coordinate.y][coordinate.x] = "M";
            System.out.println("You missed!");
        }
    }

    // Block to PLAY
    void play() {
        while (player1.hasShips() || player2.hasShips()) {
            fire(player1, player2);
            passTurn();
            fire(player2, player1);
            passTurn();
        }
    }
}

// BLOCK to MAIN
public class Main {
    // START
    public static void main(String[] args) {
        Game game = new Game();
        game.placeShips();
        game.play();
    }
}
