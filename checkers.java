import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aaron on 4/11/2016.
 * <p/>
 * <p/>
 * WORKS LIKE A CHAMP! 10 OUT OF 10! PLUS BONUS FOR ALPHA BETA!
 */
public class checkers {

    static int counter = 0;
    static boolean success = false;


    /**
     * STATE CLASS
     */
    static class State {

        /**
         * COORDINATES CLASS INSIDE STATE
         */
        public class Coordinates {

            private int x, y;

            public Coordinates(int inX, int inY) {
                x = inX;
                y = inY;
            }

            public int getX() {
                return x;
            }

            public int getY() {
                return y;
            }

            public String printCoordinates() {
                return ("( " + x + ", " + y + " )");
            }

            public void setX(int inX) {
                x = inX;
            }

            public void setY(int inY) {
                y = inY;
            }
        }

        /**
         * MOVE CLASS INSIDE STATE
         */
        public class Move {
            Coordinates piece;
            Coordinates move_to;
            Coordinates opponent_piece;
            boolean piece_jumped = false;

            public Move(Coordinates selected_piece, Coordinates where_to) {
                piece = selected_piece;
                move_to = where_to;
            }

            public Move(Coordinates selected_piece, Coordinates where_to, Coordinates losing_piece) {
                piece_jumped = true;
                piece = selected_piece;
                move_to = where_to;
                opponent_piece = losing_piece;
            }

            public boolean isPiece_jumped() {
                return piece_jumped;
            }

            public Coordinates getPiece() {
                return piece;
            }

            public Coordinates getMove_to() {
                return move_to;
            }

            public Coordinates getOpponent_piece() {
                return opponent_piece;
            }
        }

        /**
         * STATES VARIABLES DECLARED BELOW
         */

        public int depth = 0;

        public int red_pieces = 0;
        public int black_pieces = 0;
        public int red_kings = 0;
        public int black_kings = 0;
        ArrayList<Move> moveArrayList = new ArrayList<Move>();
        public boolean from_jump;
        public ArrayList<State> successors = new ArrayList<State>();
        public boolean whos_turn;
        public int child_result = -1000;
        public int jumps = 0;
        private Move last_move = null;

