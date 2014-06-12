package ro.mihaisurdeanu.redfury.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ro.mihaisurdeanu.redfury.models.Log;
import ro.mihaisurdeanu.redfury.models.Setting;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Controller utilizat pentru a defini interactiunea dintre aplicatie si baza
 * de date de tipul SQLite.
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public class DatabaseController extends SQLiteOpenHelper
{
	private static DatabaseController instance;
	
	/** Numele bazei de date si versiunea acesteia. */
	private static final String DATABASE_NAME = "robotics.db";
	/** Versiunea curenta a bazei de date. */
	private static final int DATABASE_VERSION = 2;
	
	/** Tabela de setari. In cadrul acesteia vor fi stocate o serie de optiuni. */
	private final String TABLE_SETTINGS = "settings";
	private final String COLUMN_ID_SETTINGS = "id";
	private final String COLUMN_NAME_SETTINGS = "name";
	private final String COLUMN_VALUE_SETTINGS = "value";
	public 	final String BLUETOOTH_SAVED_SETTING = "bluetoothDeviceSaved";
	private final String CREATE_TABLE_SETTINGS = "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT)";
	private final String DROP_TABLE_SETTINGS = "DROP TABLE IF EXISTS %s";
	
	/** Tabela cu log-uri. */
	private final String TABLE_LOGS = "logs";
	private final String COLUMN_ID_LOGS = "id";
	private final String COLUMN_TIMESTAMP_LOGS = "date";
	private final String COLUMN_CLASS_LOGS = "class";
	private final String COLUMN_EXCEPTION_LOGS = "exception";
	private final String CREATE_TABLE_LOGS = "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s DATETIME DEFAULT CURRENT_TIMESTAMP)";
	private final String DROP_TABLE_LOGS = "DROP TABLE IF EXISTS %s";
	private final String COUNT_TOTAL_LOGS = "SELECT COUNT(*) FROM %s";
	
	private DatabaseController(Context context) {
		super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public static DatabaseController getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseController(context);
		}
		
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Se creaza tabela cu setarile interne aplicatiei.
        db.execSQL(String.format(CREATE_TABLE_SETTINGS,
        						 TABLE_SETTINGS,
        						 COLUMN_ID_SETTINGS,
        						 COLUMN_NAME_SETTINGS,
        						 COLUMN_VALUE_SETTINGS));
        // Se creaza tabela cu jurnalele din cadrul sistemului.
        db.execSQL(String.format(CREATE_TABLE_LOGS,
        						 TABLE_LOGS,
        						 COLUMN_ID_LOGS,
        						 COLUMN_CLASS_LOGS,
        						 COLUMN_EXCEPTION_LOGS,
        						 COLUMN_TIMESTAMP_LOGS));
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Se sterg toate tabelele posibile din baza de date
		db.execSQL(String.format(DROP_TABLE_SETTINGS,
								 TABLE_SETTINGS));
		// Se sterg toate jurnalele din baza de date
		db.execSQL(String.format(DROP_TABLE_LOGS,
								 TABLE_LOGS));
		
		// Se apeleaza din nou metoda care va crea aceste tabele cat si anumite
		// intrari din ele. Stim cu siguranta ca la apelul acestei metode nu se
		// apela si "onCreate" in mod implicit.
        onCreate(db);
	}
	
	public void onSetup() {
	}
	
	/**
	 * Cauta in tabela de setari o setare dupa numele ei.
	 * 
	 * @param name
	 * 		Numele setarii pe care o dorim sa o obtinem.
	 * @param defaultValue
	 * 		Setarea default care se va returna atunci cand nicio setare nu a fost
	 * gasita.
	 * @return
	 * 		Intoarce setarea gasita sau valoarea implicita daca nu a fost gasita
	 * sau o eroare a aparut.
	 */
	public Setting findSettingByName(String name, Setting defaultValue) {
		SQLiteDatabase db = getReadableDatabase();
	 
	    Cursor cursor = db.query(TABLE_SETTINGS, 
	    		new String[] { 	COLUMN_ID_SETTINGS, 
	    						COLUMN_NAME_SETTINGS, 
	    						COLUMN_VALUE_SETTINGS  }, 
	    		COLUMN_NAME_SETTINGS + "= ?",
	    		new String[] { name }, null, null, null, null);
	    
	    // Cursorul a gasit cel putin o intrare in tabela cu setari?
	    if (cursor != null) {
	    	try {
	    		// Mutam cursorul pe prima intrare. Asta este singura care ne
	    		// intereseaza in acest moment.
	    		cursor.moveToFirst();
	    	
	    		Setting setting = new Setting(Integer.parseInt(cursor.getString(0)),
	    								  	  cursor.getString(1),
	    								  	  cursor.getString(2));
	    		return setting;
	    	} catch (Exception ex) {
	    		// S-a produs o exceptie, cum ar fi NumberFormatException?
	    		return defaultValue;
	    	}
	    } else {
	    	// Nicio intrare nu a fost gasita.
	    	return defaultValue;
	    } 
	}
	
	/**
	 * Adauga o noua setare in tabela de setari.
	 * 
	 * @param setting
	 * 		Setarea care se va adauga.
	 * @return
	 * 		ID-ul setarii din cadrul tabelei cu acelasi nume.
	 */
	public long addSetting(Setting setting) {
		SQLiteDatabase db = getWritableDatabase();
		 
		// Seteaza datele pentru noua setare.
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_NAME_SETTINGS, setting.getName());
	    values.put(COLUMN_VALUE_SETTINGS, setting.getValue());
	 
	    // Insereaza setarea in baza de date
	    long result = db.insert(TABLE_SETTINGS, null, values);
	    
	    // Se inchide conexiunea cu baza de date.
	    db.close();  
	    return result;
	}
	
	/**
	 * Adauga un nou jurnal in sistem.
	 * 
	 * @param log
	 * 		Jurnalul care se va adauga.
	 * @return
	 * 		ID-ul jurnalului din cadrul tabelei cu acelasi nume.
	 */
	public long addLog(Log log) {
		SQLiteDatabase db = getWritableDatabase();
		
		// Seteaza datele pentru noul jurnal.
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_CLASS_LOGS, log.getMyClass());
	    values.put(COLUMN_EXCEPTION_LOGS, log.getException());
	 
	    long result = db.insert(TABLE_LOGS, null, values);
	    
	    db.close(); 
		return result;
	}
	
	/**
	 * Returneaza numarul total de jurnale gasite la un moment de timp.
	 * Metoda va fi folosita pentru paginare.
	 */
	public long countLogs() {
		SQLiteDatabase db = getReadableDatabase();
		SQLiteStatement s = db.compileStatement(String.format(COUNT_TOTAL_LOGS,
															  TABLE_LOGS));

		return s.simpleQueryForLong();
	}
	
	/**
	 * Returneaza jurnalele gasite pe pozitiile dintre start si end, sortate
	 * descrescator dupa data introducerii lor in tabela.
	 * 
	 * @param start
	 * 		Pozitia de inceput.
	 * @param end
	 * 		Pozitia de sfarsit.
	 * @return
	 * 		Lista de intrari. De obicei dimensiunea listei va fi de 10 elemente.
	 */
	public List<Log> getLogs(int start, int end) {
		SQLiteDatabase db = getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_LOGS, 
	    		new String[] { 	COLUMN_ID_LOGS, 
	    						COLUMN_CLASS_LOGS, 
	    						COLUMN_EXCEPTION_LOGS,
	    						COLUMN_TIMESTAMP_LOGS}, 
	    		null, null, null, null,
	    		COLUMN_TIMESTAMP_LOGS + " DESC", start + "," + end);
	    
	    List<Log> logs = new ArrayList<Log>();
	    
	    // Parcurgem linie cu linie query-ul obtinut.
	    while (cursor.moveToNext()) {
	    	try {
	    		Log log = new Log(Integer.parseInt(cursor.getString(0)),
	    						  cursor.getString(1),
	    						  cursor.getString(2),
	    						  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
	    								  			   Locale.ENGLISH).parse(cursor.getString(3)));
	    		logs.add(log);
	    	} catch (Exception ex) { 
	    		// S-a produs o exceptie, cum ar fi NumberFormatException?
	    		// Nu se intampla nimic. Pur si simplu nu mai luam randul in considerare.
	    	}
	    }
	    
		return logs;
	}
	
	/**
	 * Modifica o setare existenta din baza de date. Doar valoarea unei setari
	 * se va putea modifica cu aceasta functie.
	 * 
	 * @param setting
	 * 		Setarea ce se va modifica. Numele setarii este folosit ca si
	 * identificator.
	 * @return
	 * 		Numarul de randuri afectate de schimbare.
	 * Nota: Pot exista doua sau mai multe setari cu acelasi nume. Asa ca mare
	 * atentie!
	 */
	public int editSetting(Setting setting) {
		SQLiteDatabase db = getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_VALUE_SETTINGS, setting.getValue());
	 
	    // Se actualizeaza setarea / setarile dorite...
	    return db.update(TABLE_SETTINGS, values, COLUMN_NAME_SETTINGS + " = ?",
	            new String[] { setting.getName() });
	}
	
	/**
	 * Metoda utilizata pentru a sterge o setare din cadrul tabelei corespunzatoare.
	 * Actiunea este irevocabila.
	 * 
	 * @param name
	 * 		Numele setarii folosite pe post de identificator.
	 * @return
	 * 		Numarul de intrari sterse.
	 */
	public int deleteSetting(String name) {
		SQLiteDatabase db = getWritableDatabase();
		
		// Se sterge intrarea / intrarile din sistem.
	    return db.delete(TABLE_SETTINGS, COLUMN_NAME_SETTINGS + " = ?",
	            new String[] { name });
	}
}