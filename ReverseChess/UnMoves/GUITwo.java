import StandardChess.BoardBuilder;
import StandardChess.ChessBoard;
import StandardChess.Coordinate;
import StandardChess.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.function.BiConsumer;

public class GUITwo {

    private static final String FILE_PATH = "C:/Project/UnPassant/ReverseChess/resources/";
    private static Coordinate origin = null;
    private static Coordinate target = null;
    private static final int SQUARE_SIZE = 64;
    private static UnMoveMaker unMoveMaker;
    private static ChessPanel chessPanel;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setBounds(10, 10, 600, 600);
        ChessBoard board = args.length == 0 ? BoardBuilder.buildBoard() : BoardBuilder.buildBoard(args[0]);
        unMoveMaker = new UnMoveMaker(board);
        ChessPanel panel = new ChessPanel(board);
        chessPanel = panel;

        panel.setBounds(0, 0,
                SQUARE_SIZE * 8, SQUARE_SIZE * 8);

        frame.add(panel);
        PieceListener pieceListener = new PieceListener();
        panel.addMouseListener(pieceListener);
        panel.addMouseMotionListener(pieceListener);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    private static Image getImage(Piece piece) {
        String fileName = piece.getColour().toUpperCase().charAt(0)
                + piece.getType().substring(0,1).toUpperCase()
                + piece.getType().toLowerCase().substring(1)
                + ".png";
        try {
            return ImageIO.read(new File(FILE_PATH
                    + fileName));
        } catch (IOException e) {
            throw new RuntimeException(fileName, e);
        }
    }

    private static void set(int x, int y) {
        Coordinate newCoordinate = new Coordinate(x / SQUARE_SIZE, 7 - y / SQUARE_SIZE);
        if (origin == null){
            origin = newCoordinate;
        } else {
            target = newCoordinate;
        }
        if (origin.equals(target)) {
            origin = null;
            target = null;
        } else if (target != null) {
            if (unMoveMaker.makeUnMove(origin, target)) {
                chessPanel.repaint();
            }
            origin = null;
            target = null;
        }

    }

    public static class PieceListener implements MouseListener, MouseMotionListener {
        private boolean dragged = false;
        private boolean clicked = false;
        @Override
        public void mouseClicked(MouseEvent e) {
            clicked = !clicked;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            set(e.getX(), e.getY());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (dragged) {
                set(e.getX(), e.getY());
                dragged = false;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!clicked) {
                dragged = true;
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

    public static class ChessPanel extends JPanel {
        private ChessBoard board;
        private boolean setUp = true;

        public ChessPanel(ChessBoard board) {
            this.board = board;
        }

        public void setBoard(ChessBoard board) {
            this.board = board;
        }

        @Override
        public void paint(Graphics g) {
//            if (setUp) {
                try {
                    g.drawImage(ImageIO.read(new File(FILE_PATH + "Board.png")), 0, 0,
                            8 * SQUARE_SIZE, 8 * SQUARE_SIZE,
                            this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//            }
            long start = System.nanoTime();

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    int xOffset = i * SQUARE_SIZE;
                    int yOffset = (7 - j) * SQUARE_SIZE;
                    Piece piece = board.at(new Coordinate(i, j));
                    if (!board.at(new Coordinate(i, j)).getType().equals("null")) {
                        g.drawImage(getImage(piece), xOffset, yOffset, SQUARE_SIZE, SQUARE_SIZE, this);
                    }
                }
            }
            System.out.println(System.nanoTime() - start);
        }
//        @Override
//        public void repaint() {
//            setUp = false;
//            super.repaint();
//        }


    }

}
