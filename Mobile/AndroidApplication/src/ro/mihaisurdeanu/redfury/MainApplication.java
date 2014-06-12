package ro.mihaisurdeanu.redfury;

import java.util.HashMap;
import java.util.Map;

import ro.mihaisurdeanu.redfury.fragments.BasicFragment;
import ro.mihaisurdeanu.redfury.fragments.BluetoothFragment;
import ro.mihaisurdeanu.redfury.fragments.LogsFragment;
import ro.mihaisurdeanu.redfury.fragments.SettingsFragment;

import android.app.Application;
import android.app.Fragment;

public class MainApplication extends Application {
	
	/**
	 * Meniu principal va fi alcatuit din mai multe intrari, fiecare intrare
	 * avand la randul ei mai multe subintrari.
	 */
	public static enum MenuCategory {
		GENERAL(R.string.title_menu_category_main),
		BLUETOOTH(R.string.title_menu_category_bluetooth),
		SETTINGS(R.string.title_menu_category_configs);

		private int resourceID;

		MenuCategory(int resourceID) {
			this.resourceID = resourceID;
		}

		public int getResourceID() {
			return resourceID;
		}
	}
	
	/**
	 * Fiecare subintrare va putea fi selectabila avand asociata o actiune.
	 * In principiu actiunea care se va efectua este aceea de a modifica
	 * fragmentul curent de pe fundal. Aplicatia creaza un numar foarte mic de
	 * activitati tocmai pentru a optimiza aplicatia.
	 */
	public static final class MenuItem {
		public final Class<? extends Fragment> myClass;
		public final int resourceID;
		
		public MenuItem(int resourceID, Class<? extends Fragment> myClass) {
			this.resourceID = resourceID;
			this.myClass = myClass;
		}
	}

	public static final Map<MenuCategory, MenuItem[]> ITEMS = new HashMap<MenuCategory, MenuItem[]>();
	
	@Override
	public void onCreate() {
		super.onCreate();

		ITEMS.put(MenuCategory.GENERAL, new MenuItem[] { 
				new MenuItem(R.string.title_menu_category_main,
							 BasicFragment.class),
				new MenuItem(R.string.title_menu_logs,
							 LogsFragment.class)
		});
		ITEMS.put(MenuCategory.BLUETOOTH, new MenuItem[] { 
				new MenuItem(R.string.title_menu_category_bluetooth,
							 BluetoothFragment.class)
		});
		ITEMS.put(MenuCategory.SETTINGS, new MenuItem[] {
				new MenuItem(R.string.title_menu_configs_settings,
							 SettingsFragment.class)
		});
	}

}
