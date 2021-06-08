/*
 * This code is protected under the Gnu General Public License (Copyleft), 2005 by
 * IBM and the Computer Science Teachers of America organization. It may be freely
 * modified and redistributed under educational fair use.
 */

/*
 * @author Mark Zhang
 */
import csta.ibm.pong.Game;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

public class PacMan extends Game {
	// Add any state variables or object references here
	Player player;
	Player[] ghosts = new Player[3];
	int[][] ghostSpawn = {{10*20, 8*20}, {9*20, 8*20}, {11*20, 8*20}}; //coordinates of the spawn points of the ghosts
	int[] lastDirectionMoved = new int[3]; // stores current movement direction of ghosts
	
	// screen labels for score and lives and their respective counters
	JLabel displayScore = new JLabel();
	JLabel displayLives = new JLabel();
	int lives = 3;
	int score = 0; 
	
	public static final int HEIGHT = 420; // getFieldHeight() for pac man area
	public static final int WIDTH = 420; // getFieldWidth() for pac man area
	
	Wall[] walls = new Wall[44];
	Wall[][] coins = new Wall[21][21];
	
	//power pellets are special objects in Pac Man where if the player picks them up, they can eat the ghosts 
	Wall[] pellets = new Wall[6]; // there will be 6 pellets placed in certain locations 
	int powerPellet = 0; // using an int as a timer 
	JLabel pelletTimer = new JLabel(); // will be used to display amount of time left once player has eaten pellet
	
	Random r = new Random(); // using random class for ghost movement
	
	// read file (for instructions)
	public String readFile() {
		String text = "";
		try {
			File myFile = new File("ICS3U1File.txt"); 
			Scanner input = new Scanner(myFile);
			while (input.hasNextLine()) {
				String line = input.nextLine();
				text += line + "\n";
			}
		}
		catch (FileNotFoundException e) {
			
		}
		return text;
	}

