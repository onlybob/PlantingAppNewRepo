package com.openatk.planting.db;
import android.database.Cursor;

public class Seed {
	private Integer id = null;
	private String remote_id = null;
	private Integer hasChanged = 0;
	private String dateChanged = null;
	private String name = "";
	//private String seednotes = ""; 

	public Seed(){
		
	}

	public Integer getId() {
		return id;
	}
	

	public String getRemote_id() {
		return remote_id;
	}


	public Integer getHasChanged() {
		return hasChanged;
	}

	public String getName() {
		return name;
	}
	
	public String getDateChanged() {
		return dateChanged;
	}


	public void setId(Integer id) {
		this.id = id;
	}

	public void setRemote_id(String remote_id) {
		this.remote_id = remote_id;
	}

	public void setHasChanged(Integer hasChanged) {
		this.hasChanged = hasChanged;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setDateChanged(String dateChanged) {
		this.dateChanged = dateChanged;
	}
	
	/*public String getSeednotes() {
		return seednotes;
	}
	public void setSeednotes(String seednotes) {
		this.seednotes = seednotes;
	}*/
	public static Seed cursorToSeed(Cursor cursor) {
		if(cursor != null){
			Seed seed = new Seed();
			seed.setId(cursor.getInt(cursor.getColumnIndex(TableSeed.COL_ID)));
			seed.setRemote_id(cursor.getString(cursor.getColumnIndex(TableSeed.COL_REMOTE_ID)));
			seed.setHasChanged(cursor.getInt(cursor.getColumnIndex(TableSeed.COL_HAS_CHANGED)));
			seed.setName(cursor.getString(cursor.getColumnIndex(TableSeed.COL_NAME)));
			seed.setDateChanged(cursor.getString(cursor.getColumnIndex(TableSeed.COL_DATE_CHANGED)));
			//seed.setSeednotes(cursor.getString(cursor.getColumnIndex(TableSeed.COL_SEEDNOTES)));
			return seed;
		} else {
			return null;
		}
	}
	
	public String toString(){
		return name;
	}
}
