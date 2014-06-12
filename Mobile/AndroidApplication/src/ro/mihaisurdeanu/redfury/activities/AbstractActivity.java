package ro.mihaisurdeanu.redfury.activities;

import java.lang.reflect.Method;

import ro.mihaisurdeanu.redfury.controllers.MainController;
import android.app.Activity;
import android.os.Bundle;

/**
 * Fiecare activitate nou creata o va extinde pe aceasta. Aceasta clasa
 * inglobeaza actiuni care definesc fiecare clasa in parte.
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public abstract class AbstractActivity extends Activity {
	private final int layoutResID;
	
	public AbstractActivity(int layoutResID) {
		this.layoutResID = layoutResID;
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);
        
        // Se actualizeaza activitatea curenta...
        MainController.getInstance().updateCurrentActivity(this);
        
        // Mai exista si alte lucruri care trebuie facute la crearea activitatii?
        afterCreate(savedInstanceState);
    }
	
	/**
	 * Invoca in mod dinamic o metoda din cadrul unei clase (in cazul de fata
	 * din cadrul unei activitati). Functia este necesara pentru a crea o
	 * generalizare / abstractizare la nivelul acesteia.
	 * 
	 * @param myClass
	 * 		Clasa din care se va apela metoda.
	 * @param myMethod
	 * 		Numele metodei care se va invoca.
	 * @param myParams
	 * 		O lista de parametri ce se vor pasa la acea metoda.
	 */
	public void invoke(String myMethod, Object... myParams) {
		Method[] methods = getClass().getMethods();
		for (Method method : methods) {
			if (method.getName().equals(myMethod)) {
				try { 
					method.invoke(this, myParams);
				} catch (Exception e) {
					// Exceptia ar trebui jurnalizata. Oricum codul va fi scris
					// in asa fel incat pe aceasta ramura nu se va ajunge
					// niciodata. Totusi sa jurnalizam exceptia...
					// TODO: Jurnalizare exceptie...
				}
			}
		}
		// In cazul in care metoda nu este gasita, nu se va intampla nimic!
	}
	
	protected abstract void afterCreate(Bundle savedInstanceState);
}
