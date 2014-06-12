package ro.mihaisurdeanu.redfury.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * Clasa wrapper pentru afisarea unui mesaj personalizat in cadrul unei activitati
 * sau a unui fragment.
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
@SuppressLint("ShowToast")
public class UserMessage {
	private Toast toast;
	
	/**
	 * Varianta de constructor ce permite afisarea mesajului pe o perioada scurta de timp.
	 */
	public UserMessage(Context context, int resId) {
		this(context, resId, Toast.LENGTH_SHORT);
	}
	
	public UserMessage(Context context, String message, int duration) {
		toast = Toast.makeText(context, message, duration);
	}
	
	public UserMessage(Context context, int resId, int duration) {
		toast = Toast.makeText(context, resId, duration);
	}
	
	/**
	 * Afiseaza mesajul utilizatorului oferind si oportunitatea de a il plasa
	 * undeva in pagina.
	 * 
	 * @param gravity
	 * 		Unde va fi plasat mesajul?
	 */
	public void show(UserMessageType type, int gravity) {
		if ((gravity & Gravity.TOP) == Gravity.TOP) {
			toast.setGravity(gravity, 0, 48);
		}
		
		show(type);
	}
	
	/**
	 * Afiseaza un mesaj personalizat. View-ul mesajului prezinta si transparenta.
	 * 
	 * @param type
	 * 		Tipul mesajului care se doreste a fi afisat.
	 */
	public void show(UserMessageType type) {
		View view = toast.getView();
		view.setAlpha(.5f);
		
		switch (type) {
			case ERROR:
				view.setBackgroundColor(Color.RED);
				break;
			case WARNING:
				view.setBackgroundColor(Color.YELLOW);
				break;
			case INFORMATION:
				view.setBackgroundColor(Color.GRAY);
				break;
		}
		
		toast.show();
	}

}
