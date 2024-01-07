
public class GameThread extends Thread{
    private Game game;

    int ticks;
    public GameThread(Game game){
        this.game = game;
        this.ticks = 0;
    }
    @Override
    public void run(){

        while(game.isActive){
            try{

                //Gravity
                if(game.canDown()){
                    game.currPiece.setYPos(1);
                    game.setPos();
                    game.quickRepaint();
                }
                else{
                    ticks ++;
                }
                Thread.sleep(750);

                //Stacks piece automatically if it is resting for too long
                if(ticks == 2){
                    game.stack();
                    ticks = 0 ;
                }
            }
            catch (InterruptedException e){

            }
        }
    }
}
