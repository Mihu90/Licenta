package ro.mihaisurdeanu.redfury.fragments;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

import ro.mihaisurdeanu.redfury.R;
import ro.mihaisurdeanu.redfury.controllers.BluetoothController;
import ro.mihaisurdeanu.redfury.controllers.DatabaseController;
import ro.mihaisurdeanu.redfury.models.Setting;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * TODO
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public class BluetoothFragment extends Fragment {
    private ButtonClicked clicked;
    
    private ListView listViewPaired;
    private ListView listViewDetected;
    private ArrayAdapter<String> adapter, detectedAdapter;
    private ArrayList<BluetoothDevice> bluetoothDevices, pairedBluetoothDevices;
    private ArrayList<String> pairedDevices;
    private BluetoothAdapter bluetoothAdapter;
    
    private Button buttonSearch, buttonDesc;
    private ToggleButton buttonOnOff;
    
    private boolean disableListActions = false;
    
    private ListItemClick itemClick;
    private ListItemClickOnPaired itemClickOnPaired;
    private ListItemLongClickOnPaired itemLongClickOnPaired;
    
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // S-a gasit un nou dispozitiv
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	// Afisam un mesaj frumos pe ecran prin care il anuntam pe utilizator
            	// de faptul ca un nou dispozitiv Bluetooth a fost gasit.
                Toast.makeText(context,
                			   getResources().getString(R.string.toast_module_discovered),
                			   Toast.LENGTH_SHORT).show();

                // Obtinem o referinta catre dispozitivul Bluetooth gasit
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Mai sunt alte dispozitive in lista? Facem acest test pentru ca
                // nu dorim sa avem duplicate in liste de dispozitive gasite.
                if (bluetoothDevices.isEmpty()) {
                    detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                    bluetoothDevices.add(device);
                    detectedAdapter.notifyDataSetChanged();
                } else {
                    boolean alreadyFound = false;
                    for (BluetoothDevice tempDevice : bluetoothDevices) {
                    	// Compararea dispozitivilor se va face dupa adresa MAC
                        if (device.getAddress().equals(tempDevice.getAddress())) {
                        	alreadyFound = true;
                            // Oprim cautarea pentru a optimiza aplicatia...
                            break;
                        }
                    }
                    // Il vom adauga in lista numai daca nu exista deja...
                    if (!alreadyFound) {
                        detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                        bluetoothDevices.add(device);
                        detectedAdapter.notifyDataSetChanged();
                    }
                }
            }
            // Procesul de descoperire de-abia ce a inceput
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                // Butoanele prezente in cadrul layout-ul se vor dezactiva
            	buttonSearch.setEnabled(false);
            	buttonDesc.setEnabled(false);
            	
            	disableListActions = true;
            }
            // Procesul de descoperire s-a terminat
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	// Butoanele vor trebui activate
            	buttonSearch.setEnabled(true);
            	buttonDesc.setEnabled(true);
            	
            	disableListActions = false;
            	
            	// Il notificam si pe utilizator (asa e frumos)
            	Toast.makeText(context,
         			   getResources().getString(R.string.toast_module_discovered_finished),
         			   Toast.LENGTH_SHORT).show();
            }
            // Procesul de imperechere s-a realizat cu succes
            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            	int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
            	if (state == BluetoothDevice.BOND_BONDED) {
            		getPairedDevices();
            	}
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	clicked = new ButtonClicked();
    	
    	pairedDevices = new ArrayList<String>();
    	
    	adapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, pairedDevices);
        detectedAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	
    	bluetoothDevices = new ArrayList<BluetoothDevice>();
        pairedBluetoothDevices = new ArrayList<BluetoothDevice>();
        
        itemClick = new ListItemClick();
        itemClickOnPaired = new ListItemClickOnPaired();
        itemLongClickOnPaired = new ListItemLongClickOnPaired();
        
        // Obtine lista de dispozitive cu care suntem deja imperecheati si afiseaza-o!
        getPairedDevices();
        
        IntentFilter intentFilter = new IntentFilter();
        // Avem de-a face cu un proces asincron, de aceea vom defini cateva
        // actiuni care ne permit sa fim notificati atunci cand ceva se intampla.
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // Ne vom inregistra ca si ascultatori pentru a putea obtine notificari
        getActivity().registerReceiver(myReceiver, intentFilter);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	    	
        return inflater.inflate(R.layout.main, container, false);
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	
    	// Seteaza ascultatori pe toate butoanele prezente in cadrul layout-ului.
    	(buttonOnOff = (ToggleButton)getView().findViewById(R.id.buttonOnOff)).setOnClickListener(clicked);
    	(buttonSearch = (Button)getView().findViewById(R.id.buttonSearch)).setOnClickListener(clicked);
    	(buttonDesc = (Button)getView().findViewById(R.id.buttonDesc)).setOnClickListener(clicked);
    	
    	// Seteaza starea curenta pentru butonul de On / Off
    	if (bluetoothAdapter.isEnabled()) {
    		buttonOnOff.setChecked(true);
    	} else {
    		buttonOnOff.setChecked(false);
    	}
        
        // Seteaza adaptorii si ascultatorii pentru cele doua liste din layout
        listViewPaired = (ListView) getView().findViewById(R.id.listViewPaired);
        listViewPaired.setAdapter(adapter);
        listViewDetected = (ListView) getView().findViewById(R.id.listViewDetected);
        listViewDetected.setAdapter(detectedAdapter);
        listViewDetected.setOnItemClickListener(itemClick);
        listViewPaired.setOnItemClickListener(itemClickOnPaired);
        listViewPaired.setOnItemLongClickListener(itemLongClickOnPaired);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	// Evitam leak-urile de memorie...
    	getActivity().unregisterReceiver(myReceiver);
    }
    
    /**
     * Seteaza in cadrul unei liste dispozitive cu care exista deja o imperechere.
     */
    private void getPairedDevices() {
    	// Stergem tot ceea ce era inainte...
    	pairedDevices.clear();
    	pairedBluetoothDevices.clear();
    	
    	// Se obtin toate dispozitivile cu care suntem imprecheati
        Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
        // Fiecare dispozitiv in parte va fi adaugat in lista
        for(BluetoothDevice device : pairedDevice) {
        	pairedDevices.add(device.getName() + "\n" + device.getAddress());
        	pairedBluetoothDevices.add(device);
        }
        // Notifica UI-ul pentru a actualiza informatiile din lista
        adapter.notifyDataSetChanged();
    }
    
    /**
     * Doar dupa ce suntem in modul discovery vom putea incepe procesul de imperechere
     * cu alte dispozitive Bluetooth.
     */
    private void startDiscovery() {
        bluetoothAdapter.startDiscovery();
    }
    
    /**
     * Porneste procesul de discovery pentru noi dispozitive Bluetooth. In mod
     * implicit pentru a reduce consumul de baterie, modul discovery nu poate fi
     * activat permanent ci doar pentru o perioada limitata de timp. Vom avea
     * o setare in cadrul aplicatiei prin care putem regla acest interval.
     */
    private void makeDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
        							Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getActivity())
        											 				  .getString("setting_discover_time", "300")));
        startActivity(discoverableIntent);
    }
    
    /**
     * Realizeaza procesul de imperechere sau desperechere cu un anumit dispozitiv
     * Bluetooth primit ca si parametru. Actiunea depinde foarte mult de parametru
     * "create".
     * 
     * @param btDevice
     * 		Dispozitivul Bluetooth partener.
     * @param create
     * 		Daca acest parametru este setat pe true atunci se va incerca sa se
     * realizeze imperecherea, iar daca e setat pe false atunci se va realiza
     * desperecherea.
     * @return
     * 		A reusit actiunea dorita?
     * @throws Exception
     */
    public boolean invokeBondary(BluetoothDevice btDevice, boolean create) 
    		throws Exception {
    	// Utilizeaza reflection pentru a putea miscora numarul de linii de cod
    	// folosite pentru a implementa ceea ce ne dorim.
    	Class<?> bdClass = Class.forName("android.bluetooth.BluetoothDevice");
    	String action = (create) ? "createBond" : "removeBond";
    	Method method = bdClass.getMethod(action);
    	Boolean result = (Boolean) method.invoke(btDevice);
    	return result.booleanValue();  
    }
    
    /**
     * Clasa utilizata pentru a interpreta actiunile pe care un utilizator si le
     * doreste prin prezenta butoanelor prezente in fragment.
     */
    class ButtonClicked implements OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
            	case R.id.buttonOnOff:
            		if (bluetoothAdapter.isEnabled()) {
            			buttonOnOff.setChecked(false);
            			bluetoothAdapter.disable();
            		} else {
            			buttonOnOff.setChecked(true);
            			bluetoothAdapter.enable();
            		}
            		break;
            	case R.id.buttonSearch:
            		bluetoothDevices.clear();
            		startDiscovery();
            		break;
            	case R.id.buttonDesc:
            		makeDiscoverable();
            		break;
            	default:
            		break;
            }
        }
    }
    
    /**
     * La un simplu click pe un item din cadrul listei de dispozitive Bluetooth
     * descoperite actiunea care se va realiza va fi cea de imperechere a acestora.
     * Nu stim sigur daca procesul se finalizeaza mereu cu succes. Daca partenerul
     * nu ne va da acordul, atunci vom primi o exceptie pe care va trebui cumva sa
     * o tratam.
     */
    class ListItemClick implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent,
        						View view,
        						int position,
        						long id) {
        	if (disableListActions)
        		return;
        	
            BluetoothDevice device = bluetoothDevices.get(position);
            try {
                if (invokeBondary(device, true)) {
                	getPairedDevices();
                    adapter.notifyDataSetChanged();
                } else {
                	// TODO: Afiseaza un mesaj de eroare
                }
            } catch (Exception e) {
                // TODO: Jurnalizeaza cumva exceptia si notifica utilizatorul...
            }
        }  
    }
    
    /**
     * La un simplu click pe un item din cadrul listei de dispozitive Bluetooth
     * partenere actiunea care se va realiza va fi cea de despartire a acestora.
     */
    class ListItemClickOnPaired implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent,
        						View view,
        						int position,
        						long id) {
        	if (disableListActions)
        		return;
        	
            BluetoothDevice device = pairedBluetoothDevices.get(position);
            try {          	
                if (invokeBondary(device, false)) {
                	pairedDevices.remove(position);
                    adapter.notifyDataSetChanged();
                } else {
                	// TODO: Afiseaza un mesaj de eroare
                }
            } catch (Exception e) {
            	// TODO: Jurnalizeaza cumva exceptia si notifica utilizatorul...
            }
        }
    }
    
    /**
     * Ascultator prin care sunt interceptate operatiile de selectare mai lungi
     * pentru un item din cadrul unei liste. Menirea lui este aceea de a alege
     * dispozitivul (cu care suntem deja imperecheati) partener - cu care vom
     * incepe sa comunicam.
     */
    class ListItemLongClickOnPaired implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent,
									   View view,
									   int position,
									   long id) {
			if (disableListActions)
        		return true;
			
			// Daca modulul Bluetooth nu e pornit atunci nu putem realiza o conexiune.
			// Utilizatorul va trebui sa fie notificat de acest lucru.
			if (!bluetoothAdapter.isEnabled()) {
				// TODO
			}
			
			// Obtinem dispozitivul bluetooth selectat de utilizator.
			BluetoothDevice device = pairedBluetoothDevices.get(position);
			
			// Incercam sa salvam dispozitivul in baza de date pentru a ne conecta
			// automat data viitoare direct din interfata 3D.
			DatabaseController db = DatabaseController.getInstance(getActivity());
			Setting autoSave = db.findSettingByName(db.BLUETOOTH_SAVED_SETTING,
													null);
			// Setarea exista deja in baza de date
			if (autoSave != null) {
				// Salveaza noua adresa MAC in baza de date
				autoSave.setValue(device.getAddress());
				db.editSetting(autoSave);
			} else {
				// Creaza o noua setare cu numele si valoarea dorita.
				// Id-ul setarii nu conteaza, pentru ca tabela cu setari are
				// proprietatea de auto increment activata.
				autoSave = new Setting(0,
									   db.BLUETOOTH_SAVED_SETTING,
									   device.getAddress());
				db.addSetting(autoSave);
			}
			
			// Setam device-ul ales in cadrul controller-ului
			BluetoothController.getInstance().setDevice(device);
			
			// Daca aceasta metoda nu returneaza "true" atunci se va apela imediat
			// dupa si varianta mai scurta a selectarii unui item. Clar nu ne dorim
			// acest comportament.
			return true;
		}
    }
    
}
