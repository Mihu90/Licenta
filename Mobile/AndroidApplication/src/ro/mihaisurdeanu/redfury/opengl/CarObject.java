package ro.mihaisurdeanu.redfury.opengl;

import java.io.ObjectInputStream;

import rajawali.Object3D;
import rajawali.SerializedObject3D;
import rajawali.materials.Material;
import rajawali.materials.methods.DiffuseMethod;
import rajawali.materials.textures.Texture;
import rajawali.scene.RajawaliScene;
import ro.mihaisurdeanu.redfury.R;
import ro.mihaisurdeanu.redfury.controllers.MainController;

/**
 * Clasa ce reprezinta obiectul 3D al masinii ce se va plimba prin labirintul
 * nostru.
 * 
 * @author 		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public class CarObject extends AbstractObject {

	@Override
	public void init(RajawaliScene scene) throws Exception {
		// Cream o instanta a obiectului masina
		ObjectInputStream ois = new ObjectInputStream(MainController.getInstance()
																	.getCurrentActivity()
																	.getResources()
																	.openRawResource(R.raw.car_object));
		object = new Object3D((SerializedObject3D) ois.readObject());
		
		// Seteaza si o textura pentru obiectul curent
		Texture texture = new Texture("jetTexture", R.drawable.car_texture);
		texture.setInfluence(1.0f);

		// Seteaza si proprietatile de material ale obiectului
		material = new Material();
		material.enableLighting(true);
		material.setDiffuseMethod(new DiffuseMethod.Lambert());
		material.addTexture(texture);
		material.setColorInfluence(0);
		object.setMaterial(material);
		
		// Se adauga obiectul in cadrul scenei 3D
		scene.addChild(object);
	}

	@Override
	public void draw() {
		// TODO: Schimba pozitiile		
	}

}
