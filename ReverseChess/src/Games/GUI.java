package Games;

import StandardChess.Coordinate;
import StandardChess.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class GUI {

    private static final String FILE_PATH = "C:/Project/UnPassant/ReverseChess/resources/";
    private static final Map<String, Image> ICONS = new TreeMap<>();
    private static GUILogicInterface guiInterface;
    private static Coordinate origin = null;
    private static Coordinate target = null;
    private static final int SQUARE_SIZE = 64;
    private static ChessPanel chessPanel;
    private static JTextField fenField;

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        frame.setBounds(10, 10, 600, 700);
        guiInterface = new GUILogicInterface(args.length == 0 ? "" : args[0]);
        ChessPanel panel = new ChessPanel(guiInterface);
        chessPanel = panel;

        panel.setMaximumSize(new Dimension(SQUARE_SIZE * 8, SQUARE_SIZE * 8));
        PieceListener pieceListener = new PieceListener();
        panel.addMouseListener(pieceListener);
        panel.addMouseMotionListener(pieceListener);
        frame.add(panel);

        fenField = new JTextField();
        fenField.setMaximumSize(new Dimension(SQUARE_SIZE * 6, 32));
        fenField.addActionListener(new FENUpdate());
        frame.add(fenField);

        JButton undo = new JButton();
        undo.setMaximumSize(new Dimension(32, 15));
        undo.addActionListener(new Undo());

        JButton redo = new JButton();
        redo.setMaximumSize(new Dimension(32, 15));
        redo.addActionListener(new Redo());

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(panel)
//                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(undo)
                                .addComponent(redo)
                        )
                        .addComponent(fenField)
        );
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(panel)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(undo)
                                        .addComponent(redo)
                                )
                                .addComponent(fenField)
                        )


        );
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

    public static void update() {
        chessPanel.repaint();
    }

    private static Image getImage(Piece piece) {
        String pieceName = piece.getColour() + piece.getType();
        if (ICONS.containsKey(pieceName)) {
            return ICONS.get(pieceName);
        }
        String fileName = piece.getColour().toUpperCase().charAt(0)
                + piece.getType().substring(0,1).toUpperCase()
                + piece.getType().toLowerCase().substring(1)
                + ".png";
        try {
            Image icon =  ImageIO.read(new File(FILE_PATH
                    + fileName));
            ICONS.put(pieceName, icon);
            return icon;
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
            guiInterface.makeMove(origin, target);
            origin = null;
            target = null;
        }

    }

    public static class Redo extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            guiInterface.redo();
        }
    }

    public static class Undo extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            guiInterface.undo();
        }
    }

    public static class FENUpdate extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            guiInterface.setFEN(fenField.getText());
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
        private GUILogicInterface guiInterface;
        private boolean setUp = true;

        public ChessPanel(GUILogicInterface guiInterface) {
            this.guiInterface = guiInterface;
        }

//        public void setBoard(ChessBoard board) {
//            this.board = board;
//        }

        @Override
        public void paint(Graphics g) {
            Image boardIcon;
            if (!ICONS.containsKey("board")) {
                try {
                    boardIcon = ImageIO.read(new File(FILE_PATH + "Board.png"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                boardIcon = ICONS.get("board");
            }
            g.drawImage(boardIcon, 0, 0,
                    8 * SQUARE_SIZE, 8 * SQUARE_SIZE,
                    this);
            long start = System.nanoTime();

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    int xOffset = i * SQUARE_SIZE;
                    int yOffset = (7 - j) * SQUARE_SIZE;
                    Piece piece = guiInterface.at(new Coordinate(i, j));
                    if (!guiInterface.at(new Coordinate(i, j)).getType().equals("null")) {
                        g.drawImage(getImage(piece), xOffset, yOffset, SQUARE_SIZE, SQUARE_SIZE, this);
                    }
                }
            }
        }
    }

}
