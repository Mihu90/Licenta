package ro.mihaisurdeanu.redfury.configs;

/**
 * Clasa ce incapsuleaza o serie de constante ce sunt utilizate in cadrul
 * aplicatiei de fata.
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public final class Constants 
{
	/**
	 * Variabila folosita pentru activarea modulului debug.
	 * 
	 * Interpretare:
	 * - false		: doar erorile sau atentionarile sunt raportate;
	 * - true		: toate tipurile de mesaje sunt notificate (cele de mai sus
	 * 		+ mesajele de tip informatie)
	 */
	public final static boolean DEBUG = true;
	
	/**
	 * COM_PT_CMD_SIZE : integer value (must be less then 24)
	 * (Communication Protocol Command Size)
	 * 
	 * Interpretation :
	 * The maximum size for a command sended throught bluetooth 
	 * will be determined by this setting.
	 * 
	 * Recommended value : 8
	 */
	public final static char COM_PT_CMD_SIZE = 8;
	
	public final static char COM_PT_CMD_BFR = 0x10;
	
	public final static char COM_PT_CMD_SEP = '\n';
	
	public final static char BT_REQUESTED_CODE = 0x01;
	public final static char DC_REQUESTED_CODE = 0x02;
}
