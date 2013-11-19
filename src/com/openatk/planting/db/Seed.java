package com.openatk.planting.db;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Seed {
	private Integer id = null;
	private String remote_id = null;
	private Integer hasChanged = 0;
	private String dateChanged = null;
	private String name = "";
	private String seedinfo = ""; 

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
	
	public String getSeedinfo() {
		return seedinfo;
	}
	public void setSeedinfo(String seedinfo) {
		this.seedinfo = seedinfo;
	}
	public static Seed cursorToSeed(Cursor cursor) {
		if(cursor != null){
			Seed seed = new Seed();
			seed.setId(cursor.getInt(cursor.getColumnIndex(TableSeed.COL_ID)));
			seed.setRemote_id(cursor.getString(cursor.getColumnIndex(TableSeed.COL_REMOTE_ID)));
			seed.setHasChanged(cursor.getInt(cursor.getColumnIndex(TableSeed.COL_HAS_CHANGED)));
			seed.setName(cursor.getString(cursor.getColumnIndex(TableSeed.COL_NAME)));
			seed.setDateChanged(cursor.getString(cursor.getColumnIndex(TableSeed.COL_DATE_CHANGED)));
			seed.setSeedinfo(cursor.getString(cursor.getColumnIndex(TableSeed.COL_SEEDINFO)));
			return seed;
		} else {
			return null;
		}
	}
	
	public String toString(){
		return name;
	}
	public static Seed FindSeedNoteBySeedID(SQLiteDatabase database, Integer seedID){
		if (seedID != null) {
			// Find current field
			String where = TableSeed.COL_ID + " = " + Integer.toString(seedID);
			Cursor cursor = database.query(TableSeed.TABLE_NAME,TableSeed.COLUMNS, where, null, null, null, null);
			cursor.moveToFirst(); 
			Seed seedinfoobtain = Seed.cursorToSeed(cursor);			
			cursor.close();
			return seedinfoobtain;
		} else {
			return null;
		}
	}
}
