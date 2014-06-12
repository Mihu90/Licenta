package ro.mihaisurdeanu.redfury.controllers;

import ro.mihaisurdeanu.redfury.activities.AbstractActivity;

public class MainController 
{
	private static MainController instance;
	private AbstractActivity currentActivity;
	
	public static MainController getInstance() {
		if (instance == null) {
			instance = new MainController();
		}
		
		return instance;
	}
	
	/*	
	public void changePosition(float x, float y)
	{
		char mL = 0, mR = 0;
		String format;
		
		if (y > 0) {
			format = ">";
		} else {
			format = "<";
		}
		
		if (x < 10.0f && y < 10.0f) {
			mL = (char)Math.max(0, Math.min(255, Math.round(Math.abs(y) * 25.6f)));
			mR = mL;
			if (x > 0) {
				mL = (char)Math.max(0, mL - Math.min(255, Math.round(x * 12.8f)));
			} else {
				mR = (char)Math.max(0, mR - Math.min(255, Math.round(Math.abs(x) * 12.8f)));
			}
		}
		
		if (mL == 0 || mL == 10) {
			mL++;
		}
		
		if (mR == 0 || mR == 10) {
			mR++;
		}
		
		format += "%c%c";
		
		//BluetoothController.getInstance().addMessage(String.format(format, mL, mR));
	}
	*/
	
	public void updateCurrentActivity(AbstractActivity currentActivity) {
		this.currentActivity = currentActivity;
	}
	
	public AbstractActivity getCurrentActivity() {
		return currentActivity;
	}
}
