package ro.mihaisurdeanu.redfury.exceptions;

public class ConnectionFailedException extends BluetoothException
{
	private static final long serialVersionUID = 1L;

	public ConnectionFailedException()
	{
		super("Bluetooth connection to device has failed.");
	}
}
