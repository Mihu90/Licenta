package ro.mihaisurdeanu.redfury.configs;

/**
 * Enumeratie ce defineste posibilele status-uri pe care modulul Bluetooth le
 * poate avea in cadrul aplicatiei la un moment de timp.
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public enum BluetoothStatus 
{
	NOT_SUPPORTED,
	ENABLED,
	DISABLED,
	CONNECTED,
	DISCONNECTED
}
