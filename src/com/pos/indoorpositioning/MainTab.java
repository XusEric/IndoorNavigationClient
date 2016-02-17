package com.pos.indoorpositioning;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.serialization.SoapObject;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.pos.rssi.*;
import com.pos.indoorpositioning.R;
import com.pos.indoorpositioning.FingerTab.DoCollect;
import com.pos.util.ConfigHelper;
import com.pos.util.DataBaseHelper;
import com.pos.util.WebServiceHelper;
import com.pos.entity.*;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

public class MainTab extends Fragment{
//	private EditText userNameEditText;  
//    private EditText userPwdEditText;  
//    private Button loginButton; 
//    private TextView t;
    MapView mMapView = null;  
	private Button buttonLocation,buttonStop,buttonContinue,buttonLab;
	private boolean flag=true;//��λ�߳�ֹͣ��־
	private WifiManager wifiManager;
	private SQLiteDatabase sqliteDatabase=null;//sqlite��������

	List<ScanResult> list; //�ܱ�wifi�б�
	Map<String, List<Double>> wifilist = new HashMap<String, List<Double>>();
	private BaiduMap mBaiduMap;
	private Overlay  myOverlay;//��ǰ���ǵ�
    private int kNum=Integer.parseInt( ConfigHelper.getKNum());//kֵ
    BitmapDescriptor bitmap;//��λͼ��
    String FPNo="";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.maintab, container,
				false); 

		buttonLocation = (Button)view.findViewById(R.id.btnLocation);
		buttonStop = (Button)view.findViewById(R.id.btnStop);
		buttonContinue = (Button)view.findViewById(R.id.btnContinue);
		buttonLab = (Button)view.findViewById(R.id.btnLab);
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.dingwei1); 
        
		//��ȡWi-Fi Manager����
		wifiManager= (WifiManager)getActivity(). getSystemService(getContext().WIFI_SERVICE);

		//����������λ
		buttonLocation.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
	    		DoLocation ds1 = new DoLocation();
	            Thread t1 = new Thread(ds1);
	            t1.start();
            }  
        }); 
		
		//����������λ
		buttonContinue.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
	            flag=true;
	    		DoContinueLocation ds2 = new DoContinueLocation();
	            Thread t2 = new Thread(ds2);
	            t2.start();
            }  
        }); 
		

		//����ʵ�鷽ʽ��λ
		buttonLab.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	final EditText etFPNo = new EditText(v.getContext());
	    		//���뵱ǰʵ��Ķ�Ӧָ�Ƶ���
	    		new AlertDialog.Builder(v.getContext()).setTitle("����ָ�Ƶ���").setView(etFPNo)
            	.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int which) {
            		FPNo=etFPNo.getText().toString();
            		flag=true;
    	            DoLabLocation ds3 = new DoLabLocation(FPNo);
    	            Thread t3 = new Thread(ds3);
    	            t3.start();
            	}})
            	.setNegativeButton("ȡ��",null)
            	.show();
            }  
        }); 

		//ֹͣ������λ
		buttonStop.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	flag=false;
            }  
        }); 

        //*********�����ݿ����**********
        // ������һ��DatabaseHelper���� 
        DataBaseHelper dbHelper = new DataBaseHelper(this.getContext(),"MyWifiCollect",null,1);  
        // �������һ������  
        sqliteDatabase = dbHelper.getReadableDatabase(); 

		//��ʼ������ͼ
		InitMap(view);
		
