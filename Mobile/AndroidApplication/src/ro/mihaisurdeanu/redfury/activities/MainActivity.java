package ro.mihaisurdeanu.redfury.activities;

import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnChildClickListener;

import rajawali.RajawaliActivity;
import ro.mihaisurdeanu.redfury.MainApplication;
import ro.mihaisurdeanu.redfury.MainApplication.MenuItem;
import ro.mihaisurdeanu.redfury.R;
import ro.mihaisurdeanu.redfury.MainApplication.MenuCategory;

public class MainActivity extends RajawaliActivity implements OnChildClickListener {
	private static final String FRAGMENT_TAG = "rajawali";
	private static final String PREF_FIRST_RUN = "RajawaliActivity.PREF_FIRST_RUN";
	private static final String KEY_TITLE = MainActivity.class.getSimpleName() + ".KEY_TITLE";
	
	private DrawerLayout drawerLayout;
	private ExpandableListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Configureaza bara cu actiuni
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Configureaza layout-ul in care se va desena
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerList = (ExpandableListView) findViewById(R.id.left_drawer);
		drawerList.setGroupIndicator(null);
		drawerList.setAdapter(new MyAdapter(getApplicationContext(),
				MainApplication.ITEMS));
		drawerList.setOnChildClickListener(this);
		drawerList.setDrawSelectorOnTop(true);

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				invalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
				drawerLayout.clearFocus();				
			}
		};
		
		drawerLayout.setDrawerListener(drawerToggle);
		drawerLayout.setFocusable(false);

		if (savedInstanceState == null) {
			onChildClick(null, null, 0, 0, 0);
		} else {
			setTitle(savedInstanceState.getString(KEY_TITLE));
		}

		// La prima rulare afiseaza pagina principala
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (!prefs.contains(PREF_FIRST_RUN)) {
			prefs.edit().putBoolean(PREF_FIRST_RUN, false).apply();
			drawerLayout.openDrawer(drawerList);
		}
	}
	
	@Override
	protected void onDestroy() {
		try {
			super.onDestroy();
		} catch (Exception e) {
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (drawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		final MenuCategory category = MenuCategory.values()[groupPosition];
		final MenuItem item = MainApplication.ITEMS.get(category)[childPosition];
		launchFragment(category, item);

		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putString(KEY_TITLE, getTitle().toString());
	}

	private void launchFragment(MenuCategory category, MenuItem exampleItem) {
		final FragmentManager fragmentManager = getFragmentManager();

		drawerLayout.closeDrawers();

		// Seteaza noul titlu al fragmentului
		setTitle(getResources().getString(exampleItem.resourceID));

		final FragmentTransaction transaction = fragmentManager.beginTransaction();
		try {
			final Fragment fragment = (Fragment) exampleItem.myClass.getConstructors()[0].newInstance();

			if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) != null)
				transaction.addToBackStack(null);
			
			transaction.replace(R.id.content_frame, fragment, FRAGMENT_TAG);
			transaction.commit();
		} catch (Exception e) {
			// TODO
		}
	}
	
	private static final class MyAdapter extends BaseExpandableListAdapter {

		private static final int COLORS[] = new int[] { 0xFF0099CC,
														0xFF9933CC,
														0xFF669900,
														0xFFFF8800,
														0xFFCC0000 };

		private final Map<MenuCategory, MenuItem[]> items;
		private final LayoutInflater inflater;
		private final MenuCategory[] keys;

		public MyAdapter(Context context, Map<MenuCategory, MenuItem[]> items) {
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.items = items;
			keys = MenuCategory.values();
		}

		@Override
		public MenuItem getChild(int groupPosition, int childPosition) {
			return items.get(keys[groupPosition])[childPosition];
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			final MenuItem item = getChild(groupPosition, childPosition);
			final ViewHolder holder;

			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.drawer_list_child_item, null);
				holder = new ViewHolder(convertView);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.textViewItemTitle.setText(parent.getResources()
												   .getString(item.resourceID));

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return items.get(keys[groupPosition]).length;
		}

		@Override
		public MenuCategory getGroup(int groupPosition) {
			return keys[groupPosition];
		}

		@Override
		public int getGroupCount() {
			return keys.length;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			final String groupName = parent.getResources().getString(getGroup(groupPosition).getResourceID());
			final ViewHolder holder;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.drawer_list_group_item, null);
				holder = new ViewHolder(convertView);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.imageViewItemColor.setBackgroundColor(COLORS[groupPosition
					% COLORS.length]);
			holder.textViewItemTitle.setBackgroundColor(COLORS[groupPosition
					% COLORS.length] - 0x00000110);
			holder.textViewItemTitle.setText(groupName);

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
	
	private static final class ViewHolder {
		public ImageView imageViewItemColor;
		public TextView textViewItemTitle;

		public ViewHolder(View convertView) {
			imageViewItemColor = (ImageView) convertView.findViewById(R.id.item_color);
			textViewItemTitle = (TextView) convertView.findViewById(R.id.item_text);
			convertView.setTag(this);
		}
	}
}
