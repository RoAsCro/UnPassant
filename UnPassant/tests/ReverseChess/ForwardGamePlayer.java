package ReverseChess;

import ReverseChess.StandardChess.BoardReader;
import ReverseChess.StandardChess.ChessBoard;
import ReverseChess.StandardChess.Coordinate;
import ReverseChess.StandardChess.Coordinates;

import java.util.List;

public class ForwardGamePlayer {

    private static int ASCII_J = 108;
    private static int ASCII_A = 97;

    private ChessBoard board;

    private List<Coordinate> rook = List.of(Coordinates.UP, Coordinates.DOWN, Coordinates.LEFT, Coordinates.RIGHT);
    private List<Coordinate> bishop = List.of(Coordinates.UP_LEFT, Coordinates.UP_RIGHT, Coordinates.DOWN_RIGHT, Coordinates.DOWN_LEFT);
    private List<Coordinate> queen = List.of(Coordinates.UP, Coordinates.DOWN, Coordinates.LEFT, Coordinates.RIGHT,
            Coordinates.UP_LEFT, Coordinates.UP_RIGHT, Coordinates.DOWN_RIGHT, Coordinates.DOWN_LEFT);
    private static final List<Coordinate> KNIGHT_DIRECTIONS = List.of( new Coordinate(1, 2),
            new Coordinate(1, -2), new Coordinate(-1, 2), new Coordinate(-1, -2),
            new Coordinate(2, 1), new Coordinate(-2, 1), new Coordinate(2, -1),
            new Coordinate(-2, -1));


    public boolean gameDecode(String game, ChessBoard board) {
        game = game.substring(2);
        while(true) {
            String piece = "P";
            boolean capture = false;
            int y = 0;
            int x = 0;

            char current = game.charAt(0);
            while (current != ' ') {
                if (current == 'x') {
                    capture = true;
                    game = game.substring(1);
                } else if (current > ASCII_J && current != 'O') {
                    piece = "" + current;
                    game = game.substring(1);
                }
                else if (Character.isDigit(current)) {
                    y = (int) (current - 48);
                }
                if (current <= ASCII_J && current >= ASCII_A) {
                    x = (int) (current - ASCII_A);
                }
                current = game.charAt(0);
            }

        }
    }

    private Coordinate[] getOrigin(String piece, String targetX, String targetY, boolean capture) {
        Coordinate[] coordinates;
        int x = (targetX.charAt(0) - ASCII_A);
        int y = Integer.parseInt(targetY);
        Coordinate target = new Coordinate(x, y);

        switch (piece) {
            case ("P") -> {
                coordinates = new Coordinate[]{new Coordinate(x, y - 1), new Coordinate(x, y - 2),
                        new Coordinate(capture ? x - 1 : x, y - 1), new Coordinate(capture ? x + 1 : x, y - 1)};
                return coordinates;
            }
            case ("R") -> {
                coordinates = iterator(target, rook);
                return coordinates;
            }
            case ("B") -> {
                coordinates = iterator(target, bishop);
                return coordinates;
            }
            case ("Q") -> {
                coordinates = iterator(target, queen);
                return coordinates;
            }
            case ("N") -> {
                coordinates = knightIterator(target);
                return coordinates;
            }
            default -> {
                coordinates = kingDirections(target);
                return  coordinates;
            }
        }
    }

    private Coordinate[] kingDirections(Coordinate target) {
        Coordinate[] coordinates = new Coordinate[queen.size()];
        for (int i = 0 ; i < queen.size() ; i++) {
            coordinates[i] = Coordinates.add(target, queen.get(i));
        }
        return coordinates;
    }

    private Coordinate directionChecker(Coordinate kingLocation, Coordinate currentDirection) {
        BoardReader reader = this.board.getReader();
        reader.to(Coordinates.add(kingLocation, currentDirection));

        reader.nextWhile(currentDirection,
                c -> reader.hasNext(currentDirection)
                        && this.board.at(reader.getCoord()).getType().equals("null")
        );
        return reader.getCoord();
    }

    private Coordinate[] iterator(Coordinate target, List<Coordinate> directions) {
        Coordinate[] coordinates = new Coordinate[directions.size()];
        for (int i = 0 ; i < directions.size() ; i++) {
            coordinates[i] = directionChecker(target, directions.get(i));
        }
        return coordinates;
    }

    private Coordinate[] knightIterator(Coordinate target) {
        Coordinate[] coordinates = new Coordinate[KNIGHT_DIRECTIONS.size()];
        for (int i = 0 ; i < KNIGHT_DIRECTIONS.size() ; i++) {
            coordinates[i] = knightDirection(target, KNIGHT_DIRECTIONS.get(i));
        }
        return coordinates;
    }

    private Coordinate knightDirection(Coordinate kingLocation, Coordinate currentDirection) {
        BoardReader reader = this.board.getReader();
        reader.to(Coordinates.add(kingLocation, currentDirection));
        return reader.getCoord();
    }


}
