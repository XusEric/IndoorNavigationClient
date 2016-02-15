package com.pos.indoorpositioning;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.pos.rssi.kmeans;
import com.pos.rssi.kmeansmodel;
import com.pos.util.WebServiceHelper;
import com.pos.entity.FingerDataModel;
import com.pos.rssi.gaussionmodel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.pos.util.*;

public class FingerTab  extends Fragment{
	private BaiduMap mBaiduMap;
	private BitmapDescriptor bitmap;
	private Overlay  myOverlay;
	private Button buttonNew,buttonStart,buttonReset,buttonCluster;
    MapView mMapView = null;  
    private boolean isNew=true;//�Ƿ�������
    private boolean flag=true;//�ɼ��̱߳�־
    private LatLng myLatLng;//��ǰ�����
    private Long rateNumber=(long) 1000;//�߳��ӳ�ʱ��
    private int maxNumber=5;//���ɼ�ʱ�䣨�룩
    private Date startDate;//�ɼ���ʼʱ��
	private SQLiteDatabase sqliteDatabase=null;//sqlite��������
	List<ScanResult> list; //�ܱ�wifi�б�
	Map<String, List<Double>> wifilist = new HashMap<String, List<Double>>();
	Map<String, String> ssidlist = new HashMap<String, String>();
	private WifiManager wifiManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fingertab, container,
				false);

		buttonNew = (Button)view.findViewById(R.id.btnNew);
		buttonStart = (Button)view.findViewById(R.id.btnStart);
		buttonReset = (Button)view.findViewById(R.id.btnReset);
		buttonCluster = (Button)view.findViewById(R.id.btnCluster);
		
		//�½�ָ�Ƶ�
		buttonNew.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	isNew=true;
            }  
        }); 
		//�����ɼ�
		buttonStart.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
	            startDate    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��  
	            flag=true;
	
	    		//��ȡWi-Fi Manager����
	    		wifiManager= (WifiManager)getActivity(). getSystemService(getContext().WIFI_SERVICE);
	    		
	            DoCollect ds1 = new DoCollect("");
	            Thread t1 = new Thread(ds1);
	            t1.start();
            }  
        }); 
		//����
		buttonReset.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	//�������е��ָ�ƿ�
            	//�����ͼ�� 
                mBaiduMap.clear(); 
                sqliteDatabase.delete("FingerData", null, null);  
                sqliteDatabase.delete("FingerIndex", null, null);  
                sqliteDatabase.delete("MainIndex", null, null);  
            }  
        }); 
		
		//����
		buttonCluster.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
