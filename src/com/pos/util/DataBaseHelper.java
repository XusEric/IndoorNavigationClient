package com.pos.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


/** 
 * SQLite���ݿ������ 
 * @author xusong 
 */  
public class DataBaseHelper extends SQLiteOpenHelper {

	

	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	/** 
	 * ���ݿⴴ������
	 */  
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		/* ����ָ��table */
        String sql = "CREATE TABLE FingerData (ID INTEGER primary key autoincrement,MAC nvarchar(20) not null,Lat double,Lng double,Rssi int,SSID nvarchar(50))";
        db.execSQL(sql);
        sql = "CREATE TABLE MainIndex (ID INTEGER primary key autoincrement,IndexNum int,MAC nvarchar(20),Rssi int)";
        db.execSQL(sql);
        sql = "CREATE TABLE FingerIndex (ID INTEGER primary key autoincrement,IndexNum int,FPId int)";
        db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
//		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
//        db.execSQL(sql);
//        onCreate(db);
	}
}
