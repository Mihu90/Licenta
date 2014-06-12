package ro.mihaisurdeanu.redfury.algorithms;

/**
 * Clasa ce codifica structura interna a labirintului in care se plimba masina.
 * Dimensiunea labirintului este dinamica si modificaread dimensiunii acestuia
 * va face doar atunci cand este nevoie.
 * 
 * @author 		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public class Maze {
	/** Codifica labirintul care se va extinde in timp, in functie de dimensiunea
	 * finala a labirintului. */
	private short[][] maze;
	private short[][] oldMaze;
	/** Reprezinta pozitia masinii in cadrul labirintului */
	private int posX, posY;
	/** Reprezinta directia masinii in cadrul labirintului 2D.
	 * La inceput presupunem ca ea merge inainte, catre nord. */
	private Direction direction = Direction.NORTH;
	
	/** Dimensiunea curenta a labirintului (X = Y) */
	private int currentDimension = 7;
	
	public Maze() {
		// Initial labirintul va avea o dimensiune de 7 x 7 si la fiecare overflow
		// se va mari de doua ori atat pe verticala cat si pe orizontala.
		maze = new short[currentDimension][currentDimension];
	}
	
	/**
	 * Metoda prin care suntem atentionati de faptul ca masina a inaintat pe
	 * aceeasi traiectorie cu o casuta.
	 */
	public void goForward() {
		goForward(direction);
	}
	
	/**
	 * Metoda prin care suntem atentionati de faptul ca masina a facut un viraj
	 * si a inaintat o pozitie.
	 * @param dir
	 * 		S-a schimbat cumva directia. Daca da, care este noua directie?
	 */
	public void goForward(Direction dir) {
		// In cazul in care pozitia nu este valida, va trebui sa extindem mai
		// labirintul la unul ceva mai mare dupa care sa purcedem la celelalte
		// actualizari.
		if (!isAvailable(dir)) {
			extend();
		}
		
		// Inregistram faptul ca ne-am modificat pozitia.
		posX += dir.x;
		posY += dir.y;
		
		// Am schimbat cumva si directia de mers?
		direction = dir;
	}
	
	/**
	 * Verifica daca pozitia urmatoare poate fi codificata in labirintul curent
	 * sau va trebui sa il extindem.
	 * 
	 * @return
	 * 		Exista pozitia sau nu?
	 */
	private boolean isAvailable(Direction dir) {
		int newX = posX + dir.x;
		int newY = posY + dir.y;
		// Verificam sa fim in limitele dimensiunile labirintului.
		if (newX < 0 || newY < 0 
				|| newX >= currentDimension || newY >= currentDimension) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Metoda care produce extinderea labirintului si actualizeaza noii indici
	 */
	private void extend() {
		// Pastram o referinta catre vechiul labirint, pentru a putea salva ceea
		// ce am descoperit pana acum.
		oldMaze = maze;
		// Cream un labirint mai mare. Dimensiunea trebuie sa fie impara.
		int newDimension = currentDimension * 2 + 1;
		maze = new short[newDimension][newDimension];
		// Ne asiguram ca avem drum peste tot...
		setToZero(newDimension);
		// Copiem elementele din vechiul labirint in cel nou.
		int difference = (int)Math.ceil(currentDimension / 2.0d);
		for (int i = 0; i < currentDimension; i++) {
			for (int j = 0; j < currentDimension; j++) {
				maze[i + difference][j + difference] = oldMaze[i][j];
			}
		}
		// Actualizam si pozitia curenta a masinii
		posX += difference;
		posY += difference;
		// In fine, ultimul pas este sa actualizam si dimensiunea curenta a
		// labirintului, pentru ca referinta catre cel vechi se va pierde.
		currentDimension = newDimension;
		// Invoka garbage collecter-ul pentru a reduce memoria utilizata.
		oldMaze = null;
	}
	
	/**
	 * Seteaza labirintul ca fiind drum peste tot.
	 * @param dimension
	 * 		Dimensiunea labirintului. Cu siguranta va fi diferita de currentDimension
	 */
	private void setToZero(int dimension) {
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				maze[i][j] = 0;
			}
		}
	}
}
