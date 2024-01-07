public class Figure {

    private String[] pieces = new String[] {"line","t","square","l",
            "j","s","z"};

    private int[][][] rotations = new int[][][] {
            //Positions of pieces in 4x4 array
            {{4,5,6,7},{2,6,10,14},{8,9,10,11},{1,5,9,13}},
            {{1,4,5,6},{1,5,6,9},{4,5,6,9},{1,4,5,9}},
            {{1,2,5,6}},
            {{2,4,5,6},{1,5,9,10},{4,5,6,8},{0,1,5,9}},
            {{0,4,5,6},{1,2,5,9},{4,5,6,10},{1,5,8,9}},
            {{1,2,4,5},{1,5,6,10},{5,6,8,9},{0,4,5,9}},
            {{0,1,5,6},{2,5,6,9},{4,5,9,10},{1,4,5,8}}
    };
    //Width,Height and xOffset,yOffset of piece from edge in 4x4 array
    private int dim[][][] = new int[][][] {
            {{4,1,0,1},{1,4,2,0},{4,1,0,2},{1,4,1,0}},
            {{3,2,0,0},{2,3,1,0},{3,2,0,1},{2,3,0,0}},
            {{2,2,1,0}},
            {{3,2,0,0},{2,3,1,0},{3,2,0,1},{2,3,0,0}},
            {{3,2,0,0},{2,3,1,0},{3,2,0,1},{2,3,0,0}},
            {{3,2,0,0},{2,3,1,0},{3,2,0,1},{2,3,0,0}},
            {{3,2,0,0},{2,3,1,0},{3,2,0,1},{2,3,0,0}},
    };
    /* Arrays describing kick possibilities given that rotation is blocked.
    * Tests 5 possibilities, then fails, given starting position and rotation(left or right)
    * Each tuple describes a shift in x,y. e.g. {1,1} means right 1 and down 1
    * First list describes kicks for l-pieces, second describes kicks for t,l,j,s,z. o doesn't kick
    * https://tetris.wiki/Super_Rotation_System
    * 0,1,2,3 are the basic rotation turning right from home. a->b means turning from a to b
    * */

    public int IKick[][][] = new int [][][]{
            {{0,0},{-1,0},{2,0},{-1,-2},{2,1}}, // 0 -> 3
            {{0,0},{-2,0},{1,0},{-2,1},{1,-2}}, // 0 -> 1
            {{0,0},{2,0},{-1,0},{2,-1},{-1,2}}, // 1 -> 0
            {{0,0},{-1,0},{2,0},{-1,-2},{2,1}}, // 1 -> 2
            {{0,0},{1,0},{-2,0},{1,2},{-2,-1}}, // 2 -> 1
            {{0,0},{2,0},{-1,0},{2,-1},{-1,2}}, // 2 -> 3
            {{0,0},{-2,0},{1,0},{-2,1},{1,-2}}, // 3 -> 2
            {{0,0},{1,0},{-2,0},{1,2},{-2,-1}}, // 3 -> 0
    };

    public int restKick[][][] = new int[][][]{
            {{0,0},{1,0},{1,-1},{0,2},{1,2}},     // 0 -> 3
            {{0,0},{-1,0},{-1,-1},{0,2},{-1,2}},  // 0 -> 1
            {{0,0},{1,0},{1,1},{0,-2},{1,-2}},    // 1 -> 0
            {{0,0},{1,0},{1,1},{0,-2},{1,-2}},    // 1 -> 2
            {{0,0},{-1,0},{-1,-1},{0,2},{-1,2}},  // 2 -> 1
            {{0,0},{1,0},{1,-1},{0,2},{1,2}},     // 2 -> 3
            {{0,0},{-1,0},{-1,1},{0,-2},{-1,-2}}, // 3 -> 2
            {{0,0},{-1,0},{-1,1},{0,-2},{-1,-2}}, // 3 -> 0
    };

    //The squares the pieces lies in on a 4x4 array.
    public int[] rot;

    //The number of unique rotations for the given piece
    public int numRots;

    //The current rotation
    public int currRot;

    //Which piece the current Figure is. Always 0<=piece<=6.
    private int piece;


    private int height;
    private int width;
    private int xOffset;
    private int yOffset;
    private int xPos;
    private int yPos;


    public Figure(int s){
        assert (s < 7 && s >= 0);
        this.piece = s;
        this.rot = rotations[piece][0];
        this.numRots = rotations[piece].length;
        this.currRot=0;
        this.xPos = 4;
        this.yPos = 0;
        findDim();

    }

    //Rotates the piece clockwise
    public void rotate(){
        currRot = (currRot+1)%4;
        rot = rotations[piece][(currRot)%numRots];
        findDim();
    }

    //Rotates the piece anticlockwise
    public void leftRotate(){
        currRot = (currRot+3)%4;
        rot = rotations[piece][(currRot)%numRots];
        findDim();
    }

    public int getXPos(){
        return this.xPos;
    } //Returns the X coordinate of the piece

    public int getYPos(){
        return this.yPos;
    } //Returns the Y coordinate of the piece

    public void setXPos(int x){
        this.xPos = this.xPos+x;
    } //Adds x to the current X coordinate

    public void setYPos(int y){
        this.yPos = this.yPos+y;
    } // Adds y to the current Y coordinate

    //Returns the offset of the piece from the left side of the 4x4 square containing the piece
    public int getXOffset(){
        return this.xOffset;
    }

    //Returns the offset of the piece from the top of the 4x4 square containing the piece
    public int getYOffset(){
        return this.yOffset;
    }

    public int getHeight(){
        return this.height;
    } // Returns the height of the piece

    public int getWidth(){
        return this.width;
    } // Returns the width of the piece

    public int getPiece(){
        return this.piece;
    } // Returns which piece the current figure is


    //Finds width,height,xOffset,yOffset from dim array
    public void findDim(){
        this.width = dim[piece][(currRot)%numRots][0];
        this.height = dim[piece][(currRot)%numRots][1];
        this.xOffset = dim[piece][(currRot)%numRots][2];
        this.yOffset = dim[piece][(currRot)%numRots][3];

    }

    @Override
    public Figure clone(){
        Figure clone = new Figure(piece);
        clone.setXPos(xPos-clone.getXPos());
        clone.setYPos(yPos-clone.getYPos());
        clone.rot = rot;
        clone.currRot = currRot;
        clone.findDim();
        return clone;
    }
}
