package ro.mihaisurdeanu.redfury.activities;

import ro.mihaisurdeanu.redfury.R;
import ro.mihaisurdeanu.redfury.configs.Constants;
import ro.mihaisurdeanu.redfury.tasks.SplashTask;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Activitatea de inceput a aplicatiei. Foarte importanta pentru a incarca
 * modele 3D ce vor fi utilizate mai tarziu cat si pentru a face o serie de
 * verificari cu privire la o serie de module ale aplicatiei.
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public class SplashActivity extends AbstractActivity {
	private TextView progressView;
	private SplashTask launcherTask;
	
	public SplashActivity() {
		super(R.layout.activity_splash);
	}

	@Override
	protected void afterCreate(Bundle savedInstanceState) {
		progressView = (TextView) findViewById(R.id.progressView);

		(launcherTask = new SplashTask()).execute();
	}
	
	/**
	 * Metoda prin care se realizeaza actualizarea mesajului cu statusul curent
	 * al procesului de incarcare al aplicatiei.
	 * 
	 * @param message
	 * 		Noul mesaj care se va afisa utilizatorului.
	 */
	public void updateMessage(Object message) {
		progressView.setText((String)message);
	}
	
	/**
	 * Task-ul asincron ce se afla in spatele acestei activitati va genera
	 * activitati secundare ce vor trebui sincronizate. Sincronizarea intre firele
	 * de executie se va face folosind mecanisme de tip wait / notify sincronizate
	 * chiar pe instantele respective.
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			// Activarea modulului Bluetooth
			case Constants.BT_REQUESTED_CODE:
				// Actiunea s-a terminat cu succes sau nu, iar SplashTask-ul
				// trebuie notificat pentru a isi putea continua executia.
				synchronized (launcherTask) {
					launcherTask.notify();
				}
				break;
			default:
				// Nu se intampla nimic in cadrul acestei activitati
				break;
		}
    }
	
}
