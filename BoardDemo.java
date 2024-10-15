
import java.io.InputStream;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import  java.util.Map;
import  java.util.Hashtable;
public class BoardDemo {
    public static int iscomp = 1; // auto playing second
    private static boolean isdebug = false;
    private static boolean isplay = true;

    public static int[][] eval_board;
    public static void main(String[] args) throws InterruptedException {
        // Asking basic entry set-up questions:
        Scanner scan = new Scanner(System.in);
        while(isplay && !isdebug) {

            System.out.print("Run part A, B, or C?: \n");
            String ai_search = scan.nextLine();

            System.out.print("Include debugging info? (y for Yes n for no):  \n");
            String debug = scan.nextLine();
            isdebug = debug.contains("y");

            System.out.print("Enter Rows: \n");
            int row = Integer.parseInt(scan.nextLine());

            System.out.print("Enter Cols: \n");
            int cols = Integer.parseInt(scan.nextLine());

            System.out.print("Enter number in a row to win: \n");
            int to_win_num = Integer.parseInt(scan.nextLine());

            // initializing the board with users requirements
            Board board = new Board(row, cols, to_win_num);
            //Board board = new Board( 4,4,4);
            Map<Board, Minmaxinfo> table = new Hashtable<Board, Minmaxinfo>();

            //getting how long it takes for program to run
            long startT = System.currentTimeMillis();
            //List of search algs: (MinMax, AlphaBeta, Alphabeta with Heauristics)
            Minmaxinfo winner;
            if (ai_search.equals("a")) {
                winner = MinimaxSearch.MinMax_Search(board, table);
            } else if (ai_search.equals("b")) {
                winner = AlphabetaSearch.AlphaBetasearch(board, Integer.MIN_VALUE, Integer.MAX_VALUE, table);
            } else {
                eval_board = AlphaBeteHeuristic.asign_valPositions(board);
                AlphaBeteHeuristic.to2DString(board, eval_board);
                winner = AlphaBeteHeuristic.AB_heuristic_search(board, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, table);
            }

            long end = System.currentTimeMillis();
            Double total_t = (end - startT) * 0.001;
            System.out.printf("Search Completed in %f \n", total_t);

            // Printing alg info:
            System.out.printf("Transposition table has %d states \n", table.size());
            if (ai_search.equals("b")) {
                System.out.printf("The tree was pruned %d times  \n", AlphabetaSearch.num_pruned);
            }
            System.out.printf("%s player has a garaunteed win with perfect play. \n", who_wins(winner));

            //print debugging information after collecting all the info and if user wants it
            if (isdebug) {
                print_table(table);
            }

            //only plays if debugger is off
            else{
                System.out.print("Who plays first? 1 = human 2 = comp: \n");
                int iscomp_first = Integer.parseInt(scan.next());
                if (iscomp_first == 2) {
                    iscomp = 0;
                } //will be using the num of moves to flip back and forth


                while (board.getGameState() == GameState.IN_PROGRESS) {
                    // current status of the board
                    System.out.println(board.to2DString());

                    // if the prior move was suboptimal and was pruned will rerun code
                    if (ai_search.equals("b") && !table.containsKey(board)) {
                        System.out.print("This is a state that was previously pruned; re-running alpha beta from here.\n");
                        Map<Board, Minmaxinfo> new_table = new Hashtable<Board, Minmaxinfo>();
                        Minmaxinfo info = AlphabetaSearch.AlphaBetasearch(board, Integer.MIN_VALUE, Integer.MAX_VALUE, new_table);
                        table = new_table;
                        table.put(board, info);

                    } else if (ai_search.equals("c")) {
                        Map<Board, Minmaxinfo> new_table = new Hashtable<Board, Minmaxinfo>();
                        Minmaxinfo info = AlphaBeteHeuristic.AB_heuristic_search(board, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, new_table);
                        //table = new_table;
                        table.put(board, info);
                    }

                    System.out.printf(" Minimax value for this state: [%d], optimal move: [%d] \n", table.get(board).getValue(), table.get(board).getAction());


                    // MAX (X) makes a move:
                    if (board.getPlayerToMoveNext().equals(Player.MAX)) {
                        System.out.print("It is MAX's turn! \n");
                    }
                    // MIN (O) makes a move:
                    else {
                        System.out.print("It is MIN's turn! \n");
                    }

                    //computer is choosing the val
                    if (board.getNumberOfMoves() % 2 == iscomp) {
                        System.out.printf("Computer chooses move: %d \n", table.get(board).getAction());
                        board = board.makeMove(table.get(board).getAction());

                        // causing some delay for better user experienec:
                        TimeUnit.SECONDS.sleep(2);
                    }
                    //player is choosing the move
                    else {
                        System.out.print("Enter Move: \n");
                        int move = Integer.parseInt(scan.next());
                        board = board.makeMove(move);

                    }


                }
                System.out.println("Game Over!");
                System.out.println(board.to2DString());
                System.out.println("State of the game: " + board.getGameState());
                System.out.println("Play again? (yes or no): ");
                String play = scan.nextLine();
                if (play.contains("n")) {
                    isplay= false;
                }

            }
        }

    }

    /*
    helper function for deciding who wins
     */
    public static String who_wins(Minmaxinfo info){
        int val = info.getValue();
        if (val> 0){
            return "First";
        }
        else if( val<0){
            return "Second";
        }
        else{
            return "Neither";
        }
    }

    /*
    prints debugging information by taking all the keys in the table and putting them in
    a set and prints out the board state and all the other values by accessing the table.
    We want to put them in a set because it'll print out the address of the minimaxinfo object
     */
    public static void print_table(Map<Board, Minmaxinfo> table){
        Set<Board> boards = table.keySet();
        for( Board board: boards){
            String board_string = board.toString();
            Minmaxinfo info = table.get(board);
            System.out.printf("%s ->  Minimax:[ value=%d , action = %d] \n", board_string, info.value, info.action);

        }

    }



}
