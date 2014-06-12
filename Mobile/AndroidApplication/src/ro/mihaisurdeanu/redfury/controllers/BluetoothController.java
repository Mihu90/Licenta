package ro.mihaisurdeanu.redfury.controllers;

import java.util.Set;

import ro.mihaisurdeanu.redfury.configs.BluetoothStatus;
import ro.mihaisurdeanu.redfury.threads.ConnectedThread;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothController
{
	private static BluetoothController instance = null;
	private BluetoothAdapter adapter = null;
	private BluetoothStatus status;
	
	private ConnectedThread connectedThread;

	private BluetoothDevice device;
	private BluetoothSocket socket;
	
	private BluetoothController() {
		adapter = BluetoothAdapter.getDefaultAdapter();
		status = BluetoothStatus.DISCONNECTED;
	}
	
	public static BluetoothController getInstance() {
		if (instance == null) {
			instance = new BluetoothController();
		}
		
		return instance;
	}
	
	/**
	 * Verifica statusul dispozitivului Bluetooth de pe telefonul de pe care
	 * ruleaza aplicatia.
	 * @return			: Unul din urmatoarele status-uri : 
	 * 					NOT_SUPPORTED, DISABLED sau ENABLED
	 */
	public BluetoothStatus checkStatus() {
		if (adapter == null) {
		    return BluetoothStatus.NOT_SUPPORTED;
		} else {
		    if (!adapter.isEnabled()) {
		        return BluetoothStatus.DISABLED;
		    } else {
		    	return BluetoothStatus.ENABLED;
		    }
		}
	}
	
	public BluetoothDevice getDevice() {
		return device;
	}
	
	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}
	
	public BluetoothSocket getSocket() {
		return socket;
	}
	
	public BluetoothStatus getStatus() {
		return status;
	}

	/**
	 * Return the set of BluetoothDevice objects that are paired to the
	 * local adapter.
	 * 
	 * @return			: Set of BluetoothDevice objects
	 */
	public Set<BluetoothDevice> getPairedDevices() {
		return adapter.getBondedDevices();
	}
	
	/**
	 * Intoarce o referinta catre un obiect de tipul ConnectedThread.
	 */
	public ConnectedThread getConnection() {
		return connectedThread;
	}
	
	/**
	 * Metoda apelata atunci cand s-a realizat o conexiune cu un dispozitiv
	 * Bluetooth.
	 * 
	 * @param socket
	 * 		Socketul utilizat pentru comunicatie.
	 */
	public void setConnection(BluetoothSocket socket) {
		// Ce vom face daca suntem deja conectati?
		if (status == BluetoothStatus.CONNECTED) {
			// Vom incerca sa inchidem vechea conexiune.
			// TODO
		}
		
		// Conexiunea a fost stabilita
		status = BluetoothStatus.CONNECTED;
		this.socket = socket;
		
		// Comunicatia se va face pe un alt fir de executie, care va rula la
		// infinit, pana cand ii vom spune noi sa se opreasca.
		(connectedThread = new ConnectedThread()).start();
	}
}