//		t=(TextView) view.findViewById(R.id.kuanTextView1); 
//		loginButton=(Button) view.findViewById(R.id.login_Button);  
//		userNameEditText=(EditText) view.findViewById(R.id.userName); 
//        loginButton.setOnClickListener(new OnClickListener() {  
//            @Override  
//            public void onClick(View arg0) {  
//                String userName=userNameEditText.getText().toString();  
//                if("".equals(userName)){  
//                    Toast.makeText(MainTab.this.getActivity(), "�û�������Ϊ��", Toast.LENGTH_LONG).show();  
//                }  
//                
//                DoSomething ds1 = new DoSomething(userName);
//                Thread t1 = new Thread(ds1);
//                t1.start();
//            }  
//        });  
        
		return view;
	}
	
	public void InitMap(View view ){
		//��ȡ��ͼ�ؼ�����  
        mMapView = (MapView) view.findViewById(R.id.bmapView); 
        mMapView.showScaleControl(false);// ����ʾĬ�ϱ����߿ؼ�
        mBaiduMap=mMapView.getMap();  
        //�հ׵�ͼ, ������ͼ��Ƭ�����ᱻ��Ⱦ���ڵ�ͼ����������ΪNONE��������ʹ���������ػ�����ͼ��Ƭͼ�㡣ʹ�ó���������Ƭͼ��һ��ʹ�ã���ʡ�����������Զ�����Ƭͼ�����ٶȡ�
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
        //�������ż���
        float zoomLevel = 3;
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
        mBaiduMap.animateMapStatus(u);
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
        		color=0xAAE5F2FF;
        		Title="��װ";
        	}
        	for(int j=0;j<pt.length&&pt[j]!="";j++){
        		LatLng latlng = new LatLng(Double.parseDouble(pt[j].split(",")[0]), Double.parseDouble(pt[j].split(",")[1]));
        		ptall.add(latlng);
        	}
        	//�����û����ƶ���ε�Option����  
            OverlayOptions polygonOption = new PolygonOptions()  
                .points(ptall)  
                .stroke(new Stroke(5, 0xAAd9d9d9))  
                .fillColor(color);  
            //�ڵ�ͼ����Ӷ����Option��������ʾ  
            mBaiduMap.addOverlay(polygonOption);
            //��ӱ��⡢˵��
            BitmapDescriptor bitmapCart = BitmapDescriptorFactory.fromResource(R.drawable.cart); 
            LatLng point = new LatLng(Double.parseDouble(attr[1].split(",")[0]), Double.parseDouble(attr[1].split(",")[1])); 
	          MarkerOptions options = new MarkerOptions().position(point) 
	          .icon(bitmapCart)
	          .title(Title)
	          .zIndex(9)  //����marker���ڲ㼶
	          .draggable(true);  //����������ק
	        mBaiduMap.addOverlay(options);
        }
	}
	
	public class DoLocation implements Runnable {
	    public void run() {
	    	Message msg = new Message();
	    	try
	    	{
	    		String result=startLocation();
		    	msg.what = 1;
		    	//�����������
//		        HashMap<String, Object> paramsMap = new HashMap<String, Object>();  
//		        //�������
//		        paramsMap.put("name", name);  
//		        //�������
//		        SoapObject a= WebServiceHelper.getSoapObject("BasicData", "getUserList", null, paramsMap);
//		        System.out.println("result:"+a.toString());
		        msg.obj=result;
	            handler.sendMessage(msg);
	    	}
	    	catch(Exception e)
	    	{
		    	msg.what = -1;
		        msg.obj=e;
	            handler.sendMessage(msg);
	    		System.out.println("Error:"+e);
	    	}
	    }
	}
	
	//������λ
	public class DoContinueLocation implements Runnable {
	    public void run() {
    		Message msg = new Message();
	    	try
	    	{	    		
	    		while(flag){
		    		String result=startLocation();	    			
	    		}
	    		msg.what = 2;
	            handler.sendMessage(msg);
	    	}
	    	catch(Exception e)
	    	{
		    	msg.what = -1;
		        msg.obj=e;
	            handler.sendMessage(msg);
	    		e.printStackTrace();
	    	}
	    }
	}
	
	//ʵ��������λ������¼��־
	public class DoLabLocation implements Runnable {
		private String _fpNo;
		
		public DoLabLocation(String fpNo){
			this._fpNo=fpNo;
		}
		
	    public void run() {
    		Message msg = new Message();
	    	try
	    	{
	    		String FPNo=_fpNo; 
	    		Date    startDate,endDate;
	    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    		while(flag){
	    			startDate    =   new    Date(System.currentTimeMillis());//��ʼʱ��  
		    		String result=startLocation();
		    		endDate    =   new    Date(System.currentTimeMillis());//����ʱ��  
	    	    	long diff = endDate.getTime() - startDate.getTime();//΢�뼶��
	    	    	//long between=(diff)/1000;//����1000��Ϊ��ת������
	    	    	
	    	    	// ����ContentValues����  
                  ContentValues values = new ContentValues();
                  values.put("FPNo", FPNo);  
                  values.put("Lat", Double.parseDouble(result.split(",")[0])); 
                  values.put("Lng", Double.parseDouble(result.split(",")[1]));  
                  values.put("Duration", diff);   
                  values.put("CreateTime", formatter.format(endDate));  
                  sqliteDatabase.insert("LocationLog", null, values);
	    		}
	    		msg.what = 2;
	            handler.sendMessage(msg);
	    	}
	    	catch(Exception e)
	    	{
		    	msg.what = -1;
		        msg.obj=e;
	            handler.sendMessage(msg);
	    		e.printStackTrace();
	    	}
	    }
	}
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what)
            {
                case -1:
                	Toast.makeText(getContext(),"�쳣��ʾ:"+msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    System.out.println("����:"+msg.obj.toString());
                    break;
                case 1:
                	Toast.makeText(getContext(),"������λ��� ",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                	Toast.makeText(getContext(),"������λ ",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    
    //��λ������
    public String startLocation(){
    	//����ɨ��
		wifiManager.startScan();
		//��ȡɨ����
        list = wifiManager.getScanResults(); 
        Map<String, Double> Rssi = new HashMap<String, Double>();//�洢mac-rssiӳ���б�
        for (int i = 0; i < list.size(); i++) { 
        	Rssi.put(list.get(i).BSSID, (double)list.get(i).level);
    	}
        fingermodel testData = new fingermodel(0,0,Rssi);//����λ��ԭ��
		//��һ���������������ֵ
        //��ȡ������
    	Cursor cursor = sqliteDatabase.rawQuery("select * from MainIndex ", new String[0]);
    	//����Ŀ¼
        Map<Integer,Map<String, Double>> indexData=new HashMap<Integer,Map<String, Double>>();
        while (cursor.moveToNext()) {  
        	int key = cursor.getInt(cursor.getColumnIndex("IndexNum"));
        	String mac=cursor.getString(cursor.getColumnIndex("MAC"));
        	int rssi=cursor.getInt(cursor.getColumnIndex("Rssi"));		        	
        	if(!indexData.containsKey(key)){
            	Map<String,Double> list = new HashMap<String,Double>();
        		list.put(mac, (double)rssi);
        		indexData.put(key, list);
        	}
        	else{
        		Map<String,Double> newlist=indexData.get(key);
        		newlist.put(mac, (double)rssi);
        		indexData.put(key, newlist);
        	}
        } 

        kwnn kwnn = new kwnn();
        int nearestIndex=0;//�������ֵ
        
        double minD=0.00;
        for (Map.Entry<Integer,Map<String, Double>> entry : indexData.entrySet()) {
        	fingermodel fm=new fingermodel(0,0,entry.getValue());
        	double d=kwnn.calDistance(testData, fm);
        	if(minD==0||d<minD){
        		nearestIndex=entry.getKey();
        		minD=d;
        	}
        }
        
        //�ڶ�����ƥ�������õ���Ӧָ������
        cursor = sqliteDatabase.rawQuery("select d.MAC,d.Lat,d.Lng,d.Rssi from FingerIndex f join FingerData d on f.FPId=d.ID where f.IndexNum=? "
        						, new String[]{String.valueOf(nearestIndex)});

    	Map<String, Map<String,Double>> fingerList = new HashMap<String, Map<String,Double>>();
        while (cursor.moveToNext()) { 
        	FingerDataModel fdm=new FingerDataModel(); 
        	fdm.MAC = cursor.getString(cursor.getColumnIndex("MAC"));  
            fdm.Lat = cursor.getDouble(cursor.getColumnIndex("Lat"));  
            fdm.Lng = cursor.getDouble(cursor.getColumnIndex("Lng"));  
            fdm.Rssi = cursor.getInt(cursor.getColumnIndex("Rssi"));  
            String key=Double.toString(fdm.Lat)+","+Double.toString(fdm.Lng);
            if(!fingerList.containsKey(key)){
            	Map<String,Double> list = new HashMap<String,Double>();
        		list.put(fdm.MAC, (double)fdm.Rssi);
        		fingerList.put(key, list);
        	}
        	else{
        		Map<String,Double> newlist=fingerList.get(key);
        		newlist.put(fdm.MAC, (double)fdm.Rssi);
        		fingerList.put(key, newlist);
        	}
        }
        List<fingermodel> datas = new ArrayList<fingermodel>(); 
        for (Map.Entry<String,Map<String, Double>> entry : fingerList.entrySet()) {
        	String[] key=entry.getKey().split(",");
        	fingermodel fm=new fingermodel(Double.parseDouble(key[0]),Double.parseDouble(key[1]),entry.getValue());
        	datas.add(fm);
        }
        String result=kwnn.startkwnn(datas, testData, kNum);
        //���������ڵ�ͼ��ע
        // ����Maker����� 
        double latitude=Double.parseDouble(result.split(",")[0]);
        double longitude=Double.parseDouble(result.split(",")[1]);
        LatLng point = new LatLng(latitude, longitude); 
//        DotOptions op=new  DotOptions().center(point) 
//                .color(0XFFfaa755)
//                .radius(25)
//                .zIndex(9);  //����marker���ڲ㼶
        MarkerOptions options = new MarkerOptions().position(point) 
  	          .icon(bitmap)
  	          .title("test")
  	          .zIndex(9);  //����marker���ڲ㼶
        if(myOverlay!=null){
        	myOverlay.remove();
        }
        myOverlay=mBaiduMap.addOverlay(options); 
        
        return result;
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
}