//            	List<FingerDataModel> allData=new ArrayList<FingerDataModel>();
            	//��ȡ��ʼά��
            	Cursor cursor = sqliteDatabase.rawQuery("select MAC from FingerData group by MAC ", new String[0]);
		        Map<String,Integer> macAddress=new HashMap<String,Integer>();
		        while (cursor.moveToNext()) {  
		        	String mac=cursor.getString(cursor.getColumnIndex("MAC"));
		        	macAddress.put(mac, 0);
		        } 
		        
            	// ��һ������String������  
                // �ڶ�������String[]:Ҫ��ѯ������  
                // ����������String����ѯ����  
                // ���ĸ�����String[]����ѯ�����Ĳ���  
                // ���������String:�Բ�ѯ�Ľ�����з���  
                // ����������String���Է���Ľ����������  
                // ���߸�����String���Բ�ѯ�Ľ����������
            	cursor = sqliteDatabase.query("FingerData", new String[] { "ID","MAC",  
                      "Lat","Lng","Rssi" }, null, null, null, null, "Lat,Lng");  
            	double oldLat,oldLng;
            	Map<String, Map<String,Integer>> clusterList = new HashMap<String, Map<String,Integer>>();
		        // ������ƶ�����һ�У��Ӷ��жϸý�����Ƿ�����һ�����ݣ�������򷵻�true��û���򷵻�false 
            	//����ָ�Ƶ�ֿ��洢
		        while (cursor.moveToNext()) {  
		        	FingerDataModel fdm=new FingerDataModel();
		        	fdm.ID = cursor.getInt(cursor.getColumnIndex("ID"));  
		        	fdm.MAC = cursor.getString(cursor.getColumnIndex("MAC"));  
		            fdm.Lat = cursor.getDouble(cursor.getColumnIndex("Lat"));  
		            fdm.Lng = cursor.getDouble(cursor.getColumnIndex("Lng"));  
		            fdm.Rssi = cursor.getInt(cursor.getColumnIndex("Rssi"));  
//		            allData.add(fdm);
		            String key=Double.toString(fdm.Lat)+","+Double.toString(fdm.Lng);
		            if(!clusterList.containsKey(key)){
		            	Map<String,Integer> list = new HashMap<String,Integer>();
		            	list.putAll(macAddress);
	            		list.put(fdm.MAC, fdm.Rssi);
	            		clusterList.put(key, list);
	            	}
	            	else{
	            		Map<String,Integer> newlist=clusterList.get(key);
	            		newlist.put(fdm.MAC, fdm.Rssi);
	            		clusterList.put(key, newlist);
	            	}
		        } 
		        
		        //clusterList��ʾ������ָ�Ƶ㣬macAddress��ʾÿ��ָ�Ƶ��Ӧ��RSSIֵ��ȡ���вɼ�����MAC��Ϊ����ά��
		        double[][] clusterData=new double[clusterList.size()][macAddress.size()];
		        String[] clusterOrder=new String[clusterList.size()];//�洢ָ��˳��
		        String[] clusterMac=new String[macAddress.size()];//�洢mac
		        //ֻ����values
		        int i=0,j=0;
		        for (Map.Entry<String, Map<String,Integer>> o : clusterList.entrySet()) {
		        	clusterOrder[i]=o.getKey();
		        	for (Map.Entry<String,Integer> r : o.getValue().entrySet()) {
		        		clusterData[i][j]=r.getValue();
		        		clusterMac[j]=r.getKey();
		        		j++;
		        	}
		        	i++;
		        	j=0;
		        }
		        kmeans k=new kmeans();
		        kmeansmodel km=new kmeansmodel(2,clusterData,macAddress.size());
		        //�õ�������
		        Map<double[][],int[]> result=k.doKmeans(km);
		        int index=0;
		        for(double[][] o:result.keySet()){
		        	//����������
		        	for(int m=0;m<o.length;m++){
		        		for(int n=0;n<o[m].length;n++){
				        	// ����ContentValues����  
				            ContentValues values = new ContentValues(); 
				            values.put("IndexNum", m+1);
				            values.put("MAC", clusterMac[n]);
				            values.put("Rssi", o[m][n]);
				            sqliteDatabase.insert("MainIndex", null, values); 
		        		}
		        	}
		        }
		        for(int[] o:result.values()){
		        	//��������ָ�ƹ�ϵ��
		        	for(int m=0;m<o.length;m++){
		        		String[] key=clusterOrder[m].split(",");
		        		sqliteDatabase.execSQL("insert into FingerIndex (IndexNum,FPId) select ?,ID from FingerData where Lat=? and Lng=? ",
		        				new Object[] { o[m]+1, Double.parseDouble(key[0]), Double.parseDouble(key[1]) });
		        	}
		        }
	        	
            }  
        }); 
        //��ȡ��ͼ�ؼ�����  
//        mMapView = (MapView)view.findViewById(R.id.bmapViewfinger); 
//        mBaiduMap=mMapView.getMap();  
//        //�հ׵�ͼ, ������ͼ��Ƭ�����ᱻ��Ⱦ���ڵ�ͼ����������ΪNONE��������ʹ���������ػ�����ͼ��Ƭͼ�㡣ʹ�ó���������Ƭͼ��һ��ʹ�ã���ʡ�����������Զ�����Ƭͼ�����ٶȡ�
//        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
//        // ɾ���ٶȵ�ͼLoGo 
//        //mMapView.removeViewAt(1);  
//        mMapView.showScaleControl(false);// ����ʾĬ�ϱ����߿ؼ�
        // ����markerͼ�� 
