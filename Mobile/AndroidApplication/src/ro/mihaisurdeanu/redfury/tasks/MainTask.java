package ro.mihaisurdeanu.redfury.tasks;

import junit.framework.Assert;
import ro.mihaisurdeanu.redfury.R;
import ro.mihaisurdeanu.redfury.fragments.BasicFragment;
import ro.mihaisurdeanu.redfury.threads.ConnectThread;
import ro.mihaisurdeanu.redfury.threads.ConnectedThread;
import android.os.AsyncTask;

public class MainTask extends AsyncTask<Void, String, Void> {
	private BasicFragment parentFragment;
	
	public MainTask(BasicFragment parentFragment) {
		this.parentFragment = parentFragment;
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			// Incercam sa stabilim o conexiune Bluetooth
			publishProgress(getStringFromResource(R.string.basic_step_establish_coonection));
			ConnectThread ct;
			// Pornim firul de executie necesar conectarii
			(ct = new ConnectThread()).start();
			// Asteptam pana el se termina
			ct.join();
			
			// Incepem procesul de configurare al masinii...
			publishProgress(getStringFromResource(R.string.basic_step_start_configuration));
			ConnectedThread connected;
			(connected = new ConnectedThread()).start();
			connected.addMessage("Test");
			
			// TODO: Aici se va realiza toata logica...
		} catch (Exception e) {
			
		}
		return null;
	}
	
	@Override
	protected void onProgressUpdate(String... progress) {
		Assert.assertTrue(progress.length == 1);
		String message = progress[0];
		parentFragment.updateStatus(message);
    }

	@Override
    protected void onPostExecute(Void result) {
		
    }
	
	private String getStringFromResource(int resourceID) {
		return parentFragment.getResources().getString(resourceID);
	}
}