	public void setup() {
		//set delay
		setDelay(2);
		
		//set up walls and add them to screen
		walls[0] = new Wall(0, 0, WIDTH, 20);
		walls[1] = new Wall(0, 0, 20, HEIGHT);
		walls[2] = new Wall(WIDTH-20, 0, 20, HEIGHT);
		walls[3] = new Wall(0, HEIGHT-20, WIDTH, 20);
		
		walls[4] = new Wall(40, 40, 20, 4*20);
		walls[5] = new Wall(60, 40, 20, 20);
		walls[6] = new Wall(5*20, 40, 4*20, 20);
		walls[7] = new Wall(4*20, 4*20, 20, 5*20);
		walls[8] = new Wall(3*20, 7*20, 20, 2*20);
		walls[9] = new Wall(20, 7*20, 20, 7*20);
		walls[10] = new Wall(80, 6*20, 5*20, 20);
		walls[11] = new Wall(200, 20, 20, 40);
		walls[12] = new Wall(7*20, 4*20, 7*20, 20);
		walls[13] = new Wall(10*20, 5*20, 20, 40);
		walls[14] = new Wall(20, 10*20, 4*20, 20);
		walls[15] = new Wall(12*20, 2*20, 4*20, 20);
		walls[16] = new Wall(17*20, 2*20, 20, 20);
		walls[17] = new Wall(18*20, 2*20, 20, 4*20);
		walls[18] = new Wall(12*20, 6*20, 4*20, 20);
		walls[19] = new Wall(16*20, 4*20, 20, 5*20);
		walls[20] = new Wall(7*20, 4*20, 7*20, 20);
		walls[21] = new Wall(200, 5*20, 20, 40);
		walls[22] = new Wall(6*20, 9*20, 9*20, 20);
		walls[23] = new Wall(6*20, 8*20, 20, 20);
		walls[24] = new Wall(14*20, 8*20, 20, 20);
		walls[25] = new Wall(6*20, 11*20, 20, 4*20);
		walls[26] = new Wall(17*20, 7*20, 20, 40);
		walls[27] = new Wall(380, 7*20, 20, 7*20);
		walls[28] = new Wall(16*20, 10*20, 4*20, 20);
		walls[29] = new Wall(80, 12*20, 20, 4*20);
		walls[30] = new Wall(60, 12*20, 20, 40);
		walls[31] = new Wall(14*20, 11*20, 20, 4*20);
		walls[32] = new Wall(12*20, 14*20, 40, 20);
		walls[33] = new Wall(6*20, 16*20, 9*20, 20);
		walls[34] = new Wall(7*20, 14*20, 40, 20);
		walls[35] = new Wall(16*20, 12*20, 20, 4*20);//can add one to h
		walls[36] = new Wall(17*20, 12*20, 20, 40);
		walls[37] = new Wall(40, 15*20, 20, 4*20);
		walls[38] = new Wall(60, 18*20, 20, 20);
		walls[39] = new Wall(5*20, 18*20, 4*20, 20);
		walls[40] = new Wall(18*20, 15*20, 20, 4*20);
		walls[41] = new Wall(17*20, 18*20, 20, 20);
		walls[42] = new Wall(12*20, 18*20, 4*20, 20);
		walls[43] = new Wall(200, 18*20, 20, 40);
		
		for (Wall w: walls) {
			w.setColor(Color.blue);
			add(w);
		}
		
		// set up and add pellets ("+5" to center align them) 
		pellets[0] = new Wall(20+5, 20+5, 10, 10);
		pellets[1] = new Wall(200+5, 3*20+5, 10, 10);
		pellets[2] = new Wall(380+5, 20+5, 10, 10);
		pellets[3] = new Wall(200+5, 17*20+5, 10, 10);
		pellets[4] = new Wall(20+5, 19*20+5, 10, 10);
		pellets[5] = new Wall(380+5, 18*20+5, 10, 10);
		
		for (Wall p: pellets) {
			p.setColor(Color.green);
			add(p);
		}
		
		// setting up coins here so the walls and pellets overlap the coins 
		for(int i = 1; i < 20; i += 1){
			for (int j = 1; j < 20; j += 1) {
				coins[i][j] = new Wall(j*20+8, i*20+8, 4, 4);
				coins[i][j].setColor(Color.yellow);
				add(coins[i][j]);
			}
		}
		// delete coins on top of ghosts
		for(int i = 7; i < 14; i++) {
			remove(coins[8][i]);
			coins[8][i] = null; // whenever a coin gets removed (player picks it up) I will use "null" to keep track
		}
		// coin on top of player
		remove(coins[11][10]);
		coins[11][10] = null;
		
		//set up player
		player = new Player();
		player.setSize(20, 20);
		player.setColor(Color.yellow); 
		player.setX(10*20);
		player.setY(11*20);
		add(player);  
		
		//add ghosts
		ghosts[0] = new Player();
		ghosts[0].setSize(20, 20);
		ghosts[0].setColor(Color.pink);
		ghosts[0].setX(10*20);
		ghosts[0].setY(8*20);
		add(ghosts[0]);
		
		ghosts[1] = new Player();
		ghosts[1].setSize(20, 20);
		ghosts[1].setColor(Color.red);
		ghosts[1].setX(9*20);
		ghosts[1].setY(8*20);
		add(ghosts[1]);
		
		ghosts[2] = new Player();
		ghosts[2].setSize(20, 20);
		ghosts[2].setColor(Color.orange);
		ghosts[2].setX(11*20);
		ghosts[2].setY(8*20);
		add(ghosts[2]);
		
		// give each ghost a random direction to move in
		lastDirectionMoved[0] = r.nextInt(4);
		lastDirectionMoved[1] = r.nextInt(4);
		lastDirectionMoved[2] = r.nextInt(4);
		
		// set up messages
		JOptionPane.showMessageDialog(null, readFile());
		displayLives.setForeground(Color.white);
		displayLives.setBounds(0, 420, 100, 20);
		displayLives.setText("Lives: "+lives);
		add(displayLives);
		repaint();
		displayScore.setForeground(Color.white);
		displayScore.setBounds(140, 420, 100, 20);
		displayScore.setText("Score: "+score);
		add(displayScore);
		repaint();
		pelletTimer.setForeground(Color.white);
		pelletTimer.setBounds(280, 420, 100, 20);
		pelletTimer.setText("Pellet time: "+powerPellet);
		add(pelletTimer);
		repaint();
	}	

