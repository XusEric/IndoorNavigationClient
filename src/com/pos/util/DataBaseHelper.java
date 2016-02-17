package com.pos.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


/** 
 * SQLite数据库帮助类 
 * @author xusong 
 */  
public class DataBaseHelper extends SQLiteOpenHelper {

	

	public DataBaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	/** 
	 * 数据库创建及打开
	 */  
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		/* 建立指纹table */
        String sql = "CREATE TABLE FingerData (ID INTEGER primary key autoincrement,MAC nvarchar(20) not null,Lat double,Lng double,Rssi int,SSID nvarchar(50))";
        db.execSQL(sql);
        sql = "CREATE TABLE MainIndex (ID INTEGER primary key autoincrement,IndexNum int,MAC nvarchar(20),Rssi int)";
        db.execSQL(sql);
        sql = "CREATE TABLE FingerIndex (ID INTEGER primary key autoincrement,IndexNum int,FPId int)";
        db.execSQL(sql);
        sql = "CREATE TABLE Map (ID INTEGER primary key autoincrement,Floor nvarchar(10),Polygon text)";
        db.execSQL(sql);
        sql = "CREATE TABLE FingerMap (ID INTEGER primary key autoincrement,FPId int,MapId int)";
        db.execSQL(sql);
        sql = "CREATE TABLE Time (ID INTEGER primary key autoincrement,Week int,Time time,Range int)";
        db.execSQL(sql);
        sql = "CREATE TABLE TimeIndex (ID INTEGER primary key autoincrement,TenseId int,IndexNum int)";
        db.execSQL(sql);
        sql = "CREATE TABLE CollectWifi (ID INTEGER primary key autoincrement,SSID nvarchar(50),MAC nvarchar(20),LinkSpeed nvarchar(10),Rssi int,CreateTime datetime)";
        db.execSQL(sql);
        sql = "CREATE TABLE LocationLog (ID INTEGER primary key autoincrement,FPNo nvarchar(50),Lat double,Lng double,Duration int,CreateTime datetime)";
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
