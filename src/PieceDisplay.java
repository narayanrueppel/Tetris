import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class PieceDisplay extends JPanel{

    Figure piece;

    int gridCellSize;

    //Does not draw piece if game has just been restarted
    boolean restart = false;

    public Color[] colors = new Color[] {Color.cyan,Color.magenta,Color.yellow,Color.orange,
            Color.blue,Color.green,Color.red};

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(!restart){
            paintPiece(g);
        }
    }

    //Paints the piece in the center of the display
    public void paintPiece(Graphics g){
        if(this.piece != null){
            for(Integer i: piece.rot){
                g.setColor(Color.black);
                g.drawRect((piece.getXPos() + i%4 + 1)*gridCellSize,
                        (piece.getYPos() + i/4 + 1)*gridCellSize, gridCellSize,gridCellSize);
                g.setColor(colors[piece.getPiece()]);
                g.fillRect((piece.getXPos() + i%4 + 1)*gridCellSize,
                        (piece.getYPos() + i/4 + 1)*gridCellSize, gridCellSize,gridCellSize);


            }
        }
    }

    public PieceDisplay(int gridCellSize) {
        this.gridCellSize = gridCellSize;
    }

    //Sets the current piece
    public void setPiece(Figure f){
        restart = false;
        this.piece = f;
        this.piece.setXPos(-4);
        this.piece.setYPos(2);
        repaint();
    }

    //Gets the current piece
    public Figure getPiece(){
        return this.piece;
    }

    public void reset(){
        restart = true;
        repaint();

    }


}
