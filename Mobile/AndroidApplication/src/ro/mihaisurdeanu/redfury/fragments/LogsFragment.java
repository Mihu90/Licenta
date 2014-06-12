package ro.mihaisurdeanu.redfury.fragments;

import java.util.ArrayList;
import java.util.List;

import ro.mihaisurdeanu.redfury.R;
import ro.mihaisurdeanu.redfury.controllers.DatabaseController;
import ro.mihaisurdeanu.redfury.models.Log;
import android.app.Fragment;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Fragmentul ce permite vizualizarea tuturor jurnalelor din sistem, de la un
 * anumit moment moment de timp. Paginarea este folosita pentru optimizarea
 * cautarii si afisarii rezultatelor din baza de date interna.
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public class LogsFragment extends Fragment implements OnClickListener {
	
	private ArrayAdapter<String> adapter;
	private ListView listView;
    private TextView titleView;
    private Button prevButton, nextButton;
    
    private List<String> elements;
    private long totalItems;
    private int currentPage, itemsPerPage = 10, pageCount;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	elements = new ArrayList<String>();
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	    	
        return inflater.inflate(R.layout.logs, container, false);
    }
	
	 @Override
	 public void onStart() {
		 super.onStart();
		 
		 View mainView = getView();
		 titleView = (TextView)mainView.findViewById(R.id.logsTitle);
		 listView = (ListView)mainView.findViewById(R.id.logsList);
		 (prevButton = (Button)mainView.findViewById(R.id.logsPrev)).setOnClickListener(this);
		 (nextButton = (Button)mainView.findViewById(R.id.logsNext)).setOnClickListener(this);

		 // Butonul "Previous" va fi dezactivat in prima faza
		 prevButton.setEnabled(false);
		 
		 // Obtine numarul total de intrari din baza de date
		 totalItems = DatabaseController.getInstance(getActivity()).countLogs();
		 // Calculeaza numarul de pagini necesar pentru afisarea lor
		 pageCount = (int)Math.ceil(totalItems / (float)itemsPerPage);
		 
		 // Exista cel putin o pagina de incarcat?
		 if (pageCount > 0) {
			 currentPage = 1;
			 
			 // By default vom incarca prima pagina de jurnale
			 loadPage(currentPage);
		 }
	 }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			// Ce se intampla atunci cand se apasa pe butonul de "Previous"?
			case R.id.logsPrev:
				if (currentPage > 1) {
					loadPage(--currentPage);
					updateButtonState();
				}
				break;
			// Ce se intampla atunci cand se apasa pe butonul de "Next"?
			case R.id.logsNext:
				if (currentPage < pageCount) {
					loadPage(++currentPage);
					updateButtonState();
				}
				break;
			// Pe viitor, ce actiune se va intampla daca vom avea mai multe
			// butoane setate?
			default:
				break;
		}
	}
	
	/**
	 * Actualizeaza statusul butoanelor in functie de pagina curenta. Unele
	 * butoane vor fi activate altele dezactivate.
	 */
	private void updateButtonState() {
		// Cand butonul "Previous" va fi activat?		
		if (currentPage == 0) {
			prevButton.setEnabled(false);
		} else {
			prevButton.setEnabled(true);
		}
		
		// Cand butonul "Next" va fi activat?
		if (currentPage == pageCount) {
			nextButton.setEnabled(false);
		} else {
			nextButton.setEnabled(true);
		}
	}
	
	/**
     * Metoda utilitara ce permita incarcarea in lista a unor jurnale, aferente
     * unei pagini.
     * 
     * @param page
     * 		Pagina care se incearca a fi afisata. Acest numar este indexat
     * incepand cu 1.
     */
    private void loadPage(int page) {
    	// Verificam mai intai sa nu cerem o pagina inexistenta
    	if (page > pageCount) {
    		return;
    	}
    	
    	// Se actualizeaza pagina curenta
    	titleView.setText(String.format(getResources().getString(R.string.logs_title_view),
    									page,
    									pageCount));
    	
    	// Testam daca exista un adaptor pentru lista cu jurnale. Daca nu exista
    	// va trebui neaparat sa setam unul.
    	if (elements.isEmpty()) {
    		adapter = new ArrayAdapter<String>(getActivity(),
                    						   android.R.layout.simple_list_item_1,
                    						   elements);
            listView.setAdapter(adapter);
    	}
    	
    	// Obtine elementele din baza de date ce vor fi afisate.
    	elements.clear();
        List<Log> logs = DatabaseController.getInstance(getActivity())
        							 	   .getLogs((page - 1) * itemsPerPage,
        							 			    page * itemsPerPage - 1);
        for (Log log : logs) {
        	elements.add(log.getMyClass());
        }
        
        // Notifica faptul ca lista a fost modificata pentru ca update-urile
        // sa se poata propaga catre utilizator.
        
    }
}
