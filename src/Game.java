import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Queue;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.util.Random;
import java.util.Collections;
import java.util.List;
import java.util.*;
import java.util.ArrayDeque;
import javax.swing.JLabel;

public class Game extends JPanel implements KeyListener {

    public boolean isActive;


    public boolean isLost;

    private int score;

    //An extra 400 points are given for back to back tetrises;
    private boolean b2b;

    private int numRows;

    private int numColumns;

    private int gridCellSize;

    public Figure currPiece;

    private Deque<Integer> currQueue;

    ArrayList<Integer> queue;

    //Whether hold has been used
    private boolean usedHold;

    //The piece which is being held, if any.
    private int held;

    //The positions of the current piece, in list form.
    public int[] currPos = new int[4];

    public Color[] colors = new Color[] {Color.cyan,Color.magenta,Color.yellow,Color.orange,
            Color.blue,Color.green,Color.red};

    public Board board;

    public PieceDisplay hold;

    public PieceDisplay nextPiece;


    private boolean stacking;

    public Game(int columns,int rows,int cellSize, PieceDisplay hold, PieceDisplay nextPiece){

        addKeyListener(this);

        this.board = new Board(this);
        this.setBounds(200,50,cellSize*columns,cellSize*(rows+2));

        this.hold = hold;

        this.nextPiece = nextPiece;


        this.setBackground(Color.white);
        this.numColumns = columns;
        gridCellSize = cellSize;
        this.numRows = rows;
        this.held = -1;
        this.usedHold = false;

        this.currQueue = new ArrayDeque<>();
        this.queue = new ArrayList<>();

        this.stacking = false;

        this.score = 0;
        this.b2b = false;

        queue.add(0);
        queue.add(1);
        queue.add(2);
        queue.add(3);
        queue.add(4);
        queue.add(5);
        queue.add(6);

        setQueue();

        newPiece();

        setPos();

        this.isActive = true;

    }
    @Override
    public void keyPressed(KeyEvent e) {

        if(e.getKeyCode() == KeyEvent.VK_R){
            restartGame();
        }

        if(!this.stacking && isActive) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                int attempt = rotationKick(true);
                rotateWithKick(true,attempt);
            }

