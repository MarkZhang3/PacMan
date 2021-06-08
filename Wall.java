/*
 * This code is protected under the Gnu General Public License (Copyleft), 2005 by
 * IBM and the Computer Science Teachers of America organization. It may be freely
 * modified and redistributed under educational fair use.
 */

import csta.ibm.pong.GameObject;

public class Wall extends GameObject{
	public Wall(int x, int y, int width, int height) {
		setX(x);
		setY(y);
		setSize(width, height);
	}
	public void act() {
		
	}
}
