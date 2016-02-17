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
    List<ScanResult> list; //�ܱ�wifi�б�
    
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
            
            //ע���㲥  
            //unregisterReceiver(mWifiIntentReceiver); 
        	CloseDataBase();
        	releaseWakeLock();//�ͷ��豸��Դ��
        	stopForeground(true); //�ָ����ȼ�
            super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        Bundle bundle = intent.getExtras();
        rateNumber = bundle.getLong("rate");
        
        CreateOrOpenDataBase();
		StartWifiCollect();
		//������ȼ�
		Notification notification = new Notification(R.drawable.ic_launcher, "WifiCollect",
				System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, CollectTab.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "WifiCollect","Collect", pendingIntent);
		startForeground(1000,notification);//������ȼ�
        return super.onStartCommand(intent, flags, startId);
    }
    

    public void StartWifiCollect(){
                 
        mWifiIntentFilter = new IntentFilter(); 
        mWifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); 
         
        mWifiIntentReceiver = new mWifiIntentReceiver(); 
        registerReceiver(mWifiIntentReceiver, mWifiIntentFilter); 
        acquireWakeLock();//��ȡ��Դ��
        timer.schedule(task,0, rateNumber); //��ʱ1000ms��ִ�У�1000msִ��һ��
        
	}
    //TimerTaskʵ�ַ�ʽ
    TimerTask task = new TimerTask(){  
        public void run() {  
        showWIFIListDetail();//���ܱ�����wifi
     }  
  };
  	//Runnableʵ�ַ�ʽ
    Runnable TimerProcess = new Runnable(){ 
        public void run() { 
            showWIFIListDetail();//���ܱ�����wifi
            mHandler.postDelayed(this,rateNumber); 
        } 
    } ;
    
	public void CreateOrOpenDataBase(){
		//���þ�̬�����������ݿ�
        sqliteDatabase=openOrCreateDatabase(dbName, Context.MODE_WORLD_READABLE, null);
        //��ʾ��ʾ��Ϣ
        if(sqliteDatabase!=null){
            Toast.makeText(getBaseContext(),"success to create or open "+dbName,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getBaseContext(),"failure to create or open "+dbName,Toast.LENGTH_SHORT).show();
        }
    }
	

	public void CloseDataBase(){
		sqliteDatabase.close();
	}
	
	//�ռ�ɨ�赽���ܱ�wifi
	public void showWIFIListDetail() 
    { 
		//��ȡWi-Fi Manager����
		WifiManager wifiManager= (WifiManager) getSystemService(WIFI_SERVICE);
		//����ɨ��
		wifiManager.startScan();
		//��ȡɨ����
        list = wifiManager.getScanResults(); 
         
        /*
        info.getBSSID()��      ��ȡBSSID��ַ��
        info.getSSID()��       ��ȡSSID��ַ��  ��Ҫ���������ID
        info.getIpAddress()��  ��ȡIP��ַ��4�ֽ�Int, XXX.XXX.XXX.XXX ÿ��XXXΪһ���ֽ�
        info.getMacAddress()�� ��ȡMAC��ַ��
        info.getNetworkId()��  ��ȡ����ID��
        info.getLinkSpeed()��  ��ȡ�����ٶȣ��������û���֪��һ��Ϣ��
        info.getRssi()��       ��ȡRSSI��RSSI���ǽ����ź�ǿ��ָʾ
         */ 
         
        for(int i = 0; i < list.size(); i++)  
        {  
        	ScanResult scanResult=list.get(i);  
            SimpleDateFormat    formatter    =   new    SimpleDateFormat    ("yyyy-MM-dd HH:mm:ss");
            Date    curDate    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��       
            String    str    =    formatter.format(curDate);
            String sql = "insert into CollectWifi(SSID,MAC,LinkSpeed,Rssi,CreateTime) values ('"+scanResult.SSID+"','"+scanResult.BSSID+"','0Mbps','"+scanResult.level+"','"+str+"')";//���������SQL���
            sqliteDatabase.execSQL(sql);//ִ��SQL���
        } 
    } 
	
	
	private class mWifiIntentReceiver extends BroadcastReceiver{ 
		 
        public void onReceive(Context context, Intent intent) { 
 
            WifiInfo info = ((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo(); 
            /*
            WifiManager.WIFI_STATE_DISABLING   ����ֹͣ
            WifiManager.WIFI_STATE_DISABLED    ��ֹͣ
            WifiManager.WIFI_STATE_ENABLING    ���ڴ�
            WifiManager.WIFI_STATE_ENABLED     �ѿ���
            WifiManager.WIFI_STATE_UNKNOWN     δ֪
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
     * ��ȡ��Դ�������ָ÷�������ĻϨ�����Ȼ������������ 
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
     * �ͷ��豸��Դ�� 
     */  
    private void releaseWakeLock() {  
        if (null != wakeLock && wakeLock.isHeld()) {  
            Log.i(TAG, "call releaseWakeLock");  
            wakeLock.release();  
            wakeLock = null;  
        }  
    }
}
