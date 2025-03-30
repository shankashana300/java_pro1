import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener{
    
    //Location
    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startx;
        int starty;
        //Movement of Pacman
        char direction = 'U'; //U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height){
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startx = x;
            this.starty = y;
        }

        //Updating movements of the Pacman when keys are pressed.
        //Includes movement of Pacman when there is space otherwise, keeps moving.
        void updateDirection(char direction){
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity(){
            if (this.direction == 'U') {
                this.velocityX = 0;
                this.velocityY = -titleSize/4; //32px/4 = 8px. moving 8 blocks or pxs upwards in regard to velocity and face up ONLY. "-" cuz it's "-y" and towards row number 0.
            }
            else if (this.direction == 'D') {
                this.velocityX = 0;
                this.velocityY = titleSize/4; //32px/4 = 8px. moving 8 blocks or pxs downwards in regard to velocity and face down ONLY. "+" cuz it's "+y" and towards row number 20.
            }
            else if (this.direction == 'L') {
                this.velocityX = -titleSize/4; //32px/4 = 8px. moving 8 blocks or pxs to the left in regard to velocity and face left ONLY. "-" cuz it's "-x" and towards column number 0.
                this.velocityY = 0;
            }
            else if (this.direction == 'R') {
                this.velocityX = titleSize/4; //32px/4 = 8px. moving 8 blocks or pxs to the right in regard to velocity and face right ONLY. "+" cuz it's "+x" and towards column number 20.
                this.velocityY = 0; 
            }
        }
        //when Pacman collides w Ghosts, reset game and starting position for Pacman.
        //Resetting the position to origin.
        void reset(){
            this.x = this.startx;
            this.y = this.starty;
        }
    }
    private int rowCount = 21;
    private int columnCount = 19;
    private int titleSize = 32;
    private int boardWidth = columnCount * titleSize;
    private int boardHeight = rowCount * titleSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image redGhostImage;
    private Image pinkGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    //Title Map
    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    //Timer needed for the activation of the game loop.
    Timer gameLoop;
    //For each ghost to work at random directions.
    char[] directions ={'U', 'D', 'L', 'R'}; //array containing up, down, left and right
    Random random = new Random();

    //Keeping track on how many lives does Pacman have - second
    //Keeping score if Pacman eats the food - first
    //If it's game over or not? - last
    int score = 0; //Everytime Pacman eats the food, he will gain 10 points.
    int lives = 3;
    boolean gameOver = false; 


    PacMan(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true); //This component is main focus. J.I.C. if there are other components involved.

        //load images
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)]; //index [0,1,2,3]
            ghost.updateDirection(newDirection); //update velocity of per ghost.
        }
        
        //how long it takes to start timer, milliseconds gone between frames
        //50 millisecs and "this" is the pacman propertities taken from.
        gameLoop = new Timer(50, this); //20fps (1000/50), 1000 millisec = 1 sec
        gameLoop.start();

        // System.out.println(walls.size());
        // System.out.println(foods.size());
        // System.out.println(ghosts.size());
    }

    public void loadMap(){

        //creating objs
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        //repeatition the process
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r]; //current row
                char tileMapChar = row.charAt(c); //current character

                //Finding the char in the specific title in map
                int X = c*titleSize; //from left to right
                int y = r*titleSize; //from up to down

                //
                if (tileMapChar=='X') { //Block wall
                    Block wall = new Block(wallImage, X, y, titleSize,titleSize);
                    walls.add(wall);
                }
                else if (tileMapChar=='b') { //blue ghost
                    Block ghost = new Block(blueGhostImage, X, y, titleSize, titleSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar=='o') { //orange ghost
                    Block ghost = new Block(orangeGhostImage, X, y, titleSize, titleSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r') { //red ghost
                    Block ghost = new Block(redGhostImage, X, y, titleSize, titleSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar=='p') { //pink ghost
                    Block ghost = new Block(pinkGhostImage, X, y, titleSize, titleSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') { //Pac Man
                    pacman = new Block(pacmanRightImage, X, y, titleSize, titleSize);
                }
                else if(tileMapChar == ' ') //food
                {
                    Block food = new Block(null, X+14, y+14, 4, 4);
                    foods.add(food);
                }

                //The "O" is not nesscary to be added.

            }
        }


    }

    //Draw all the mentioned above into my game.
    //Making all elements visible.
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g); //Next step, Game loop needed for the game to work again and again etc.
    }

    public void draw(Graphics g){
        g.drawImage(pacman.image,pacman.x,pacman.y,pacman.width,pacman.height,null);
        
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image,ghost.x,ghost.y,ghost.width,ghost.height,null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image,wall.x,wall.y,wall.width,wall.height,null);
        }

        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillRect(food.x,food.y,food.width,food.height);
        }
        //Score
        g.setFont(new Font("Arial", Font.PLAIN, 18) );
        if (gameOver) {
            g.drawString("Game Over " + String.valueOf(score),  titleSize/2, titleSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + "Score " + String.valueOf(score),  titleSize/2, titleSize/2);
        }
    }

    //visible movement of Pacman.
    public void move(){
        //move forwards
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //move backwards
        //check wall collisions
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //check ghost collision
        for(Block ghost : ghosts){
            //soultion to check if current ghost colliled with a pacman
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }
            //solution if ghost stuck going in 1 direction
            if (ghost.y == titleSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall)|| ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        //Check Food collision
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
            }
        }
        foods.remove(foodEaten);

        //After completing LVL 1, moving on to LVL 2
        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }

        //In conclusion, moving forwards and backwards at the sametime. Results in a stand-still.
        //Visual aid, not moving but behind back, moving at the simlatanously when met with a block.
    }

    //Collision Detetction Formula
    public boolean collision(Block a, Block b){
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    //Definig reset positions.
    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;

        //new direction for the ghosts
        for (Block ghost : ghosts) {
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    //calling the paint component again.
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        //System.out.println("KEyEvent: " + e.getKeyCode());

        //Solution to try again by typing any key on the keyboard.
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        
        //Movement of the Velocities not the visible movements after release though shows Pacman Direction to move.
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN){
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }
        //showing the direction of Pacman is set below instead inside the "getKeyCode() case" is because avoid collision with wall due to the fact that Pacman is indeed a block.
        if (pacman.direction == 'U') {
            pacman.image = pacmanUpImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
    }
}
