/*
Name: Julia Hutchison
      Date: 10/24/2024
      Class: CS372
      Pledge: I have neither given nor received unauthorized aid on this program.
      Description: This is an object so the search algrithms can hold the values of each state and know what action
      to get to that particular state
 */
import java.util.Objects;

/*
 Minimaxinfo contains the state utliity value, the best action aka where to put the piece
 */
public class Minmaxinfo { // aka our node
    Integer value; // the value of the state
    Integer action; // best action/move aka which column the user should put their piece
    public Minmaxinfo( Integer value, Integer action){

        this.action = action;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Minmaxinfo that = (Minmaxinfo) o;
        return Objects.equals(value, that.value) && Objects.equals(action, that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, action);
    }

    public Integer getValue() {
        return value;
    }

    public Integer getAction() {
        return action;
    }
}
