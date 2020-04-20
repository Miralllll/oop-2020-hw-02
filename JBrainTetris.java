import javax.swing.*;
import java.awt.*;

public class JBrainTetris extends JTetris{


    private DefaultBrain brain;
    // container for best move - see in Brain interface
    private Brain.Move brainBestMove;
    // remembers the last meaning of the JTatris.count
    private int rememberedCount;

    protected JCheckBox brainMode;
    protected JSlider adversary;
    protected JLabel ok;
    protected JCheckBox animateFalling;

    /**
     * Creates a new JTetris where each tetris square
     * is drawn with the given number of pixels.
     */
    JBrainTetris(int pixels) {
        super(pixels);
        brain = new DefaultBrain();
        // this information should be updated again after stop.
        brainBestMove = null;
        rememberedCount = 0;
    }

    /**
     * Adds new panels on the control panel, adds adversary slider,
     * animation falling checkbox, and ok label.
     */
    @Override
    public JComponent createControlPanel(){
        JPanel panel = (JPanel) super.createControlPanel();
        SetUpAdversary(panel);
        SetUpBrainActive(panel);
        SetUpAnimateFalling(panel);
        SetUpOk(panel);
        return panel;
    }

    /** Sets up ok label */
    private void SetUpOk(JPanel panel) {
        JPanel okPanel = new JPanel();
        ok = new JLabel("Ok");
        okPanel.add(ok);
        panel.add(okPanel);
    }

    /** Sets up animation falling checkbox, which is true by default */
    private void SetUpAnimateFalling(JPanel panel) {
        animateFalling = new JCheckBox("Animate Falling");
        animateFalling.setSelected(true); // default to true
        panel.add(animateFalling);
    }

    /**Sets up brain turn on - checkBox, which is false by default */
    private void SetUpBrainActive(JPanel panel) {
        panel.add(new JLabel("Brain:"));
        brainMode = new JCheckBox("Brain active");
        panel.add(brainMode);
    }

    /** Sets up adversary slider, which return values from 0...99 and is 0 as default */
    private void SetUpAdversary(JPanel panel) {
        JPanel little = new JPanel(); // make a little panel
        little.add(new JLabel("Adversary:"));
        adversary = new JSlider(0, 100, 0); // min, max, current
        adversary.setPreferredSize(new Dimension(100,15));
        little.add(adversary); // put a JSlider in it.
        panel.add(little);  // add little to panel of controls
    }

    /**
     *  It's ticks override. If brain mode is turned-on and verb == DOWN
     *  method searches its piece's bestMove's x coordinate and rotation type by iterating...
     *  Tick can make only one rotation and movement left or right.
     */
    @Override
    public void tick(int verb){
        // If brain mode is selected, takes the opportunity to move the piece a bit first
        // It is an override of the DOWN part.
        if(brainMode.isSelected() && verb == DOWN) {
            // when the JTetris.count variable has changed to know that a new piece is in play
            if(rememberedCount != count)
                // if update failed, tick failed too.
                if(!tryUpdateDestination(verb)) return;
            if(tryReachToDestination())
                // If it is not already there, then we need to drop our piece
                if(currentY != brainBestMove.y) verb = DROP;
        }
        super.tick(verb);
    }

    /**
     * JBrainTetris has its new bestMove, so tick needs to try to reach this position by iterating...
     * This method rotates currentPiece one time if it is needed and moves one bit left or right, if needed too.
     * If tick reached its destination returns true, otherwise false.
     */
    private boolean tryReachToDestination() {
        // keeps searching the correct piece shape for the best move.
        if(!currentPiece.equals(brainBestMove.piece))
            currentPiece = currentPiece.fastRotation();
        // tries to go near to the best move's x coordinate.
        if(brainBestMove.x > currentX) currentX ++;
        else if(brainBestMove.x < currentX) currentX --;
        // when x coordinates are equal and animateFalling is selected, it reached best-move's destination.
        else return(!animateFalling.isSelected() && currentPiece.equals(brainBestMove.piece));
        return false;
    }

    /** Gets new bestMove from brain. It is null, it means that game is over/stopped/or/not started. */
    private boolean tryUpdateDestination(int verb) {
        //The brain needs the board in a committed state before doing its computation
        board.undo();
        brainBestMove = brain.bestMove(board, currentPiece, HEIGHT, brainBestMove);
        if (brainBestMove == null) { stopGame(); return false; }
        rememberedCount = count;
        return true;
    }

    /** It is important to start new game with updated instances after pushing stop button */
    @Override
    public void stopGame(){
        rememberedCount = 0;
        brainBestMove = null;
        super.stopGame();
    }

    /** tries to find a new piece, according to adversary value
     * Method randoms integer from adversary value to 100 and
     * if it is more than this adversary meaning, picks next piece as JTetris makes it.
     * else choose the worst case scenario from the best cases */
    @Override
    public Piece pickNextPiece(){
        int currAdversity = adversary.getValue();
        int randNum = random.nextInt(100);
        // if this, then the piece should be chosen randomly as usual
        if(randNum >= currAdversity) {
            ok.setText("Ok");
            return super.pickNextPiece();
        }
        ok.setText("* Ok *");
        return findWorstPiece();
    }

    /** tries to find the worst move from the best moves for each pieces */
    private Piece findWorstPiece() {
        Piece worstOne = null;
        double maxi = Double.MIN_VALUE;
        Brain.Move currMove = null;
        for(int i = 0; i < pieces.length; i ++){
            currMove = brain.bestMove(board, pieces[i], HEIGHT, currMove);
            if(currMove == null) return super.pickNextPiece();
            worstOne = maxi < currMove.score ? pieces[i] : worstOne;
            maxi = Math.max(maxi, currMove.score);
        }
        return worstOne;
    }

    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        // here we need to run JBrainTetris, so runtime type of tetris is JBrainTetris.
        JTetris tetris = new JBrainTetris(16);
        JFrame frame = JBrainTetris.createFrame(tetris);
        frame.setVisible(true);
    }
}