//        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.btn_wantknow_pre); 
        
        
        //*********�����ݿ����**********
        // ������һ��DatabaseHelper���� 
        DataBaseHelper dbHelper = new DataBaseHelper(this.getContext(),"MyWifiCollect",null,1);  
        // �������һ������  
        sqliteDatabase = dbHelper.getWritableDatabase(); 
        

		InitMap(view);
		return view;
	}


	public void InitMap(View view ){
		//��ȡ��ͼ�ؼ�����  
        mMapView = (MapView) view.findViewById(R.id.bmapViewfinger); 
        mMapView.showScaleControl(false);// ����ʾĬ�ϱ����߿ؼ�
        mBaiduMap=mMapView.getMap();  
        //�հ׵�ͼ, ������ͼ��Ƭ�����ᱻ��Ⱦ���ڵ�ͼ����������ΪNONE��������ʹ���������ػ�����ͼ��Ƭͼ�㡣ʹ�ó���������Ƭͼ��һ��ʹ�ã���ʡ�����������Զ�����Ƭͼ�����ٶȡ�
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
        //��ȡ��ͼ����
    	Cursor cursor = sqliteDatabase.rawQuery("select * from Map where Floor=? ", new String[]{"1"});
    	cursor.moveToFirst(); 
        String polygon= cursor.getString(cursor.getColumnIndex("Polygon"));
        String[] pts=polygon.split(";");
        int color=0xFFFFFF00;
        String Title="";
        for(int i=0;i<pts.length&&pts[i]!="";i++){
        	List<LatLng> ptall = new ArrayList<LatLng>();
        	String[] all=pts[i].split(":");
        	String[] pt=all[0].split("\\|");//���������
        	String[] attr=all[1].split("\\|");//���������,��ɫ|����
        	switch(Integer.parseInt(attr[0])){
        	case 1:
        		color=0xAAFFFF00;
        		Title="��װ";
        	}
        	for(int j=0;j<pt.length&&pt[j]!="";j++){
        		LatLng latlng = new LatLng(Double.parseDouble(pt[j].split(",")[0]), Double.parseDouble(pt[j].split(",")[1]));
        		ptall.add(latlng);
        	}
        	//�����û����ƶ���ε�Option����  
            OverlayOptions polygonOption = new PolygonOptions()  
                .points(ptall)  
                .stroke(new Stroke(5, 0xAA00FF00))  
                .fillColor(color);  
            //�ڵ�ͼ����Ӷ����Option��������ʾ  
            mBaiduMap.addOverlay(polygonOption);
            //��ӱ��⡢˵��
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.btn_wantknow_pre); 
            LatLng point = new LatLng(Double.parseDouble(attr[1].split(",")[0]), Double.parseDouble(attr[1].split(",")[1])); 
	          MarkerOptions options = new MarkerOptions().position(point) 
	          .icon(bitmap)
	          .title(Title)
	          .zIndex(9)  //����marker���ڲ㼶
	          .draggable(true);  //����������ק
	        mBaiduMap.addOverlay(options);
        } 
        
        mBaiduMap.setOnMapClickListener(new OnMapClickListener() { 
        	   
            //�����ͼ���� 
            @Override 
            public void onMapClick(LatLng latLng) { 
                //��ȡ��γ�� 
            	myLatLng=latLng;
                double latitude = latLng.latitude; 
                double longitude = latLng.longitude; 
                System.out.println("latitude=" + latitude + ",longitude=" + longitude); 
                //�����ͼ�� 
                //mBaiduMap.clear(); 
                //�������ǰͼ�� 
                if(myOverlay!=null){
                	if(!isNew)
                		myOverlay.remove();
                }
                // ����Maker����� 
                LatLng point = new LatLng(latitude, longitude); 
                DotOptions op=new  DotOptions().center(point) 
                        .color(0XFFfaa755)
                        .radius(25)
                        .zIndex(9);  //����marker���ڲ㼶
                // ����MarkerOption�������ڵ�ͼ�����Marker 
//                MarkerOptions options = new MarkerOptions().position(point) 
//                        .icon(bitmap)
//                        .zIndex(9)  //����marker���ڲ㼶
//                        .draggable(true);  //����������ק
                // �ڵ�ͼ�����Marker������ʾ 
                myOverlay=mBaiduMap.addOverlay(op); 
                isNew=false;
            }

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			} 
        });  
        
	}
	
    @Override
	public void onDestroy() {  
        super.onDestroy();  
        //��activityִ��onDestroyʱִ��mMapView.onDestroy()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onDestroy();  
    }  
    @Override  
    public void onResume() {  
        super.onResume();  
        //��activityִ��onResumeʱִ��mMapView. onResume ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onResume();  
        }  
    @Override  
    public void onPause() {  
        super.onPause();  
        //��activityִ��onPauseʱִ��mMapView. onPause ()��ʵ�ֵ�ͼ�������ڹ���  
        mMapView.onPause();  
        }
    
    public class DoCollect implements Runnable {
	    private String name;

	    public DoCollect(String name) {
	        this.name = name;
	    }

	    public void run() {
	    	Message msg = new Message();
	    	try
	    	{
	    		wifilist=new HashMap<String, List<Double>>();
	    		ssidlist = new HashMap<String, String>();
	    		while(flag){
	    			CollectFinger();
	    			Thread.sleep(rateNumber);
	    		}
	    	}
	    	catch(Exception e)
	    	{
		    	msg.what = -1;
		        msg.obj=e;
		        mHandler.sendMessage(msg);
	    		System.out.println("Error:"+e);
	    	}
	    }
	}
    //ָ�Ʋɼ�+��˹��Ȩ�˲�
    private void CollectFinger(){
    	Date    curDate    =   new    Date(System.currentTimeMillis());//��ȡ��ǰʱ��  
    	long diff = curDate.getTime() - startDate.getTime();//�����õ��Ĳ�ֵ��΢�뼶�� 
    	long between=(diff)/1000;//����1000��Ϊ��ת������
    	if(between>=maxNumber){
    		//�˵�ָ�Ʋɼ�����
    		gaussionmodel gm=new gaussionmodel();
    		Map<String, Double> result=new HashMap<String, Double>();
    		//��˹��Ȩ�˲�����
    		for (Map.Entry<String, List<Double>> o : wifilist.entrySet()) {
    			double[] array=new double[o.getValue().size()];
    			for(int i=0;i<o.getValue().size();i++){
    				array[i]=o.getValue().get(i);
    			}
    			if(array.length>1){
    				//ָ����������һ��ֵʱ����ָ��
        			gm.setData(array);
        			double gf=gm.GaussionFilter();
        			result.put(o.getKey(), gf);
    			}
    		}

    		//�������ݿ�
    		for (Map.Entry<String, Double> o : result.entrySet()) {
    			if(!o.getValue().isNaN()){
    			  String ssid=ssidlist.get(o.getKey());
            		// ����ContentValues����  
                  ContentValues values = new ContentValues();
                  values.put("MAC", o.getKey());  
                  values.put("Lat", myLatLng.latitude); 
                  values.put("Lng", myLatLng.longitude);  
                  values.put("Rssi", o.getValue());   
                  values.put("SSID", ssid);  
                  sqliteDatabase.insert("FingerData", null, values);
    			}
    		}
    		Message msg = new Message();
    		msg.what = 1;
	        msg.obj="";
    		mHandler.sendMessage(msg);
    	}
    	else{
    		

    		//����ɨ��
    		wifiManager.startScan();
    		//��ȡɨ����
            list = wifiManager.getScanResults(); 
             
            for(int i = 0; i < list.size(); i++)  
            {  
            	ScanResult scanResult=list.get(i);  
            	if(!wifilist.containsKey(scanResult.BSSID)){
            		List<Double> list = new ArrayList<Double>();
            		list.add((double)scanResult.level);
            		wifilist.put(scanResult.BSSID, list);
            	}
            	else{
            		List<Double> newlist=wifilist.get(scanResult.BSSID);
            		newlist.add((double)scanResult.level);
            		wifilist.put(scanResult.BSSID, newlist);
            	}
        		ssidlist.put(scanResult.BSSID, scanResult.SSID);
            } 
    		

    	}
    }
    
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what)
            {
                case -1:
                	System.out.println("�쳣��ʾ:"+msg.obj.toString());
                    break;
                case 0:
                    System.out.println("����:"+msg.obj.toString());
                    break;
                case 1:
                	Toast.makeText(getContext(),"�ɼ���� ",Toast.LENGTH_SHORT).show();
                	flag=false;
                    break;
                default:
                    break;
            }
        }
    };
}