	/* Finds out if a Player object has collided with a wall
	 * pre: a Player object
	 * post: returns true if collision, false otherwise 
	 */
	public boolean wallCollison(Player p) {
		for (Wall w: walls) {
			if (p.collides(w)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * gets a random number which symbolizes direction (0, 1, 2, 3) -> (up, down, left, right)
	 * pre: integer "i" which is the index that represents the ghost # 
	 * post: returns a randomly generated integer that symbolizes a new direction
	 */
	public int getRandomDirection(int i) {
		int newDirection = r.nextInt(4);
		if (newDirection == i) {
			getRandomDirection(i);
		}
		else {
			return newDirection;
		}
		return newDirection;
	}
		
	public void act() {
		//check if player collided with a ghost
		for(int i = 0; i < 3; i++) {
			if (player.collides(ghosts[i])) {
				// if player has eaten a power pellet, ghost dies and player gets additional points
				// otherwise player loses a life 
				if (powerPellet >= 0) {
					ghosts[i].respawn(ghostSpawn[i][0], ghostSpawn[i][1]);
					score += 100;
				}
				else {
					player.respawn(10*20, 11*20);
					lives -= 1;
				}
			}
		}
		
		for (int i = 0; i < 3; i++) {
			// continue to move the ghost in it's current direction
			// if ghosts collides with wall, then it will move in another random direction
			switch (lastDirectionMoved[i]) {
			case 0: 
				ghosts[i].moveUp();
				if (wallCollison(ghosts[i])) {
					ghosts[i].moveDown();
					lastDirectionMoved[i] = getRandomDirection(0);
				}
				break;
			case 1: 
				ghosts[i].moveDown();
				if (wallCollison(ghosts[i])) {
					ghosts[i].moveUp();
					lastDirectionMoved[i] = getRandomDirection(1);
				}
				break;
			case 2: 
				ghosts[i].moveLeft();
				if (wallCollison(ghosts[i])) {
					ghosts[i].moveRight();
					lastDirectionMoved[i] = getRandomDirection(2);
				}
				break;
			case 3: 
				ghosts[i].moveRight(); 
				if (wallCollison(ghosts[i])) {
					ghosts[i].moveLeft();
					lastDirectionMoved[i] = getRandomDirection(3);
				}
				break;
			}
		}
		
		// check if player collided with coins, each coin is worth 10 points 
		for (int i = 1; i < 20; i++) {
			for (int j = 1; j < 20; j++) {
				if (coins[i][j] != null && player.collides(coins[i][j])) {
					remove(coins[i][j]);
					coins[i][j] = null;
					score += 10;
				}
			}
		}
		
		// check if player collided with power pellet
		for (int i = 0; i < pellets.length; i++) {
			if (pellets[i] != null && player.collides(pellets[i])) {
				powerPellet = 1000; // power pellet lasts 1000 milliseconds 
				remove(pellets[i]);
				pellets[i] = null;
				
			}
		}
		if (powerPellet >= 0) {
			powerPellet -= 1; // countdown timer
		}
				
		if (lives <= 0 || QKeyPressed()){
			JOptionPane.showMessageDialog(null, "Score: "+score);
			stopGame();
		}
		
		if (WKeyPressed()) { 
			player.moveUp();
			if (wallCollison(player)) {
				player.moveDown();
			}
		}
		if (SKeyPressed()) {
			player.moveDown();
			if (wallCollison(player)) {
				player.moveUp();
			}
		}
		if (AKeyPressed()) {
			player.moveLeft();
			if (wallCollison(player)) {
				player.moveRight();
			}
		}
		if (DKeyPressed()) {
			player.moveRight();
			if (wallCollison(player)) {
				player.moveLeft();
			}
		}
		
		displayLives.setText("Lives: "+lives);
		repaint();
		displayScore.setText("Score: "+score);
		repaint();
		pelletTimer.setText("Pellet time: "+powerPellet);
		repaint();
	}

	public static void main(String[] args) {
		PacMan p = new PacMan();
		p.setVisible(true);
		p.initComponents();		
	}
}
