package ro.mihaisurdeanu.redfury.opengl;

import rajawali.Object3D;
import rajawali.materials.Material;
import rajawali.math.vector.Vector3;
import rajawali.scene.RajawaliScene;

/**
 * Fiecare obiect 3D care se doreste a fi randat pe ecranul telefonului va
 * trebui sa exista aceasta clasa. Ea contine de fapt o serie de actiuni si
 * proprietati pe care fiecare obiect, in parte, le va avea.
 * 
 * @author 		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public abstract class AbstractObject {
	protected Object3D object;
	protected Material material;
	protected Vector3 position;
	
	/** Metoda va fi apelata la initializarea scenei si implicit a obiectului. 
	 * Orice exceptie aruncata de aici, va fi interpretata ca fiind o eroare
	 * de incarcare a obiectului, motiv pentru el nu va mai ajunge niciodata
	 * sa fie desenat pe ecran. */
	public abstract void init(RajawaliScene scene) throws Exception;
	
	/** Metoda va fi apelata la fiecare proces de desenare a obiectului. */
	public abstract void draw();
	
	/**
	 * Metoda utilitara pentru scalarea unui obiect 3D.
	 * @param x
	 * 		De cate ori va fi scalat pe axa OX?
	 * @param y
	 * 		De cate ori va fi scalat pe axa OY?
	 * @param z
	 * 		De cate ori va fi scalat pe axa OZ?
	 */
	public void setScale(double x, double y, double z) {
		object.setScale(x, y, z);
	}
	
	/**
	 * Metoda utilitara pentru rotirea unui obiect 3D. Parametrii de rotire
	 * trebuie sa fie reprezentati in grade. Ei mai apoi sunt convertiti in
	 * radiani.
	 * 
	 * @param x
	 * 		Unghiul de rotire (in grade) fata de axa OX.
	 * @param y
	 * 		Unghiul de rotire (in grade) fata de axa OY.
	 * @param z
	 * 		Unghiul de rotire (in grade) fata de axa OZ.
	 */
	public void setRotation(double x, double y, double z) {
		object.setRotation(convertToRadians(x),
						   convertToRadians(y),
						   convertToRadians(z));
	}
	
	/**
	 * Converteste o valoare din grade in radiani.
	 */
	protected double convertToRadians(double value) {
		// 180 de grade inseamna pi radiani
		// value grade inseamna x radiani
		// x = (value * pi) / 180;
		return value * Math.PI / 180.0d;
	}
}
