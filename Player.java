/*
 * This code is protected under the Gnu General Public License (Copyleft), 2005 by
 * IBM and the Computer Science Teachers of America organization. It may be freely
 * modified and redistributed under educational fair use.
 */

import csta.ibm.pong.GameObject;
import java.util.Random;

public class Player extends GameObject {
	
	/**
	 * Fill in this method with code that describes the behavior
	 * of a ball from one moment to the next
	 */
	public void act() {
		setX(getX());
		setY(getY());
	}
	public void moveUp(){
		setY(getY() - 2);
	}
	public void moveDown(){
		setY(getY() + 2);
	}
	public void moveLeft() {
		setX(getX() - 2);
	}
	public void moveRight() {
		setX(getX() + 2);
	}
	
	/*
	 * function: returns the object (player or enemy) to original spawn point
	 * pre: x and y are ints >= 0, and are the positions of the spawn 
	 * post: changes the current position of the object to their spawn coordinates
	 */
	public void respawn(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public void customMove(int dx, int dy) {
//		Random r = new Random();
//		int direction = r.nextInt(4);
//		int dx = 0;
//		int dy = 0;
//		switch (direction) {
//			case 1: dy = -2; break;
//			case 2: dy = 2; break;
//			case 3: dx = -2; break;
//			case 4: dx = 2; break;
//		}
		setX(getX() + dx);
		setY(getY() + dy);
	}
}
