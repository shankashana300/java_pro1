import javax.swing.JFrame;

public class App {
    public static void main(String[] args) throws Exception {
        //varialables to create window
        int rowCount = 21;
        int columnCount = 19;
        int titleSize = 32;
        int boardWidth = columnCount * titleSize;
        int boardHeight = rowCount * titleSize;

        JFrame frame = new JFrame("Pac Man");
        // frame.setVisible(true); //TBS
        frame.setSize(boardWidth, boardHeight); //size of W
        frame.setLocationRelativeTo(null); //centre
        frame.setResizable(false); //cannot expand
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit

        //creating instance of JPanel
        PacMan pacmanGame = new PacMan();
        frame.add(pacmanGame); //adding current JPanel to Window.
        frame.pack(); //full size of JPanel of the window
        pacmanGame.requestFocus(); //
        frame.setVisible(true); //
    }
}
