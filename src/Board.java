import java.lang.reflect.Array;
import java.util.ArrayList;
import java.awt.Color;
import java.util.Random;

public class Board {

    //Represents the blocks which have been stacked. True if there is a block at coordinate i,j
    //Always has length 22, and boolean[] are always length 10
    //First two entries represent spawn area
    public ArrayList<boolean[]> stack;

    //Represents the color at a coordinate in the stack
    //Always has length 22, and boolean[] are always length 10
    //First two entries represent spawn area
    public ArrayList<Color[]> stackColors;

    Game game;

    public Board(Game game){

        this.stack = new ArrayList<>();
        this.stackColors = new ArrayList<>();

        for(int i = 0 ;i<22;i++){
            stack.add(new boolean[10]);
            stackColors.add(new Color[10]);
        }
        this.game = game;
    }

    //Returns an ArrayList of Lines that need to be cleared
    public ArrayList<Integer> checkClear(){
        ArrayList<Integer> lines = new ArrayList<>();

        for(int i =2; i<22;i++){
            boolean line = true;

            for(int j = 0;j<10;j++){
                if(!stack.get(i)[j]){
                    line = false;
                    break;
                }
            }
            if(line){
                lines.add(i);
            }
        }
        return lines;
    }

    //Returns true if there is block/stack beneath piece.
    public boolean checkStack(){
        for(int pos: game.currPos){
            if(pos+10>219){
                return true;
            }
            if(stack.get(pos/10+1)[pos%10]){
                return true;
            }
        }
        return false;
    }

    //Removes completed lines from the board
    public int clearLines(ArrayList<Integer> list){
        int numRemovedLines = 0;
        if(list.size() != 0){
            for(int line:list){

                stack.remove(line);
                stackColors.remove(line);

                //Adds empty line back to top

                stack.add(0,new boolean[10]);
                stackColors.add(0,new Color[10]);

                numRemovedLines ++;
            }
        }
        assert stack.size() == 22;
        assert stackColors.size() == 22;
        return numRemovedLines;
    }

    //Game is lost if pieces are stacked in spawn zone
    //Returns true if game is lost
    public boolean checkLose(){
        for(int i = 0; i<2;i++){
            for(int j = 0; j<10;j++){
                if(stack.get(i)[j]){
                    return true;
                }
            }
        }
        return false;
    }

}
