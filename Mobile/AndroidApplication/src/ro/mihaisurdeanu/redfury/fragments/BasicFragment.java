package ro.mihaisurdeanu.redfury.fragments;

import java.util.Set;

import javax.microedition.khronos.opengles.GL10;

import rajawali.lights.DirectionalLight;
import rajawali.materials.Material;
import rajawali.materials.textures.ASingleTexture;
import rajawali.materials.textures.AlphaMapTexture;
import rajawali.materials.textures.ATexture.TextureException;
import rajawali.materials.textures.NormalMapTexture;
import rajawali.materials.textures.Texture;
import rajawali.primitives.Cube;
import ro.mihaisurdeanu.redfury.R;
import ro.mihaisurdeanu.redfury.controllers.BluetoothController;
import ro.mihaisurdeanu.redfury.controllers.DatabaseController;
import ro.mihaisurdeanu.redfury.models.Setting;
import ro.mihaisurdeanu.redfury.opengl.CarObject;
import ro.mihaisurdeanu.redfury.tasks.MainTask;
import ro.mihaisurdeanu.redfury.utils.UserMessage;
import ro.mihaisurdeanu.redfury.utils.UserMessageType;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BasicFragment extends AbstractFragment implements OnClickListener {
	private final int PLAY_ID_BUTTON = 0x01;
	private final int STATUS_ID_VIEW = 0x02;
	
	private TextView textView;

	@Override
	public View onCreateView(LayoutInflater inflater,
							 ViewGroup container,
							 Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		// Creaza un nou layout pentru butoanele Start / Stop
		LinearLayout linearLayout = new LinearLayout(getActivity());
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.setPadding(0, 0, 0, 14);
		linearLayout.setGravity(Gravity.BOTTOM);

		textView = new TextView(getActivity());
		textView.setId(STATUS_ID_VIEW);
		textView.setText(R.string.basic_status_text);
		textView.setTextSize(12);
		textView.setTextColor(Color.WHITE);
		textView.setPadding(16, 0, 0, 0);
		linearLayout.addView(textView);
		
		// Butonul Start / Stop
		Button button = new Button(getActivity());
		button.setId(PLAY_ID_BUTTON);
		button.setOnClickListener(this);
		button.setText(R.string.basic_start_button);
		button.setTextSize(14);
		button.setTextColor(Color.WHITE);
		button.setAlpha(.50f);
		button.setWidth(80);
		linearLayout.addView(button);

		// Adauga layout-ul intermediar in cadrul celui principal.
		mLayout.addView(linearLayout);

		return mLayout;
	}

	@Override
	public void onClick(View v) {
		int buttonId = ((Button) v).getId();
		switch (buttonId) {
			case PLAY_ID_BUTTON:
				BluetoothController bc = BluetoothController.getInstance();
				DatabaseController db = DatabaseController.getInstance(getActivity());
				
				BluetoothDevice myDevice = null;
				// Verifica daca avem un device Bluetooth setat...
				if ((myDevice = bc.getDevice()) == null) {
					// Incercam mai intai sa folosim optiunea de auto-saving
					boolean isEnabled = PreferenceManager.getDefaultSharedPreferences(getActivity())
							 							 .getBoolean("setting_autosave_device", true);
					
					// Daca setarea e activa, vom obtine toate dispozitivele cu
					// care exista pairing si il cautam pe cel din baza de date.
					if (isEnabled) {
						// Mai intai obtinem valoarea setarii din baza de date.
						// Ar trebui sa folosim si o setare default daca nu gasim
						// nimic in baza de date,
						Setting s = db.findSettingByName(db.BLUETOOTH_SAVED_SETTING,
														 new Setting(0, "", ""));
						Set<BluetoothDevice> devices = bc.getPairedDevices();
						for (BluetoothDevice device : devices) {
							if (device.getAddress().equals(s.getValue())) {
								myDevice = device;
								break;
							}
						}
					}
				}
					
				// Daca si acum nu avem niciun device setat atunci vom afisa
				// o eroare care se va afisa pe ecran.
				if (myDevice == null) {
					new UserMessage(getActivity(),
									R.string.basic_nodevice_found).show(UserMessageType.ERROR,
																		Gravity.TOP);
				}
				// Altfel este setat dispozitivul Bluetooth intern si se va
				// porni un task asincron.
				else {
					bc.setDevice(myDevice);
					
					// Task-ul va rula atata timp cat exista conexiune cu masina
					new MainTask(this).execute();
				}
				break;
			default:
				break;
		}
	}
	
	/**
	 * Actualizeaza in interfata grafica care este statusul procesului de configurare
	 * al masinii.
	 * 
	 * @param message
	 * 		Mesajul care se va afisa pe ecran.
	 */
	public void updateStatus(String message) {
		textView.setText(message);
	}
	
	@Override
	protected FragmentRenderer createRenderer() {
		return new BasicRenderer(getActivity());
	}

	private final class BasicRenderer extends FragmentRenderer {

		private DirectionalLight mainLight;
		private CarObject object;
		
		private double angleY = 0;

		public BasicRenderer(Context context) {
			super(context);
		}

		protected void initScene() {
			mainLight = new DirectionalLight(0, -1, -1);
			mainLight.setPower(.5f);
			getCurrentScene().addLight(mainLight);
			
			try {
				object = new CarObject();
				//object.init(getCurrentScene());
			} catch (Exception e) {
				// TODO
			}
			
			Material material2 = new Material();
			try {
				material2.addTexture(new Texture("name", R.drawable.wall));
				material2.setColorInfluence(0.0f);
			} catch (TextureException e) {
				e.printStackTrace();
			}

			Cube cube2 = new Cube(1);
			cube2.setMaterial(material2);
			cube2.setX(1);
			//cube2.setDoubleSided(true);
			getCurrentScene().addChild(cube2);
			
			getCurrentCamera().setPosition(0, 1, 8);
		}

		public void onDrawFrame(GL10 glUnused) {
			super.onDrawFrame(glUnused);
			
			angleY += 10.0d;
			
			//object.setRotation(0, angleY, 0);
		}
	}
}