        public char[][] Board = {{'-', '1', '2', '3', '4', '5', '6', '7', '8', '-'},
                {'1', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '-'},
                {'2', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '-'},
                {'3', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '-'},
                {'4', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '-'},
                {'5', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '-'},
                {'6', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '-'},
                {'7', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '-'},
                {'8', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', '-'},
                {'-', '-', '-', '-', '-', '-', '-', '-', '-', '-'},};

        public State() {
            Board[1][2] = 'r';
            Board[1][4] = 'r';
            Board[1][6] = 'r';
            Board[1][8] = 'r';
            Board[2][1] = 'r';
            Board[2][3] = 'r';
            Board[2][5] = 'r';
            Board[2][7] = 'r';
            Board[3][2] = 'r';
            Board[3][4] = 'r';
            Board[3][6] = 'r';
            Board[3][8] = 'r';

            Board[6][1] = 'b';
            Board[6][3] = 'b';
            Board[6][5] = 'b';
            Board[6][7] = 'b';
            Board[7][2] = 'b';
            Board[7][4] = 'b';
            Board[7][6] = 'b';
            Board[7][8] = 'b';
            Board[8][1] = 'b';
            Board[8][3] = 'b';
            Board[8][5] = 'b';
            Board[8][7] = 'b';

            whos_turn = false;
            red_pieces = 12;
            black_pieces = 12;
        }

        public State(char[][] incomingBoard, boolean fromJump, int incoming_depth, boolean turn, Move incoming_move) {

            whos_turn = turn;
            from_jump = fromJump;
            depth = incoming_depth;
            last_move = incoming_move;

            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    Board[i][j] = incomingBoard[i][j];
                }
            }

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (Board[i][j] == 'r')
                        red_pieces++;
                    if (Board[i][j] == 'b')
                        black_pieces++;
                    if (Board[i][j] == 'R') {
                        red_pieces++;
                        red_kings++;
                    }
                    if (Board[i][j] == 'B') {
                        black_pieces++;
                        black_kings++;
                    }
                }
            }

            if ((red_pieces == 0 || black_pieces == 0) && (this.depth == 0)) {
                success = true;
                if (red_pieces == 0) {
                    System.out.println("\n\nBLACK PLAYER WINS!\n\n");
                    printBoard();
                }
                if (black_pieces == 0) {
                    System.out.println("\n\nRED PLAYER WINS!\n\n");
                    printBoard();
                }
            }

            if (!success) {
                if (fromJump) {
                    jumpTrail(last_move);
                } else {
                    getAvailableMoves();
                }
            }
        }

        private void getAvailableMoves() {                                                                              //GET ALL NON JUMP BASED MOVES
            if (counter % 2 == 0 || whos_turn == false) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (Board[i][j] == 'r') {                                                                       //IF NON KING RED PIECE
                            if (Board[i + 1][j - 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 1, j - 1)));
                            }
                            if (Board[i + 1][j + 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 1, j + 1)));
                            }
                        }
                        if (Board[i][j] == 'R') {                                                                       //IF KING RED PIECE
                            if (Board[i + 1][j - 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 1, j - 1)));
                            }
                            if (Board[i + 1][j + 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 1, j + 1)));
                            }
                            if (Board[i - 1][j - 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 1, j - 1)));
                            }
                            if (Board[i - 1][j + 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 1, j + 1)));
                            }
                        } //END OF RED KING MOVES
                    } //END OF inner FOR
                } //END OF outer FOR
            } //END OF IF
            else {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (Board[i][j] == 'b') {
                            if (Board[i - 1][j - 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 1, j - 1)));
                            }
                            if (Board[i - 1][j + 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 1, j + 1)));
                            }
                        }
                        if (Board[i][j] == 'B') {
                            if (Board[i + 1][j - 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 1, j - 1)));
                            }
                            if (Board[i + 1][j + 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 1, j + 1)));
                            }
                            if (Board[i - 1][j - 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 1, j - 1)));
                            }
                            if (Board[i - 1][j + 1] == ' ') {
                                moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 1, j + 1)));
                            }
                        } //END OF BLACK KING MOVES
                    } //END OF inner FOR
                } //END OF outer FOR
            } //END OF ELSE

            getJumpMoves();
        } //END OF GET AVAILABLE MOVES FUNCTION

        private void getJumpMoves() {                                                                                  //CHECK FOR JUMP MOVES

            if (counter % 2 == 0 || whos_turn == false) {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (Board[i][j] == 'r') {                                                                       //IF NON KING RED PIECE
                            if ((Board[i + 1][j - 1] == 'b') || (Board[i + 1][j - 1] == 'B')) {                                 //IF NON KING JUMP TO LEFT
                                if (Board[i + 2][j - 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j - 2), new Coordinates(i + 1, j - 1)));
                            }
                            if ((Board[i + 1][j + 1] == 'b') || (Board[i + 1][j + 1] == 'B')) {                                 //IF NON KING JUMP TO RIGHT
                                if (Board[i + 2][j + 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j + 2), new Coordinates(i + 1, j + 1)));
                            }
                        }
                        if (Board[i][j] == 'R') {                                                                       //IF KING RED PIECE
                            if ((Board[i + 1][j - 1] == 'b') || (Board[i + 1][j - 1] == 'B')) {                                 //IF KING JUMP TO LEFT AND DOWN
                                if (Board[i + 2][j - 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j - 2), new Coordinates(i + 1, j - 1)));
                            }
                            if ((Board[i + 1][j + 1] == 'b') || (Board[i + 1][j + 1] == 'B')) {                                 //IF KING JUMP TO RIGHT AND DOWN
                                if (Board[i + 2][j + 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j + 2), new Coordinates(i + 1, j + 1)));
                            }
                            if ((Board[i - 1][j - 1] == 'b') || (Board[i - 1][j - 1] == 'B')) {                                 //IF KING JUMP TO LEFT AND UP
                                if (Board[i - 2][j - 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j - 2), new Coordinates(i - 1, j - 1)));
                            }
                            if ((Board[i - 1][j + 1] == 'b') || (Board[i - 1][j + 1] == 'B')) {                                 //IF KING JUMP TO RIGHT AND UP
                                if (Board[i - 2][j + 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j + 2), new Coordinates(i - 1, j + 1)));
                            }
                        } //END OF RED KING MOVES
                    } //END OF inner FOR
                } //END OF outer FOR
            } //END OF IF
            else {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (Board[i][j] == 'b') {
                            if ((Board[i - 1][j - 1] == 'r') || (Board[i - 1][j - 1] == 'R')) {
                                if (Board[i - 2][j - 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j - 2), new Coordinates(i - 1, j - 1)));
                            }
                            if ((Board[i - 1][j + 1] == 'r') || (Board[i - 1][j + 1] == 'R')) {
                                if (Board[i - 2][j + 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j + 2), new Coordinates(i - 1, j + 1)));
                            }
                        }
                        if (Board[i][j] == 'B') {                                                                       //IF KING BLACK PIECE
                            if ((Board[i + 1][j - 1] == 'r') || (Board[i + 1][j - 1] == 'R')) {                                 //IF KING JUMP TO LEFT AND DOWN
                                if (Board[i + 2][j - 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j - 2), new Coordinates(i + 1, j - 1)));
                            }
                            if ((Board[i + 1][j + 1] == 'r') || (Board[i + 1][j + 1] == 'R')) {                                 //IF KING JUMP TO RIGHT AND DOWN
                                if (Board[i + 2][j + 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j + 2), new Coordinates(i + 1, j + 1)));
                            }
                            if ((Board[i - 1][j - 1] == 'r') || (Board[i - 1][j - 1] == 'R')) {                                 //IF KING JUMP TO LEFT AND UP
                                if (Board[i - 2][j - 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j - 2), new Coordinates(i - 1, j - 1)));
                            }
                            if ((Board[i - 1][j + 1] == 'r') || (Board[i - 1][j + 1] == 'R')) {                                 //IF KING JUMP TO RIGHT AND UP
                                if (Board[i - 2][j + 2] == ' ')
                                    moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j + 2), new Coordinates(i - 1, j + 1)));
                            }
                        } //END OF RED KING MOVES
                    } //END OF inner FOR
                } //END OF outer FOR
            } //END OF ELSE

            printAvailableMoves();
        }

        private void jumpTrail(Move previous_move) {                                                                                  //CHECK FOR JUMP MOVES

            if (counter % 2 == 0 || whos_turn == false) {
                int i = previous_move.getMove_to().getX();
                int j = previous_move.getMove_to().getY();

                if (Board[i][j] == 'r') {                                                                       //IF NON KING RED PIECE
                    if ((Board[i + 1][j - 1] == 'b') || (Board[i + 1][j - 1] == 'B')) {                                 //IF NON KING JUMP TO LEFT
                        if (Board[i + 2][j - 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j - 2), new Coordinates(i + 1, j - 1)));
                    }
                    if ((Board[i + 1][j + 1] == 'b') || (Board[i + 1][j + 1] == 'B')) {                                 //IF NON KING JUMP TO RIGHT
                        if (Board[i + 2][j + 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j + 2), new Coordinates(i + 1, j + 1)));
                    }
                }
                if (Board[i][j] == 'R') {                                                                       //IF KING RED PIECE
                    if ((Board[i + 1][j - 1] == 'b') || (Board[i + 1][j - 1] == 'B')) {                                 //IF KING JUMP TO LEFT AND DOWN
                        if (Board[i + 2][j - 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j - 2), new Coordinates(i + 1, j - 1)));
                    }
                    if ((Board[i + 1][j + 1] == 'b') || (Board[i + 1][j + 1] == 'B')) {                                 //IF KING JUMP TO RIGHT AND DOWN
                        if (Board[i + 2][j + 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j + 2), new Coordinates(i + 1, j + 1)));
                    }
                    if ((Board[i - 1][j - 1] == 'b') || (Board[i - 1][j - 1] == 'B')) {                                 //IF KING JUMP TO LEFT AND UP
                        if (Board[i - 2][j - 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j - 2), new Coordinates(i - 1, j - 1)));
                    }
                    if ((Board[i - 1][j + 1] == 'b') || (Board[i - 1][j + 1] == 'B')) {                                 //IF KING JUMP TO RIGHT AND UP
                        if (Board[i - 2][j + 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j + 2), new Coordinates(i - 1, j + 1)));
                    }
                } //END OF RED KING MOVES
            } //END OF inner FOR
            else {
                int i = previous_move.getMove_to().getX();
                int j = previous_move.getMove_to().getY();

                if (Board[i][j] == 'b') {
                    if ((Board[i - 1][j - 1] == 'r') || (Board[i - 1][j - 1] == 'R')) {
                        if (Board[i - 2][j - 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j - 2), new Coordinates(i - 1, j - 1)));
                    }
                    if ((Board[i - 1][j + 1] == 'r') || (Board[i - 1][j + 1] == 'R')) {
                        if (Board[i - 2][j + 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j + 2), new Coordinates(i - 1, j + 1)));
                    }
                }
                if (Board[i][j] == 'B') {                                                                       //IF KING BLACK PIECE
                    if ((Board[i + 1][j - 1] == 'r') || (Board[i + 1][j - 1] == 'R')) {                                 //IF KING JUMP TO LEFT AND DOWN
                        if (Board[i + 2][j - 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j - 2), new Coordinates(i + 1, j - 1)));
                    }
                    if ((Board[i + 1][j + 1] == 'r') || (Board[i + 1][j + 1] == 'R')) {                                 //IF KING JUMP TO RIGHT AND DOWN
                        if (Board[i + 2][j + 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i + 2, j + 2), new Coordinates(i + 1, j + 1)));
                    }
                    if ((Board[i - 1][j - 1] == 'r') || (Board[i - 1][j - 1] == 'R')) {                                 //IF KING JUMP TO LEFT AND UP
                        if (Board[i - 2][j - 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j - 2), new Coordinates(i - 1, j - 1)));
                    }
                    if ((Board[i - 1][j + 1] == 'r') || (Board[i - 1][j + 1] == 'R')) {                                 //IF KING JUMP TO RIGHT AND UP
                        if (Board[i - 2][j + 2] == ' ')
                            moveArrayList.add(new Move(new Coordinates(i, j), new Coordinates(i - 2, j + 2), new Coordinates(i - 1, j + 1)));
                    }
                } //END OF RED KING MOVES
            } //END OF inner FOR
            printAvailableMoves();
        }

        private void printAvailableMoves() {

            if (moveArrayList.size() != 0) {
                boolean jumpsOnly = false;
                for (int i = 0; i < moveArrayList.size(); i++) {
                    if (moveArrayList.get(i).isPiece_jumped()) {
                        jumpsOnly = true;
                        break;
                    }
                }

                if (jumpsOnly) {
                    for (int i = moveArrayList.size() - 1; i >= 0; i--) {
                        if (moveArrayList.get(i).isPiece_jumped() == false) {
                            moveArrayList.remove(i);
                        }
                    }
                }

                if ((depth == 0)) {
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 9; j++) {
                            System.out.print(Board[i][j] + " | ");
                        }
                        System.out.println();
                    }
                    System.out.println();
                }

                if (counter % 2 == 0) {

                    System.out.println("\nRed Turn\n");

                    for (int i = 0; i < moveArrayList.size(); i++) {
                        System.out.println("Move: " + i + ": (" + moveArrayList.get(i).getPiece().getX() + ", " + moveArrayList.get(i).getPiece().getY() + ") to spot (" +
                                moveArrayList.get(i).getMove_to().getX() + ", " + moveArrayList.get(i).getMove_to().getY() + ")");
                    }
                    int choice = -1;
                    while (choice < 0 || choice > moveArrayList.size() - 1) {
                        System.out.println("Which move would like to perform? ");
                        Scanner read = new Scanner(System.in);
                        choice = read.nextInt();
                    }

                    if (Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()] == 'r') {
                        if (moveArrayList.get(choice).getMove_to().getX() == 8) {
                            Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()] = 'R';
                        }
                    }
                    if (Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()] == 'b') {
                        if (moveArrayList.get(choice).getMove_to().getX() == 1) {
                            Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()] = 'B';
                        }
                    }

                    Board[moveArrayList.get(choice).getMove_to().getX()][moveArrayList.get(choice).getMove_to().getY()] =
                            Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()];
                    Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()] = ' ';
                    if (moveArrayList.get(choice).isPiece_jumped()) {
                        Board[moveArrayList.get(choice).getOpponent_piece().getX()][moveArrayList.get(choice).getOpponent_piece().getY()] = ' ';
                    }

                    if (jumpsOnly) {
                        char[][] tempBoard = copyBoard();
                        State tempState = new State(tempBoard, true, this.depth, whos_turn, moveArrayList.get(choice));
                        Board = tempState.copyBoard();
                    }

                } else if (counter % 2 == 1) {

                    if (depth <= 3) {
                        char[][] tempBoard;
                        for (int i = 0; i < moveArrayList.size(); i++) {
                            tempBoard = copyBoard();

                            if (tempBoard[moveArrayList.get(i).getPiece().getX()][moveArrayList.get(i).getPiece().getY()] == 'r') {
                                if (moveArrayList.get(i).getMove_to().getX() == 8) {
                                    tempBoard[moveArrayList.get(i).getPiece().getX()][moveArrayList.get(i).getPiece().getY()] = 'R';
                                }
                            }
                            if (tempBoard[moveArrayList.get(i).getPiece().getX()][moveArrayList.get(i).getPiece().getY()] == 'b') {
                                if (moveArrayList.get(i).getMove_to().getX() == 1) {
                                    tempBoard[moveArrayList.get(i).getPiece().getX()][moveArrayList.get(i).getPiece().getY()] = 'B';
                                }
                            }

                            tempBoard[moveArrayList.get(i).getMove_to().getX()][moveArrayList.get(i).getMove_to().getY()] =
                                    tempBoard[moveArrayList.get(i).getPiece().getX()][moveArrayList.get(i).getPiece().getY()];
                            tempBoard[moveArrayList.get(i).getPiece().getX()][moveArrayList.get(i).getPiece().getY()] = ' ';
                            if (moveArrayList.get(i).isPiece_jumped()) {
                                tempBoard[moveArrayList.get(i).getOpponent_piece().getX()][moveArrayList.get(i).getOpponent_piece().getY()] = ' ';
                            }

                            if (jumpsOnly && (moveArrayList.size() != 0)) {
                                if (whos_turn == false) {
                                    jumps = jumps - 3;
                                } else {
                                    jumps = jumps + 3;
                                }

                                successors.add(new State(tempBoard, true, depth + 1, whos_turn, moveArrayList.get(i)));

                            } else
                                successors.add(new State(tempBoard, false, depth + 1, !whos_turn, null));
                        }
                    }

                    if (depth == 0) {

                        int choice = alpha_beta(this);

                        for (int i = 0; i < successors.size(); i++) {
                            if (successors.get(i).child_result == choice) {
                                choice = i;
                            }
                        }

                        System.out.println("\nBlack Turn\n");

                        for (int i = 0; i < moveArrayList.size(); i++) {
                            System.out.println("Move: " + i + ": (" + moveArrayList.get(i).getPiece().getX() + ", " + moveArrayList.get(i).getPiece().getY() + ") to spot (" +
                                    moveArrayList.get(i).getMove_to().getX() + ", " + moveArrayList.get(i).getMove_to().getY() + ")");
                        }

                        try {
                            TimeUnit.SECONDS.sleep(1);
                            System.out.println("Computer is thinking... \n\n");
                        } catch (Exception e) {

                        }

                        if (Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()] == 'r') {
                            if (moveArrayList.get(choice).getMove_to().getX() == 8) {
                                Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()] = 'R';
                            }
                        }
                        if (Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()] == 'b') {
                            if (moveArrayList.get(choice).getMove_to().getX() == 1) {
                                Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()] = 'B';
                            }
                        }

                        Board[moveArrayList.get(choice).getMove_to().getX()][moveArrayList.get(choice).getMove_to().getY()] =
                                Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()];
                        Board[moveArrayList.get(choice).getPiece().getX()][moveArrayList.get(choice).getPiece().getY()] = ' ';
                        if (moveArrayList.get(choice).isPiece_jumped()) {
                            Board[moveArrayList.get(choice).getOpponent_piece().getX()][moveArrayList.get(choice).getOpponent_piece().getY()] = ' ';
                        }

                        if (jumpsOnly) {
                            char[][] tempBoard = copyBoard();
                            State tempState = new State(tempBoard, true, depth, whos_turn, moveArrayList.get(choice));
                            Board = tempState.copyBoard();
                        }

                    }

                    /**
                     *
                     * BREAK GLASS IN CASE OF BUGS!
                     *
                     * DELETE EVERYTHING UP TO THE ELSE IF AND UNCOMMENT EVERYTHING BELOW
                     * IF ALL CRAP BREAKS LOOSE.
                     *
                     * */
                }
            }

            if (depth == 0 && from_jump == false)
                counter++;

        }

        public int result() {

            int result = 0;
            int kings = black_kings - red_kings;
            int pieces = black_pieces - red_pieces;
            int jump = jumps;

            result = kings + pieces + jump;
            return result;
        }

        public void printBoard() {

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    System.out.print(Board[i][j] + " | ");
                }
                System.out.println();
            }
            System.out.println();
        }

        public char[][] copyBoard() {
            char[][] copied_board = new char[10][10];
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    copied_board[i][j] = this.Board[i][j];
                }
            }
            return copied_board;
        }

        /**
         * ALPHA BETA
         */

        public int alpha_beta(State incoming) {
            int v = max_value(incoming, -1000, 1000);
            return v;
        }

        public int max_value(State incoming, int alpha, int beta) {
            if ((incoming.depth == 3) || (incoming.successors.size() == 0)) {
                //System.out.println(incoming.result() + " MAX");
                return incoming.result();
            }

            int v = -1000;
            int temp = -1000;

            for (int i = 0; i < incoming.successors.size(); i++) {
                v = Math.max(v, min_value(incoming.successors.get(i), alpha, beta));
                if (temp < v) {
                    incoming.successors.get(i).child_result = v;
                }

                if (v >= beta) {
                    return v;
                }
                alpha = Math.max(alpha, v);
            }
            return v;
        }

        public int min_value(State incoming, int alpha, int beta) {
            if ((incoming.depth == 3) || (incoming.successors.size() == 0)) {
                //System.out.println(incoming.result() + " MIN");
                return incoming.result();
            }

            int v = 1000;

            for (int i = 0; i < incoming.successors.size(); i++) {
                //incoming.successors.get(i).printBoard();
                v = Math.min(v, max_value(incoming.successors.get(i), alpha, beta));
                if (v <= alpha) {
                    return v;
                }
                beta = Math.min(beta, v);
                return v;
            }
            return v;
        }

        /**END ALPHA BETA*/


    }

    public static void main(String[] args) {
        State initial = new State();
        boolean turn;

        while (!success) {
            if (counter % 2 == 0) {
                turn = false;
            } else
                turn = true;

            initial = new State(initial.Board, false, 0, turn, null);
        }
    }

}
