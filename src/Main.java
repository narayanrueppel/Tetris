import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;


public class Main{
    private static int cellSize = 35;

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> createGUI());

    }

    public static void createGUI(){
        System.out.println("noopy");
        //Main JFrame which contains all JPanels
        JFrame frame = new JFrame("Tetris");
        frame.setPreferredSize(new Dimension(800,1200));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Background JPanel
        JPanel panel=new JPanel();
        panel.setBounds(0,0,800,1200);
        panel.setBackground(Color.DARK_GRAY);

        //Display that shows currently held piece
        PieceDisplay hold = new PieceDisplay(cellSize);
        hold.setBorder(BorderFactory.createLineBorder(Color.black,4));
        hold.setBounds(200-5*(cellSize+5), 50+2*cellSize ,5*(cellSize+5),5*(cellSize+5));
        hold.setBackground(Color.white);
        JLabel label = new JLabel("Hold");
        label.setFont(label.getFont().deriveFont(16.0f));
        hold.add(label,BorderLayout.PAGE_START);

        //Display that shows next piece in queue.
        PieceDisplay nextPiece = new PieceDisplay(cellSize);
        nextPiece.setBorder(BorderFactory.createLineBorder(Color.black,4));
        nextPiece.setBounds(550,50+2*cellSize,5*(cellSize+5),5*(cellSize+5));
        nextPiece.setBackground(Color.white);
        JLabel label2 = new JLabel("Next Piece");
        label2.setFont(label2.getFont().deriveFont(16.0f));
        nextPiece.add(label2,BorderLayout.PAGE_START);

        //Game Initializer
        Game game = new Game(10,20,cellSize,hold,nextPiece);

        //Loss Message
        JLabel lossMessage = new JLabel("Press R to restart.",SwingConstants.CENTER);
        lossMessage.setFont(lossMessage.getFont().deriveFont(24.0f));
        lossMessage.setVisible(false);
        frame.add(lossMessage,BorderLayout.CENTER);

        //Label which keeps track of score. Updates when property change is fired from game.
        JLabel scoreLabel = new JLabel("Score: " + game.getScore(), SwingConstants.CENTER);
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(24.0f));
        frame.add(scoreLabel,BorderLayout.PAGE_START);

        //Adds game and panels to frame
        frame.pack();
        frame.add(game);
        game.requestFocus();
        frame.add(hold);
        frame.add(nextPiece);
        frame.add(panel);

        frame.setVisible(true);

        startGame(game);

        //Listeners
        game.addPropertyChangeListener(e -> scoreLabel.setText("Score: "+ game.getScore()));


    }

    public static void startGame(Game game){
        new GameThread(game).start();
    }
}