package ro.mihaisurdeanu.redfury.threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import ro.mihaisurdeanu.redfury.controllers.BluetoothController;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * Trimiterea si primirea mesajelor prin Bluetooth se va realiza asincron,
 * folosind acest fir de executie.
 * 
 * @author 		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public class ConnectedThread extends Thread {
	
    private final BluetoothSocket socket;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;
    private BufferedReader inputReader = null;
    private BufferedWriter outputWriter = null;
    
    private LinkedList<String> commands = new LinkedList<String>();
    
    private int distanceTop, distanceLeft, distanceRight;
    private char commandType, commandCrc;
    
    /** Daca aceasta variabila este setata pe "true" atunci procesul de comunicatie
     * se va opri in maxim o secunda. */
    private boolean stop = false;
    
    public ConnectedThread() {
    	// Salveaza local o referinta catre socketul pe care il folosi.
        socket = BluetoothController.getInstance().getSocket();

        // Get the BluetoothSocket input and output streams
        try {
        	inputStream = socket.getInputStream();
        	outputStream = socket.getOutputStream();
        	inputReader = new BufferedReader(new InputStreamReader(inputStream));
        	outputWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
        } catch (Exception e) {
            // TODO
        }
    }
    
    /**
	 * Adauga un mesaj in coada de mesaje care va fi transmis la masina.
	 * 
	 * @param message
	 *		Mesajul care va fi transmis
	 */
	public synchronized void addMessage(String message) {
		commands.addLast(message);
	}
	
	/**
	 * Obtine un mesaj din coada de mesaje si il va scoate din lista.
	 */
	private synchronized String getMessage()	{
		if (!commands.isEmpty()) {
			return commands.remove();
		} else {
			return null;
		}
	}
    
    public void run() {
    	String messageToSend;
    	
        // Daca in cadrul blocului try apare o exceptie atunci inseamna ca am
    	// pierdut conexiunea Bluetooth.
    	try {
    		// Procesul de comunicatia va dura pana cand nu vom mai avea permisiunea.
    	    while (!stop) {
            	// Mai intai verificam daca am primit ceva...
            	while (inputStream.available() > 0) {
            		// Ca si separator de comenzi se va folosi "\n"
            		parseCommand(inputReader.readLine());
            	}
            	// Avem ceva de trimis prin Bluetooth?
            	while ((messageToSend = getMessage()) != null) {
            		// La masina, datele sunt separate intre ele folosind ca si
            		// separator caracterul - linie noua.
            		outputWriter.write(messageToSend + "\n");
            		// Datele sunt trimise imediat
            		outputWriter.flush();
            	}
            	Thread.sleep(100);
    	    }
    	} catch (Exception e) {
    		// TODO: Conexiunea a fost pierduta...
    	}
    }
    
    /**
     * Parseaza un mesaj primit de la dispozitivul partener si actualizeaza
     * informatiile din sistem.
     * 
     * @param command
     * 		Mesajul care a fost primit de la masina.
     */
    private void parseCommand(String command) {
    	// Comenzile valide trebuie sa aiba cel putin 3 caractere
    	if (command.length() < 4) {
    		return;
    	}
    	
    	commandType = command.charAt(0);
    	commandCrc = command.charAt(1);
    	if (commandType == '$') {
    		String[] temp = command.substring(2).split("\\|");
    		if (temp.length != 3) {
    			return;
    		}
    		
    		try {
    			distanceLeft = Integer.parseInt(new StringBuffer(temp[0]).reverse().toString());
    			distanceTop = Integer.parseInt(new StringBuffer(temp[1]).reverse().toString());
    			distanceRight = Integer.parseInt(new StringBuffer(temp[2]).reverse().toString());
    		} catch (NumberFormatException e) {
    			return;
    		}
    		
    		// Verificam CRC-ul. Daca e corect facem actualizarile necesare in sistem.
    		if (commandCrc == (distanceLeft + distanceRight + distanceTop) % 115 + 11) {
    			// TODO
    			Log.i("C", command);
    		}
    	}
    }

    public void cancel() 
    {
        try {
        	outputStream.write(0);
            socket.close();
        } catch (Exception e) {
            // TODO
        }
    }
}
