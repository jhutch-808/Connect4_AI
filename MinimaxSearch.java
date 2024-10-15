/*
Name: Julia Hutchison
      Date: 10/24/2024
      Class: CS372
      Pledge: I have neither given nor received unauthorized aid on this program.
      Description: This is a search algorithm to help players get the best chance of winning at connect 4
      This particular one uses alpha beta pruning with heuristics
 */
import java.util.ArrayList;
import java.util.Map;

public class MinimaxSearch {


    public static Minmaxinfo MinMax_Search(Board board_state, Map<Board,Minmaxinfo> t_table){

        // if the state is in the table already return the info
        if (t_table.containsKey(board_state)){
            return t_table.get(board_state);
        }

        // the state is terminal so return info (action is null since its terminal)
        else if(!board_state.getGameState().equals(GameState.IN_PROGRESS)){
            Integer util = UTILITY(board_state);
            Minmaxinfo info = new Minmaxinfo(util, null);
            t_table.put(board_state, info);
            return info;
        }

        // Max's turn to explore/ evaluate
        else if(board_state.getPlayerToMoveNext().equals(Player.MAX)){
            int v = Integer.MIN_VALUE;
            Integer best_move = null;
            ArrayList<Integer> Actions = Action(board_state);
            for(Integer action: Actions){
                Board child_state = board_state.makeMove(action);
                Minmaxinfo child_info = MinMax_Search(child_state, t_table);
                int v2 = child_info.getValue();
                if (v2 > v){
                    v = v2;
                    best_move = action;
                }
            }
            Minmaxinfo info = new Minmaxinfo(v, best_move);
            t_table.put(board_state,info);
            return info;

        }
        // Mins turn to explore/ evaluate
        else{
            int v = Integer.MAX_VALUE;
            Integer best_move = null;
            ArrayList<Integer> Actions = Action(board_state);
            for(Integer action: Actions){
                Board child_state = board_state.makeMove(action);
                Minmaxinfo child_info = MinMax_Search(child_state, t_table);
                int v2 = child_info.getValue();
                if (v2 < v){
                    v = v2;
                    best_move = action;
                }
            }
            Minmaxinfo info = new Minmaxinfo(v, best_move);
            t_table.put(board_state,info);
            return info;
        }

    }

    /*
    returns the value of the state that they are in
     */
    public static Integer UTILITY(Board state){
        if (state.getGameState() == GameState.MIN_WIN){
            return ((10000* state.getCols() *state.getRows())/ state.getNumberOfMoves()) * (-1);
        }
        else if (state.getGameState() == GameState.MAX_WIN) {
            return (10000* state.getCols() *state.getRows())/ state.getNumberOfMoves();
        }
        return 0;

    }

    public static ArrayList<Integer> Action(Board state){
        ArrayList<Integer> actions = new ArrayList<>();
        // the actions are where to put the pieces oon the board which is only the num of columns
        for (int i =0 ; state.getCols()>i; i++){
            // if the column is not full then it is a valid move
            if (!state.isColumnFull(i)){
                actions.add(i);
            }
        }
        return actions;
    }
}
