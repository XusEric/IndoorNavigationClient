package com.pos.indoorpositioning;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class CollectService extends Service{
	private static final String TAG = "CollectService";

    private String dbName="MyWifiCollect";
    private IntentFilter      mWifiIntentFilter; 
    private BroadcastReceiver mWifiIntentReceiver; 
    private Handler           mHandler; 
    private SQLiteDatabase sqliteDatabase=null;
    private Long rateNumber=(long) 5000;

    private String filePath = "/sdcard/";
    private String fileName = "log.txt";
    private Timer timer = new Timer(true); 
    List<ScanResult> list; //周边wifi列表
    
    @Override
    public IBinder onBind(Intent intent) {
            Log.i(TAG, "onBind");
            return null;
    }

    @Override
    public void onCreate() {
            Log.i(TAG, "onCreate");
            super.onCreate();
    }

    @Override
    public void onDestroy() {
            Log.i(TAG, "onDestroy"); 
            timer.cancel();
            
            //注销广播  
            //unregisterReceiver(mWifiIntentReceiver); 
        	CloseDataBase();
        	releaseWakeLock();//释放设备电源锁
        	stopForeground(true); //恢复优先级
            super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        Bundle bundle = intent.getExtras();
        rateNumber = bundle.getLong("rate");
        
        CreateOrOpenDataBase();
		StartWifiCollect();
		//提高优先级
		Notification notification = new Notification(R.drawable.ic_launcher, "WifiCollect",
				System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, CollectTab.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "WifiCollect","Collect", pendingIntent);
		startForeground(1000,notification);//提高优先级
        return super.onStartCommand(intent, flags, startId);
    }
    

    public void StartWifiCollect(){
                 
        mWifiIntentFilter = new IntentFilter(); 
        mWifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); 
         
        mWifiIntentReceiver = new mWifiIntentReceiver(); 
        registerReceiver(mWifiIntentReceiver, mWifiIntentFilter); 
        acquireWakeLock();//获取电源锁
        timer.schedule(task,0, rateNumber); //延时1000ms后执行，1000ms执行一次
        
	}
    //TimerTask实现方式
    TimerTask task = new TimerTask(){  
        public void run() {  
        showWIFIListDetail();//搜周边所有wifi
     }  
  };
  	//Runnable实现方式
    Runnable TimerProcess = new Runnable(){ 
        public void run() { 
            showWIFIListDetail();//搜周边所有wifi
            mHandler.postDelayed(this,rateNumber); 
        } 
    } ;
    
	public void CreateOrOpenDataBase(){
		//调用静态方法创建数据库
        sqliteDatabase=openOrCreateDatabase(dbName, Context.MODE_WORLD_READABLE, null);
        //显示提示消息
        if(sqliteDatabase!=null){
            Toast.makeText(getBaseContext(),"success to create or open "+dbName,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getBaseContext(),"failure to create or open "+dbName,Toast.LENGTH_SHORT).show();
        }
    }
	

	public void CloseDataBase(){
		sqliteDatabase.close();
	}
	
	//收集扫描到的周边wifi
	public void showWIFIListDetail() 
    { 
		//获取Wi-Fi Manager对象
		WifiManager wifiManager= (WifiManager) getSystemService(WIFI_SERVICE);
		//开启扫描
		wifiManager.startScan();
		//获取扫描结果
        list = wifiManager.getScanResults(); 
         
        /*
        info.getBSSID()；      获取BSSID地址。
        info.getSSID()；       获取SSID地址。  需要连接网络的ID
        info.getIpAddress()；  获取IP地址。4字节Int, XXX.XXX.XXX.XXX 每个XXX为一个字节
        info.getMacAddress()； 获取MAC地址。
        info.getNetworkId()；  获取网络ID。
        info.getLinkSpeed()；  获取连接速度，可以让用户获知这一信息。
        info.getRssi()；       获取RSSI，RSSI就是接受信号强度指示
         */ 
         
        for(int i = 0; i < list.size(); i++)  
        {  
        	ScanResult scanResult=list.get(i);  
            SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd HH:mm:ss");
            Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间       
            String    str    =    formatter.format(curDate);
            String sql = "insert into CollectWifi(SSID,MAC,LinkSpeed,Rssi,CreateTime) values ('"+scanResult.SSID+"','"+scanResult.BSSID+"','0Mbps','"+scanResult.level+"','"+str+"')";//插入操作的SQL语句
            sqliteDatabase.execSQL(sql);//执行SQL语句
        } 
    } 
	
	
	private class mWifiIntentReceiver extends BroadcastReceiver{ 
		 
        public void onReceive(Context context, Intent intent) { 
 
            WifiInfo info = ((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo(); 
            /*
            WifiManager.WIFI_STATE_DISABLING   正在停止
            WifiManager.WIFI_STATE_DISABLED    已停止
            WifiManager.WIFI_STATE_ENABLING    正在打开
            WifiManager.WIFI_STATE_ENABLED     已开启
            WifiManager.WIFI_STATE_UNKNOWN     未知
             */ 
             
            switch (intent.getIntExtra("wifi_state", 0)) { 
            case WifiManager.WIFI_STATE_DISABLING: 
                Log.d(TAG, "WIFI STATUS : WIFI_STATE_DISABLING"); 
                break; 
            case WifiManager.WIFI_STATE_DISABLED: 
                Log.d(TAG, "WIFI STATUS : WIFI_STATE_DISABLED"); 
                break; 
            case WifiManager.WIFI_STATE_ENABLING: 
                Log.d(TAG, "WIFI STATUS : WIFI_STATE_ENABLING"); 
                break; 
            case WifiManager.WIFI_STATE_ENABLED: 
                Log.d(TAG, "WIFI STATUS : WIFI_STATE_ENABLED"); 
                break; 
            case WifiManager.WIFI_STATE_UNKNOWN: 
                Log.d(TAG, "WIFI STATUS : WIFI_STATE_UNKNOWN"); 
                break; 
        } 
        }
    } 
	private WakeLock wakeLock = null;  
	  
    /** 
     * 获取电源锁，保持该服务在屏幕熄灭后仍然继续保持运行 
     */  
    private void acquireWakeLock() {  
        if (null == wakeLock) {  
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);  
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK  
                    | PowerManager.ON_AFTER_RELEASE, getClass()  
                    .getCanonicalName());  
            if (null != wakeLock) {  
                Log.i(TAG, "call acquireWakeLock");  
                wakeLock.acquire();  
            }  
        }  
    }  
    /** 
     * 释放设备电源锁 
     */  
    private void releaseWakeLock() {  
        if (null != wakeLock && wakeLock.isHeld()) {  
            Log.i(TAG, "call releaseWakeLock");  
            wakeLock.release();  
            wakeLock = null;  
        }  
    }
}
