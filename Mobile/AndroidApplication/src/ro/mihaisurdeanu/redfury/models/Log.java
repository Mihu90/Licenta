package ro.mihaisurdeanu.redfury.models;

import java.util.Date;

/**
 * Clasa de tip model prin care se descrie un jurnal al aplicatiei si interactiunea
 * ei cu baza de date de tip SQLite.
 * 
 * @author		: Mihai Surdeanu
 * @version		: 1.0.0
 * 
 * =====================
 * Lista de modificari :
 * =====================
 * 		[1.0.0] : Versiune initiala.
 */
public class Log {
	private int id;
	private Date date;
	private String myClass, exception;
	
	public Log(int id, String myClass, String exception) {
		this(id, myClass, exception, null);
	}
	
	public Log(int id, String myClass, String exception, Date date) {
		this.id = id;
		this.myClass = myClass;
		this.exception = exception;
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getMyClass() {
		return myClass;
	}

	public void setMyClass(String myClass) {
		this.myClass = myClass;
	}

	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
	
}