            if (e.getKeyCode() == KeyEvent.VK_A) {
                int attempt = rotationKick(false);
                rotateWithKick(false,attempt);

            }

            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                if (canDown()) {
                    currPiece.setYPos(1);
                    repaint();
                    setPos();
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                if (canLeft()) {
                    currPiece.setXPos(-1);
                    repaint();
                    setPos();
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                if (canRight()) {
                    currPiece.setXPos(1);
                    repaint();
                    setPos();
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                stack();
            }

            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                if (!usedHold) {
                    hold();
                }
            }

        }
    }


    @Override
    public void keyTyped(KeyEvent e){;
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }


    @Override
    public void paintComponent(Graphics g) {
        // Paint the default background. Keep this as the first line of the method.
        if(isActive){
            super.paintComponent(g);
            for(int x = 0;x< this.numColumns;x++){
                for(int y = 0;y< this.numRows+2;y++){
                    g.drawRect(x*gridCellSize,y*gridCellSize,gridCellSize,gridCellSize);
                    if(y<2){
                        g.setColor(Color.gray);
                        g.fillRect(x*gridCellSize,y*gridCellSize,gridCellSize,gridCellSize);
                        g.setColor(Color.black);
                    }
                }
            }
            paintPiece(g,currPiece);

            for(int i = 0; i< 220;i++){
                if(board.stack.get(i/10)[i%10]){
                    g.setColor(board.stackColors.get(i/10)[i%10]);
                    g.fillRect((i%10)*gridCellSize,(i/10)*gridCellSize,gridCellSize,gridCellSize);
                    g.setColor(Color.black);
                    g.drawRect((i%10)*gridCellSize,(i/10)*gridCellSize,gridCellSize,gridCellSize);

                }
            }
        }

    }

    //Paints a piece
    public void paintPiece(Graphics g,Figure curr){
        g.setColor(colors[curr.getPiece()]);
        for(Integer i:curr.rot){
            g.fillRect((curr.getXPos()+i%4)*gridCellSize,(curr.getYPos() + i/4)*gridCellSize,
                    gridCellSize,gridCellSize);
            g.setColor(Color.black);
            g.drawRect((curr.getXPos()+i%4)*gridCellSize,(curr.getYPos() + i/4)*gridCellSize,
                    gridCellSize,gridCellSize);
            g.setColor(colors[curr.getPiece()]);


        }
    }

    //Restarts game
    public void restartGame(){
        isActive = true;
        isLost = false;

        board = new Board(this);

        this.held = -1;
        this.usedHold = false;

        this.hold.reset();
        this.nextPiece.reset();

        nextPiece.setVisible(true);
        hold.setVisible(true);

        this.score = 0;
        firePropertyChange("GameScore", 1, 0);

        this.b2b = false;

        currQueue.clear();
        setQueue();

        newPiece();
        repaint();

    }


    //Ends game and shows end of game screen
    public void loseGame(){
        nextPiece.setVisible(false);
        hold.setVisible(false);
        repaint();
        isActive = false;
        isLost = true;



    }

    public void quickRepaint(){
        repaint();
    }


    //Checks if a rotation requires a kick, and then returns which kick to do
    //If rotation fails, returns -1.
    //A rot value of true indicates a clockwise rotation, false is a counterclockwise rotation
    private int rotationKick(boolean rot){
        if(currPiece.getPiece() == 2){
            return 0;
        }

        //Loop to check each attempt
        for(int attempt = 0; attempt < 5; attempt++){
            if(canRotate(rot,attempt)){
                return attempt;
            }
        }

        return -1;


    }

    //References kick list to determine whether a certain kick will yield a valid position
    //5 different kicks are attempted before the rotation fails
    // a rot value of true indicates a clockwise rotation, false is a counterclockwise rotation
    private boolean canRotate(boolean rot, int attempt){
        assert (attempt>=0 && attempt<5);

        int kick[][][];

        Figure test = currPiece.clone();

        //Rotates test clone
        if (rot){
            test.rotate();
        }
        else{
            test.leftRotate();
        }

        if(test.getPiece() == 2){
            return true;
        }

        else if (test.getPiece() == 0){
            kick = currPiece.IKick;
        }
        else{
            kick = currPiece.restKick;
        }

        int xShift = 0;
        int yShift = 0;

        //Calculates the kick for this attempt
        if (rot){
            xShift = kick[currPiece.currRot*2+1][attempt][0];
            yShift = kick[currPiece.currRot*2+1][attempt][1];

        }
        else{
            xShift = kick[currPiece.currRot*2][attempt][0];
            yShift = kick[currPiece.currRot*2][attempt][1];
        }

        test.setXPos(xShift);
        test.setYPos(yShift);
        test.findDim();

        //Checks rotation against left wall
        if(test.getXPos()+test.getXOffset()<0){
            return false;
        }

        //Checks rotation against right wall
        if(test.getXPos()+test.getXOffset()+test.getWidth()>10){
            return false;
        }
        //Checks rotation against floor
        if(test.getYPos()+test.getYOffset()+test.getHeight()> 22){
            return false;
        }

        //Calculates position of test piece.
        int[] testRot = test.rot;
        int[] testPos = new int[4];
        for(int i=0;i<4;i++){
            testPos[i] = 10*(test.getYPos()+testRot[i]/4) +test.getXPos()+testRot[i]%4;
        }

        for(int pos: testPos){
            //Checks rotation into a piece in the stack
            if(board.stack.get(pos/10)[pos%10]){
                return false;
            }
        }

        return true;
    }

    //Applies the rotation with the calculated kick
    public void rotateWithKick(boolean rot, int attempt){


        if(attempt != -1){ //Makes sure rotation did not fail
            int[][][] dict;
            if(currPiece.getPiece() == 0){
                dict = currPiece.IKick;
            }
            else{
                dict = currPiece.restKick;
            }

            //Rotates piece
            if(currPiece.getPiece() != 2){
                if(rot){
                    currPiece.setXPos(dict[currPiece.currRot*2+1][attempt][0]);
                    currPiece.setYPos(dict[currPiece.currRot*2+1][attempt][1]);
                    currPiece.rotate();
                }
                else{
                    currPiece.setXPos(dict[currPiece.currRot*2][attempt][0]);
                    currPiece.setYPos(dict[currPiece.currRot*2][attempt][1]);
                    currPiece.leftRotate();
                }
            }
        }

        repaint();
        setPos();
    }

    //Stacks the block, and clears lines.
    public void stack(){

        this.stacking = true;
        while(!board.checkStack()){
            currPiece.setYPos(1);
            setPos();
        }

        for(int pos:currPos){
            board.stack.get(pos/10)[pos%10] = true;
            board.stackColors.get(pos/10)[pos%10] = colors[currPiece.getPiece()];
        }

        if(board.checkLose()){
            loseGame();
        }
        clear();

        repaint();
        newPiece();

        this.stacking = false;
    }


    //Clears lines
    private void clear(){
        ArrayList<Integer> lines = board.checkClear();
        int numRemovedLines = board.clearLines(lines);
        if(numRemovedLines != 0){
            increaseScore(numRemovedLines);
        }
    }

    //Gets new block from queue and updates queue if it is too short.
    //Peeks at currQueue to find next piece for display
    private void newPiece(){
        currPiece =new Figure(currQueue.poll());
        setPos();
        usedHold = false;
        if(currQueue.size()<7){
            setQueue();
        }

        nextPiece.setPiece(new Figure(currQueue.peek()));
    }


    //Sets currPos to the current position of the piece
    public void setPos(){
        int[] rot = currPiece.rot;
        for(int i=0;i<4;i++){
                currPos[i] = 10*(currPiece.getYPos()+rot[i]/4)
                        +currPiece.getXPos()+rot[i]%4;
        }
    }


    //Adds another seven pieces to the current queue
    //If first piece of the new pieces is equal to the last piece of the old queue, shuffles new
    // pieces once and then adds them.
    private void setQueue(){
        ArrayList<Integer> copy = shuffle(queue);
        if(!currQueue.isEmpty()){
            if(currQueue.getLast() == copy.get(0)){
                copy = shuffle(queue);
            }
        }
        for(int i: copy){
            currQueue.addLast(i);
        }
    }

    //Holds the current falling piece. Can only be used once per piece.
    private void hold(){

        int temp = held;
        if(held != -1){
            held = currPiece.getPiece();
            currPiece = new Figure(temp);
        }
        else{
            held = currPiece.getPiece();
            newPiece();
        }
        repaint();
        hold.setPiece(new Figure(held));
        usedHold = true;


    }

    //Returns the current score
    public int getScore(){
        return this.score;
    }

    //Increases score and fires property change to label
    private void increaseScore(int numRemovedLines){
        int oldScore = score;
        if(b2b && numRemovedLines==4){
            this.score += 1200;
        }

        else{
            if(numRemovedLines == 4){
                b2b = true;
                this.score += 800;
            }
            else{
                b2b = false;
                this.score += 100 + 200*(numRemovedLines-1);

            }
        }
        firePropertyChange("GameScore", oldScore, score);
    }

    //Shuffles a list randomly
    private ArrayList<Integer> shuffle(ArrayList queue){
        ArrayList<Integer> copy = (ArrayList<Integer>) queue.clone();
        Random random = new Random();
        for(int index = 0; index < copy.size(); index += 1) {
            Collections.swap(copy, index, index + random.nextInt(copy.size() - index));
        }
        return copy;
    }

    //Returns true if the piece can move left
    public boolean canLeft(){
        for(int i:currPos){
            if(i%10 == 0 || board.stack.get(i/10)[i%10-1]) {
                return false;
            }
        }
        return true;
    }

    //Returns true if the piece can move right
    public boolean canRight(){
        for(int i: currPos){
            if(i%10 == 9 || board.stack.get(i/10)[i%10+1]) {
                return false;
            }
        }
        return true;
    }

    //Returns true if the piece can move down
    public boolean canDown(){
        return !board.checkStack();
    }
}
