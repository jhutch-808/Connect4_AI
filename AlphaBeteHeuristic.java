/*
Name: Julia Hutchison
      Date: 10/24/2024
      Class: CS372
      Pledge: I have neither given nor received unauthorized aid on this program.
      Description: This is a search algorithm to help plaers get the best chance of winning at connect 4
      This particular one uses alpha beta pruning with heuristics
 */
import java.util.ArrayList;
import java.util.Map;


public class AlphaBeteHeuristic extends MinimaxSearch{

    public static int num_pruned = 0;

    public static Minmaxinfo AB_heuristic_search(Board state, Integer alpha, Integer beta, Integer depth, Map<Board, Minmaxinfo> t_table){
        //if the state is already in the table
        if(t_table.containsKey(state)){
            return t_table.get(state);
        }

        //found a terminal state so putting that state in the table
        else if (!state.getGameState().equals(GameState.IN_PROGRESS)){
            int util = UTILITY(state);
            Minmaxinfo info = new Minmaxinfo(util, null);
            t_table.put(state,info);
            return info;
        }
        //at the depth of 4 so evaluating the state of the board to see
        else if(depth == 6){
            Integer h = EVAL(state);
            //System.out.printf("at depth. eval : %d", h);
            Minmaxinfo info = new Minmaxinfo(h, null);
            t_table.put(state,info);
            return info;
        }

        //Max turn to explore:
        else if( state.getPlayerToMoveNext().equals(Player.MAX)){
            int v = Integer.MIN_VALUE;
            Integer best_move = null;
            ArrayList<Integer> Actions = Action(state);
            for (Integer action: Actions){
                Board child_state = state.makeMove(action);
                Minmaxinfo child_info = AB_heuristic_search(child_state, alpha, beta, depth+1, t_table);
                int v2 = child_info.getValue();
                if (v2>v){
                    v = v2;
                    best_move = action ;
                    alpha = Math.max(alpha, v);
                }
                if (v >= beta){ // prune tree & dont store the state in the table
                    num_pruned++;
                    return new Minmaxinfo(v, best_move);
                }
            }
            Minmaxinfo info = new Minmaxinfo(v, best_move);
            t_table.put(state,info);
            return info;
        }
        // mins turn to explore
        else {
            int v = Integer.MAX_VALUE;
            Integer best_move = null;
            ArrayList<Integer> Actions = Action(state);
            for (Integer action : Actions) {
                Board child_state = state.makeMove(action);
                Minmaxinfo child_info = AB_heuristic_search(child_state, alpha, beta, depth +1, t_table);
                int v2 = child_info.getValue();
                if (v2 < v) {
                    v = v2;
                    best_move = action;
                    beta= Math.min(beta, v);
                }
                if (v <= alpha) { // prune tree & dont store the state in the table
                    num_pruned ++;
                    return new Minmaxinfo(v, best_move);
                }
            }
            Minmaxinfo info = new Minmaxinfo(v, best_move);
            t_table.put(state, info);
            return info;
        }
    }

    /*
    evalues the state and calucates the heuristic
     */

    public static int EVAL(Board state){
        //for each position on the board add a value to it.
        //h = positional val + connecting pieces - (other players pieces are connecting)
        // for every users's peice add the position val to the total heauristic
        int player_val = (state.getPlayerToMoveNext() == Player.MIN) ? -1:1;

        int pos_val = calc_positional_val(player_val, state);

       int num_of_con =  calc_num_of_connects(player_val, state);
        return pos_val + num_of_con;

    }

    /*
    This function will read in the initial board format and will
    asign values for every option there is. It will returen the list of
    values on the board
     */
    public static int[][] asign_valPositions(Board board){
        int[][] b_pos_val = new int[board.getRows()][board.getCols()];
        int mid_col = (board.getCols()-1)/2;

        for( int row = 0; board.getRows()>row; row ++){
            for(int col = 0 ; board.getCols()>col; col ++){
                int col_weight = mid_col +1  - Math.abs(col-mid_col);

                b_pos_val[row][col] = Math.max(1,col_weight) * (board.getRows() -row);
            }
        }

        //to2DString(board,b_pos_val);
        return b_pos_val;
    }

    /*
    this calucaltes the users positional value
     */
   private static int calc_positional_val(int player_val, Board state){
       int position_val_total = 0;
       for( int row = 0; state.getRows()>row; row ++){
           for(int col = 0 ; state.getCols()>col; col ++){
               if(state.board[row][col] == player_val){
                   position_val_total += BoardDemo.eval_board[row][col];
               }
           }
       }
       if(player_val== -1){
           position_val_total *= -1;
       }

       return position_val_total;
   }


/*
Checking how many connecting pieces there are. Adds values if their are more connecting pieces as the game goes on, and
penalizes the total if the other player has connecting pieces
 */
private static int calc_num_of_connects(int playerval, Board state) {
    int total = 0;
    int rows = state.getRows();
    int cols = state.getCols();
    int consec_move = (state.getNumberOfMoves() > 5) ? 3 : 2;

    // Iterate through the board
    for (int r = 0; r < rows; r++) {
        for (int c = 0; c < cols; c++) {
            // Skip empty spots
            if (state.board[r][c] != playerval) {
                continue;
            }

            // Check northeast diagonal
            if (r + consec_move < rows && c + consec_move < cols) {
                boolean isConnected = true;
                for (int x = 0; x < consec_move; x++) {
                    if (state.board[r + x][c + x] != state.board[r + x + 1][c + x + 1]) {
                        isConnected = false;
                        break;
                    }
                }
                if (isConnected && playerval == state.board[r][c]) {
                    total += 5;
                }
                else if( isConnected){total -=6;}
            }

            // Check northwest diagonal
            if (r + consec_move < rows && c - consec_move >= 0) {
                boolean isConnected = true;
                for (int x = 0; x < consec_move; x++) {
                    if (state.board[r + x][c - x] != state.board[r + x + 1][c - x - 1]) {
                        isConnected = false;
                        break;
                    }
                }
                if (isConnected && playerval == state.board[r][c]) {
                    total += 5;
                }
                else if( isConnected){total -=6;}
            }

            // Check vertical
            if (r + consec_move < rows) {
                boolean isConnected = true;
                for (int x = 0; x < consec_move; x++) {
                    if (state.board[r + x][c] != state.board[r + x + 1][c]) {
                        isConnected = false;
                        break;
                    }
                }
                if (isConnected && playerval == state.board[r][c]) {
                    total += 5;
                }
                else if( isConnected){total -=6;}
            }

            // Check horizontal
            if (c + consec_move < cols) {
                boolean isConnected = true;
                for (int x = 0; x < consec_move; x++) {
                    if (state.board[r][c + x] != state.board[r][c + x + 1]) {
                        isConnected = false;
                        break;
                    }
                }
                if (isConnected && playerval == state.board[r][c]) {
                    total += 5;
                }
                else if( isConnected){total -=6;}
            }
        }
    }

    return (playerval > 0) ? total : total * -1;
}

    public static void to2DString(Board board, int[][] vals) {
        StringBuilder sb = new StringBuilder();
        for (int r = board.getRows() - 1; r >= 0; r--) {
            for (int c = 0; c < board.getCols(); c++) {
                sb.append(vals[r][c]);
            }
            sb.append("\n");
        }
        sb.append("0 1 2 3 4 5 6 7 8 9", 0, board.getCols() * 2);
        System.out.print(sb);//  + "full=" + boardIsFull + " winner=" + winner;
    }






}
