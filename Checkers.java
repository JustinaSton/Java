import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class Checkers extends JPanel {

    public static void main(String[] args) {
        JFrame window = new JFrame("Checkers");
        Checkers content = new Checkers();
        window.setContentPane(content);
        window.pack();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        window.setLocation( (screensize.width - window.getWidth())/2,
                (screensize.height - window.getHeight())/2 );
        window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        window.setVisible(true);
    }

    private JLabel message;

    public Checkers() {

        setLayout(null);
        setPreferredSize( new Dimension(500,400) );
        setBackground(Color.black);
        Board board = new Board();
        add(board);
        add(message);
        board.setBounds(160,100,164,164);
        message.setBounds(65, 50, 350, 30);

    }

   private class Board extends JPanel implements MouseListener {

       Checkers.CheckersData board;
       int currentPlayer;
       int selectedRow, selectedCol;
       CheckersMove[] possibleMoves;
       Board() {
           setBackground(Color.BLACK);
           addMouseListener(this);
           message = new JLabel("",JLabel.CENTER);
           message.setFont(new Font("Comic Sans MS", Font.HANGING_BASELINE, 20));
           message.setForeground(Color.white);
           board = new Checkers.CheckersData();
           startNewGame();
       }

       void startNewGame() {
           board.setUpGame();
           currentPlayer = Checkers.CheckersData.RED;
           possibleMoves = board.getPossibleMoves(Checkers.CheckersData.RED);
           selectedRow = -1;
           message.setText("RED - your turn.");
           repaint();
       }

       void ClickSquare(int row, int col) {

           for (int i = 0; i < possibleMoves.length; i++)
           {
               if (possibleMoves[i].startRow == row && possibleMoves[i].startCol == col) {
                   selectedRow = row;
                   selectedCol = col;
                   if (currentPlayer == Checkers.CheckersData.RED)
                       message.setText("RED - your turn.");
                   else
                       message.setText("BLACK - your turn.");
                   repaint();
                   return;
               }
           }

           if (selectedRow < 0) {
               message.setText("Click the checker you want to move.");
               return;
           }

           for (int i = 0; i < possibleMoves.length; i++)
               if (possibleMoves[i].goToRow == row && possibleMoves[i].goToCol == col) {
                   doMoveChecker(possibleMoves[i]);
                   return;
               }
           message.setText("Click the square you want to go to.");
       }

       void doMoveChecker(CheckersMove move) {

           board.moveChecker(move);
           if (move.isJump()) {
               possibleMoves = board.getPossibleJumpsFrom(currentPlayer,move.goToRow,move.goToCol);
               if (possibleMoves != null) {
                   if (currentPlayer == Checkers.CheckersData.RED)
                       message.setText("RED - you must jump again.");
                   else
                       message.setText("BLACK - you must jump again.");
                   selectedRow = move.goToRow;
                   selectedCol = move.goToCol;
                   repaint();
                   return;
               }
           }

           if (currentPlayer == Checkers.CheckersData.RED) currentPlayer = Checkers.CheckersData.BLACK;
           else currentPlayer = Checkers.CheckersData.RED;
           possibleMoves = board.getPossibleMoves(currentPlayer);
           if (possibleMoves == null)
           {
               if(currentPlayer == Checkers.CheckersData.RED)  message.setText("BLACK wins!");
               else message.setText("RED wins!");
           }
               if (possibleMoves[0].isJump())
                   message.setText("Opponent - you must jump.");
               else
                   message.setText("Opponent - make your move.");

           selectedRow = -1;
           repaint();

       }
       public void paintComponent(Graphics g) {
           Color lightBrown = new Color(255, 229, 204);
           Color darkerBrown = new Color(255, 178, 102);
           for (int row = 0; row < 8; row++) {
               for (int col = 0; col < 8; col++) {
                   if ( row % 2 == col % 2 )
                       g.setColor(lightBrown);
                   else
                       g.setColor(darkerBrown);
                   g.fillRect(2+ col*20, 2+ row*20, 20, 20);
                   switch (board.pieceAt(row,col)) {
                       case Checkers.CheckersData.RED:
                           g.setColor(Color.RED);
                           g.fillOval(4+ col*20, 4+ row*20, 15, 15);
                           break;
                       case Checkers.CheckersData.BLACK:
                           g.setColor(Color.BLACK);
                           g.fillOval(4 + col*20, 4 + row*20, 15, 15);
                           break;
                   }
               }
           }
       }
       public void mousePressed(MouseEvent evt) {
          int col = (evt.getX()) / 20;
          int row = (evt.getY()) / 20;
           if (col >= 0 && col < 8 && row >= 0 && row < 8)
               ClickSquare(row,col);
       }

       public void mouseReleased(MouseEvent evt) { }
       public void mouseClicked(MouseEvent evt) { }
       public void mouseEntered(MouseEvent evt) { }
       public void mouseExited(MouseEvent evt) { }
   }

    public class CheckersMove {
        int startRow, startCol;
        int goToRow, goToCol;
        CheckersMove(int r1, int c1, int r2, int c2) {
            startRow = r1;
            startCol = c1;
            goToRow = r2;
            goToCol = c2;
        }
        boolean isJump() {  // naudojama tuo atveju, jei kertame saske (reikia pereiti dviem langeliais i virsu arba apaciai)
            return (startRow - goToRow == 2 || startRow - goToRow == -2);
        }
    }

    class CheckersData {
        static final int
                EMPTY = 0,
                RED = 1,
                BLACK = 2;

        int[][] board;

        CheckersData() {
            board = new int[8][8];
            setUpGame();
        }

        void setUpGame() {
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if ( row % 2 == col % 2 ) {
                        if (row < 3)
                            board[row][col] = BLACK;
                        else if (row > 4)
                            board[row][col] = RED;
                        else
                            board[row][col] = EMPTY;
                    }
                    else {
                        board[row][col] = EMPTY;
                    }
                }
            }
        }

        int pieceAt(int row, int col) {
            return board[row][col];
        }

        void moveChecker(CheckersMove move) {
            moveChecker(move.startRow, move.startCol, move.goToRow, move.goToCol);
        }

        void moveChecker(int startRow, int startCol, int goToRow, int goToCol) {
            board[goToRow][goToCol] = board[startRow][startCol];
            board[startRow][startCol] = EMPTY;
            if (startRow - goToRow == 2 || startRow - goToRow == -2) {
                int jumpRow = (startRow + goToRow) / 2;
                int jumpCol = (startCol + goToCol) / 2;
                board[jumpRow][jumpCol] = EMPTY;
            }
        }
        CheckersMove[] getPossibleMoves(int player) {

            ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();

            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                        if (board[row][col] == player) {
                        if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                            moves.add(new CheckersMove(row, col, row+2, col+2));
                        if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                            moves.add(new CheckersMove(row, col, row-2, col+2));
                        if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                            moves.add(new CheckersMove(row, col, row+2, col-2));
                        if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                            moves.add(new CheckersMove(row, col, row-2, col-2));
                    }
                }
            }

            if (moves.size() == 0) {
                for (int row = 0; row < 8; row++) {
                    for (int col = 0; col < 8; col++) {
                            if (board[row][col] == player){
                            if (canMove(player,row,col,row+1,col+1))
                                moves.add(new CheckersMove(row,col,row+1,col+1));
                            if (canMove(player,row,col,row-1,col+1))
                                moves.add(new CheckersMove(row,col,row-1,col+1));
                            if (canMove(player,row,col,row+1,col-1))
                                moves.add(new CheckersMove(row,col,row+1,col-1));
                            if (canMove(player,row,col,row-1,col-1))
                                moves.add(new CheckersMove(row,col,row-1,col-1));
                        }
                    }
                }
            }

            if (moves.size() == 0)
                return null;
            else {
                CheckersMove[] moveArray = new CheckersMove[moves.size()];
                for (int i = 0; i < moves.size(); i++)
                    moveArray[i] = moves.get(i);
                return moveArray;
            }
        }

        CheckersMove[] getPossibleJumpsFrom(int player, int row, int col) {

            ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();
            if (board[row][col] == player)
            {
                if (canJump(player, row, col, row+1, col+1, row+2, col+2))
                    moves.add(new CheckersMove(row, col, row+2, col+2));
                if (canJump(player, row, col, row-1, col+1, row-2, col+2))
                    moves.add(new CheckersMove(row, col, row-2, col+2));
                if (canJump(player, row, col, row+1, col-1, row+2, col-2))
                    moves.add(new CheckersMove(row, col, row+2, col-2));
                if (canJump(player, row, col, row-1, col-1, row-2, col-2))
                    moves.add(new CheckersMove(row, col, row-2, col-2));
            }
            if (moves.size() == 0)
                return null;
            else {
                CheckersMove[] moveArray = new CheckersMove[moves.size()];
                for (int i = 0; i < moves.size(); i++)
                    moveArray[i] = moves.get(i);
                return moveArray;
            }
        }

        private boolean canJump(int player, int r1, int c1, int r2, int c2, int r3, int c3) {

            if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8)
                return false;

            if (board[r3][c3] != EMPTY)
                return false;

            if (player == RED) {
                if (board[r1][c1] == RED && r3 > r1)
                    return false;
                if (board[r2][c2] != BLACK)
                    return false;
                return true;
            }
            else {
                if (board[r1][c1] == BLACK && r3 < r1)
                    return false;
                if (board[r2][c2] != RED )
                    return false;
                return true;
            }

        }

        private boolean canMove(int player, int r1, int c1, int r2, int c2) {

            if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8)
                return false;

            if (board[r2][c2] != EMPTY)
                return false;

            if (player == RED) {
                if (board[r1][c1] == RED && r2 > r1)
                    return false;
                return true;
            }
            else {
                if (board[r1][c1] == BLACK && r2 < r1)
                    return false;
                return true;
            }
        }
    }
}