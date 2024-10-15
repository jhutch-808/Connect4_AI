import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class AlphabetaSearch extends MinimaxSearch{
    public static int num_pruned = 0;

    public static Minmaxinfo AlphaBetasearch(Board state, Integer alpha, Integer beta, Map<Board, Minmaxinfo> t_table){
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

        //Max turn to explore:
        else if( state.getPlayerToMoveNext().equals(Player.MAX)){
            int v = Integer.MIN_VALUE;
            Integer best_move = null;
            ArrayList<Integer> Actions = Action(state);
            for (Integer action: Actions){
                Board child_state = state.makeMove(action);
                Minmaxinfo child_info = AlphaBetasearch(child_state, alpha, beta, t_table);
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
                Minmaxinfo child_info = AlphaBetasearch(child_state, alpha, beta, t_table);
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


}
