package ro.mihaisurdeanu.redfury.threads;

import java.lang.reflect.Method;

import ro.mihaisurdeanu.redfury.controllers.BluetoothController;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * Fir de executie utilizat pentru a stabili o conexiune Bluetooth intre
 * telefonul mobil si masina.
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public class ConnectThread extends Thread 
{
    private BluetoothSocket socket;
    
    public ConnectThread() {
        
    }

    /**
     * Scopul principal al firului este acela de a crea o conexiune Bluetooth.
     * Socketul va fi mai apoi trimis catre BluetoothController.
     */
    public void run()
    {
    	// Obtine o referinta catre dispozitivul Bluetooth partener
    	BluetoothDevice device = BluetoothController.getInstance().getDevice();
    	
    	// Incearca crearea unui socket pe care se va stabili conexiunea.
    	// Masina nu va avea un identificator de tip UUID.
    	try {
        	Method method = device.getClass().getMethod("createRfcommSocket",
        										   		new Class[] {int.class});
        	// Se va folosi intotdeauna canalul numarul 1
            socket = (BluetoothSocket) method.invoke(device, 1);
        } catch (Exception e) {
        	// Socketul nu a putut fi creat.
        	// TODO
        	socket = null;
        }
    	
        // Daca socketul a putut fi creat vom incerca, mai departe, sa stabilim
    	// si conexiunea Bluetooth.
        try {
            if (socket != null) {
            	// Apelul este blocant pana cand conexiunea a fost stabilita.
            	socket.connect();
            	
            	// Seteaza socketul si implicit conexiunea care va fi utilizata
                BluetoothController.getInstance().setConnection(socket);
            }
        } catch (Exception e1) {
        	// TODO
        	// Incearca inchiderea socketului.
            try {
            	socket.close();
            } catch (Exception e2) {}
        }
        
        // Firul isi va termina executia
    }
}