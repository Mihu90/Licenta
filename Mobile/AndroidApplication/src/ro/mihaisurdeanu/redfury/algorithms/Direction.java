package ro.mihaisurdeanu.redfury.algorithms;

/**
 * Clasa de tip enumeratie care stocheaza directiile posibile pe care masina le
 * poate avea in cadrul labirintului.
 * 
 * @author 		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public enum Direction {
	NORTH(-1, 0),
	EAST(0, +1),
	SOUTH(+1, 0),
	WEST(0, -1);
	
	public int x, y;
	
	private Direction(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
