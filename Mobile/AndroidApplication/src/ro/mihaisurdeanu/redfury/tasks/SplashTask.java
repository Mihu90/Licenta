package ro.mihaisurdeanu.redfury.tasks;

import junit.framework.Assert;
import ro.mihaisurdeanu.redfury.R;
import ro.mihaisurdeanu.redfury.activities.AbstractActivity;
import ro.mihaisurdeanu.redfury.activities.MainActivity;
import ro.mihaisurdeanu.redfury.configs.BluetoothStatus;
import ro.mihaisurdeanu.redfury.configs.Constants;
import ro.mihaisurdeanu.redfury.controllers.BluetoothController;
import ro.mihaisurdeanu.redfury.controllers.DatabaseController;
import ro.mihaisurdeanu.redfury.controllers.MainController;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class SplashTask extends AsyncTask<Void, String, Void> {
	private AbstractActivity parentActivity;
	
	/**
	 * Activitatea curenta nu se va schimba pe parcursul executiei task-ului
	 * asincron. Nu avem nicio problema in a obtine doar pentru o singura
	 * data activitatea curenta.
	 */
	public SplashTask() {
		parentActivity = MainController.getInstance().getCurrentActivity();
	}

	/**
	 * Metoda utilitara ce realizeaza initializarea mai multor module.
	 * 1. Modulul de baza
	 * 2. Modulul bazei de date
	 * 	-> Verifica conexiunea cu baza de date
	 * 3. Modulul bluetooth
	 * 	-> Verifica existenta acestuia
	 *  -> Activeaza modulul doar daca utilizatorul doreste acest lucru, prin
	 *  intermediul unei setari din baza de date.
	 * 4. TODO
	 */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			publishProgress(getStringFromResource(R.string.splash_load_main));
			Thread.sleep(500);
			
			publishProgress(getStringFromResource(R.string.splash_load_db));
			// TODO : Ce actiuni mai trebuie facute?
			@SuppressWarnings("unused")
			DatabaseController dbController = DatabaseController.getInstance(parentActivity);
			//dbController.addLog(new Log(0, "test", "exception"));
			dbController.getLogs(0, 3);
			
			publishProgress(getStringFromResource(R.string.splash_load_bluetooth));
			BluetoothStatus status = BluetoothController.getInstance().checkStatus();
			// Aplicatia nu poate rula fara support Bluetooth
			if (status == BluetoothStatus.NOT_SUPPORTED) {
				// TODO : Intoarce eroare
			} else {
				// Verificam daca modulul Bluetooth este dezactivat. Daca este asa
				// cum presupunem si setarea interna ne permite activarea lui la
				// initializare, ii vom oferi utilizatorului aceasta posibilitate
				if (status == BluetoothStatus.DISABLED) {
					boolean isEnabled = PreferenceManager.getDefaultSharedPreferences(parentActivity)
														 .getBoolean("setting_enable_bluetooth", true);
					if (isEnabled) {
						publishProgress("startBluetoothEnable");
						// Asteptam pana firul de executie UI ne notifica faptul ca
						// utilizatorul a ales ceva.
						synchronized (this) {
							this.wait();
						}
					}
				}
			}
			
			publishProgress(getStringFromResource(R.string.splash_load_finished));
		} catch (Exception e) {
			
		}
		return null;
	}
	
	/**
	 * La fiecare apel al metodei "publishProgress" din cadrul metodei
	 * "doInBackground" se va apela aceasta functie de callback. Ea la randul
	 * ei notifica o metoda din clasa SplashActivity, pentru a actualiza
	 * starea curenta a initializarii.
	 */
	@Override
	protected void onProgressUpdate(String... progress) {
		// Aceasta metoda va trebui sa primeasca doar un singur string ca si
		// parametru.
		Assert.assertTrue(progress.length == 1);
		String message = progress[0];
		// Sunt si cateva comenzi speciale care nu vor fi interpretate ca si
		// mesaje de actualizare.
		if (message.equals("startBluetoothEnable")) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			parentActivity.startActivityForResult(enableIntent,
												  Constants.BT_REQUESTED_CODE);
		} else {
			parentActivity.invoke("updateMessage", (Object[])progress);
		}
    }

	/**
	 * Metoda este executata pe firul de executie UI imediat dupa ce s-a terminat
	 * de executat metoda "doInbackground". In cadrul metodei de fata se va face
	 * legatura cu o alta activitate.
	 */
	@Override
    protected void onPostExecute(Void result) {
		Intent intent = new Intent(parentActivity, MainActivity.class);
		parentActivity.startActivity(intent);
	    parentActivity.finish();
    }
	
	/**
	 * Obtine o resursa de tip String din resursele interne proiectului. Am ales
	 * sa le stochez asa pentru a putea reprezenta string-uri in formatul UTF-8.
	 * (diacriticile necesita doar acest format)
	 * 
	 * @param resourceID
	 * 		ID-ul resursei care se doreste a se gasi.
	 * @return
	 * 		Resursa gasita sau null daca nu s-a putut gasi nimic.
	 */
	private String getStringFromResource(int resourceID) {
		return parentActivity.getResources().getString(resourceID);
	}

}
