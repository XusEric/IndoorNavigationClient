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
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.pos.rssi.kmeans;
import com.pos.rssi.kmeansmodel;
import com.pos.entity.FingerDataModel;
import com.pos.rssi.gaussionmodel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
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
    private boolean isNew=true;//是否新增点
    private boolean flag=true;//采集线程标志
    private LatLng myLatLng;//当前点对象
    private Long rateNumber=(long) 1000;//线程延迟时间
    private int maxNumber=Integer.parseInt( ConfigHelper.getCollectTime());//最大采集时间（秒）
    private int clusterNum=Integer.parseInt( ConfigHelper.getClusterNum());//聚类数
    private int rssiNum=Integer.parseInt( ConfigHelper.getRssiNum());//采集数阀值
    private Date startDate;//采集开始时间
	private SQLiteDatabase sqliteDatabase=null;//sqlite操作对象
	List<ScanResult> list; //周边wifi列表
	Map<String, List<Double>> wifilist = new HashMap<String, List<Double>>();
	Map<String, String> ssidlist = new HashMap<String, String>();
	private WifiManager wifiManager;
	//声明进度条对话框  
    ProgressDialog progressDialog = null;
	
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
		
		//新建指纹点
		buttonNew.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	isNew=true;
            }  
        }); 
		//开启采集
		buttonStart.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	progressDialog.show();
	            startDate    =   new    Date(System.currentTimeMillis());//获取当前时间  
	            flag=true;
	
	    		//获取Wi-Fi Manager对象
	    		wifiManager= (WifiManager)getActivity(). getSystemService(getContext().WIFI_SERVICE);
	    		
	            DoCollect ds1 = new DoCollect("");
	            Thread t1 = new Thread(ds1);
	            t1.start();
            }  
        }); 
		//重置
		buttonReset.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	//重置所有点和指纹库
//            	//先清除图层 
//                mBaiduMap.clear(); 
            	//定义复选框选项   
                final String[] multiChoiceItems = {"FingerData","FingerIndex","MainIndex","LocationLog"};   
                //复选框默认值：false=未选;true=选中 ,各自对应items[i] 
                final boolean[] defaultSelectedStatus = {false,false,false,false}; 
                  
                //创建对话框   
                new AlertDialog.Builder(v.getContext())   
                .setTitle("复选框")//设置对话框标题   
                .setMultiChoiceItems(multiChoiceItems, defaultSelectedStatus, new OnMultiChoiceClickListener(){   
                    @Override  
                    public void onClick(DialogInterface dialog, int which,   
                            boolean isChecked) {   
                    //来回重复选择取消
                    defaultSelectedStatus[which] = isChecked; 
                    }   
                })  //设置对话框[肯定]按钮   
                .setPositiveButton("确定",new DialogInterface.OnClickListener() { 
			        @Override
			        public void onClick(DialogInterface dialog, int which) { 
				        for(int i=0;i<defaultSelectedStatus.length;i++) { 
					        if(defaultSelectedStatus[i]) { 
					        	sqliteDatabase.delete(multiChoiceItems[i], null, null); 
					        } 
				        } 
				        // TODO Auto-generated method stub  
				        Toast.makeText(getContext(),"删除成功 ",Toast.LENGTH_SHORT).show();
			        } 
		        }) 
                .setNegativeButton("取消", null)
                .show();
            }  
        }); 
		
		//分类
		buttonCluster.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
	    		progressDialog.show();
            	DoCluster ds2 = new DoCluster();
	            Thread t2 = new Thread(ds2);
	            t2.start();
            }  
        }); 
        
        //*********打开数据库相关**********
        // 创建了一个DatabaseHelper对象 
        DataBaseHelper dbHelper = new DataBaseHelper(this.getContext(),"MyWifiCollect",null,1);  
        // 创建或打开一个连接  
        sqliteDatabase = dbHelper.getWritableDatabase(); 
        
        //初始化地图线程
        DoDrawMap ds2 = new DoDrawMap(view);
        Thread t2 = new Thread(ds2);
        t2.start();
        
        InitProgress(view);
		return view;
	}

	public void InitProgress(View view ){
		//创建ProgressDialog对象  
        progressDialog = new ProgressDialog(view.getContext());  
        // 设置进度条风格，风格为圆形，旋转的  
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
        // 设置ProgressDialog 标题  
        progressDialog.setTitle("提示");  
        // 设置ProgressDialog 提示信息  
        progressDialog.setMessage("处理中...");   
        // 设置ProgressDialog 的进度条是否不明确  
        progressDialog.setIndeterminate(false);           
        // 设置ProgressDialog 是否可以按退回按键取消  
        progressDialog.setCancelable(false);
        // 让ProgressDialog显示  
        //progressDialog.show(); 
	}

	public void InitMap(View view ){
		//获取地图控件引用  
        mMapView = (MapView) view.findViewById(R.id.bmapViewfinger); 
        mMapView.showScaleControl(false);// 不显示默认比例尺控件
        mBaiduMap=mMapView.getMap();  
        //空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
        //设置缩放级别
        float zoomLevel = 3;
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
        mBaiduMap.animateMapStatus(u);
        //获取地图数据
        String floor=ConfigHelper.getFloor();
    	Cursor cursor = sqliteDatabase.rawQuery("select * from Map where Floor=? ", new String[]{floor});
    	cursor.moveToFirst(); 
    	String polygon="";
    	if(cursor.getCount()!=0)
        	polygon= cursor.getString(cursor.getColumnIndex("Polygon"));
        String[] pts=polygon.split(";");
        int color=0xFFFFFF00;
        String Title="";
        for(int i=0;i<pts.length&&pts[i]!="";i++){
        	List<LatLng> ptall = new ArrayList<LatLng>();
        	String[] all=pts[i].split(":");
        	String[] pt=all[0].split("\\|");//多边形坐标
        	String[] attr=all[1].split("\\|");//多边形属性,颜色|中心
        	switch(Integer.parseInt(attr[0])){
	        	case 1:
	        		color=0xAAE5F2FF;
	        		Title="cart";
	        		break;
	        	case 2:
	        		color=0xAAffccff;
	        		Title="服装";
	        		break;
	        	case 3:
	        		color=0xAAe6b3cc;
	        		Title="餐厅";
	        		break; 
        	}
        	for(int j=0;j<pt.length&&pt[j]!="";j++){
        		LatLng latlng = new LatLng(Double.parseDouble(pt[j].split(",")[0]), Double.parseDouble(pt[j].split(",")[1]));
        		ptall.add(latlng);
        	}
        	//构建用户绘制多边形的Option对象  
            OverlayOptions polygonOption = new PolygonOptions()  
                .points(ptall)  
                .stroke(new Stroke(5, 0xAAd9d9d9))  
                .fillColor(color);  
            //在地图上添加多边形Option，用于显示  
            mBaiduMap.addOverlay(polygonOption);
            //添加标题、说明
//            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.cart); 
//            LatLng point = new LatLng(Double.parseDouble(attr[1].split(",")[0]), Double.parseDouble(attr[1].split(",")[1])); 
//	          MarkerOptions options = new MarkerOptions().position(point) 
//	          .icon(bitmap)
//	          .title(Title)
//	          .zIndex(9)  //设置marker所在层级
//	          .draggable(true);  //设置手势拖拽
//	        mBaiduMap.addOverlay(options);
        } 
        
        mBaiduMap.setOnMapClickListener(new OnMapClickListener() { 
        	   
            //点击地图监听 
            @Override 
            public void onMapClick(LatLng latLng) { 
                //获取经纬度 
            	myLatLng=latLng;
                double latitude = latLng.latitude; 
                double longitude = latLng.longitude; 
                System.out.println("latitude=" + latitude + ",longitude=" + longitude); 
                //先清除图层 
                //mBaiduMap.clear(); 
                //先清除当前图层 
                if(myOverlay!=null){
                	if(!isNew)
                		myOverlay.remove();
                }
                // 定义Maker坐标点 
                LatLng point = new LatLng(latitude, longitude); 
                DotOptions op=new  DotOptions().center(point) 
                        .color(0XFFfaa755)
                        .radius(25)
                        .zIndex(9);  //设置marker所在层级
                // 在地图上添加Marker，并显示 
                myOverlay=mBaiduMap.addOverlay(op); 
                isNew=false;
            }

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				return false;
			} 
        });  
        //**************展示已采集指纹***************
        int index=1;
        //获取地图数据
    	cursor = sqliteDatabase.rawQuery("select distinct Lat,Lng from FingerData  ", new String[0]);
    	while (cursor.moveToNext()) { 
    		double latitude = cursor.getDouble(cursor.getColumnIndex("Lat"));
    		double longitude = cursor.getDouble(cursor.getColumnIndex("Lng"));
    		// 定义Maker坐标点 
            LatLng point = new LatLng(latitude, longitude); 
            DotOptions op=new  DotOptions().center(point) 
                    .color(0XFFfaa755)
                    .radius(25)
                    .zIndex(9);  //设置marker所在层级
            // 在地图上添加Marker，并显示 
            mBaiduMap.addOverlay(op); 
            //构建文字Option对象，用于在地图上添加文字  
            TextOptions textOption = new TextOptions()  
                .fontSize(24)  
                .fontColor(0xFF000000)  
                .text(String.valueOf(index))  
                .position(point);  
            //在地图上添加该文字对象并显示  
            mBaiduMap.addOverlay(textOption);
            index++;
    	}
	}
	
    @Override
	public void onDestroy() {  
        super.onDestroy();  
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理  
        mMapView.onDestroy();  
    }  
    @Override  
    public void onResume() {  
        super.onResume();  
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理  
        mMapView.onResume();  
        }  
    @Override  
    public void onPause() {  
        super.onPause();  
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理  
        mMapView.onPause();  
        }
    
    //采集指纹数据线程
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
    //开启线程画室内图
    public class DoDrawMap implements Runnable {
    	private View view;

	    public DoDrawMap(View view) {
	        this.view = view;
	    }
	    public void run() {
	    	try
	    	{
	    		InitMap(view);
	    	}
	    	catch(Exception e)
	    	{
		    	e.printStackTrace();
	    	}
	    }
	}
    
    //开启分类线程
    public class DoCluster implements Runnable {
	    public void run() {
	    	Message msg = new Message();
	    	try
	    	{
            	//获取初始维度
            	Cursor cursor = sqliteDatabase.rawQuery("select MAC from FingerData group by MAC ", new String[0]);
		        Map<String,Integer> macAddress=new HashMap<String,Integer>();
		        while (cursor.moveToNext()) {  
		        	String mac=cursor.getString(cursor.getColumnIndex("MAC"));
		        	macAddress.put(mac, 0);
		        } 
		        
            	// 第一个参数String：表名  
                // 第二个参数String[]:要查询的列名  
                // 第三个参数String：查询条件  
                // 第四个参数String[]：查询条件的参数  
                // 第五个参数String:对查询的结果进行分组  
                // 第六个参数String：对分组的结果进行限制  
                // 第七个参数String：对查询的结果进行排序
            	cursor = sqliteDatabase.query("FingerData", new String[] { "ID","MAC",  
                      "Lat","Lng","Rssi" }, null, null, null, null, "Lat,Lng");  
            	
		        // 将光标移动到下一行，从而判断该结果集是否还有下一条数据，如果有则返回true，没有则返回false 
            	Map<String, Map<String,Integer>> clusterList = new HashMap<String, Map<String,Integer>>();
            	//按照指纹点分开存储
		        while (cursor.moveToNext()) {  
		        	FingerDataModel fdm=new FingerDataModel();
		        	fdm.ID = cursor.getInt(cursor.getColumnIndex("ID"));  
		        	fdm.MAC = cursor.getString(cursor.getColumnIndex("MAC"));  
		            fdm.Lat = cursor.getDouble(cursor.getColumnIndex("Lat"));  
		            fdm.Lng = cursor.getDouble(cursor.getColumnIndex("Lng"));  
		            fdm.Rssi = cursor.getInt(cursor.getColumnIndex("Rssi"));  
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
		        
		        //clusterList表示共几个指纹点，macAddress表示每个指纹点对应的RSSI值，取所有采集到的MAC作为纵向维度
		        double[][] clusterData=new double[clusterList.size()][macAddress.size()];
		        String[] clusterOrder=new String[clusterList.size()];//存储指纹顺序
		        String[] clusterMac=new String[macAddress.size()];//存储mac
		        //只遍历values
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
		        kmeansmodel km=new kmeansmodel(clusterNum,clusterData,macAddress.size());
		        //得到分类结果
		        Map<double[][],int[]> result=k.doKmeans(km);
		        
		        for(double[][] o:result.keySet()){
		        	//插入索引表
		        	for(int m=0;m<o.length;m++){
		        		for(int n=0;n<o[m].length;n++){
				        	// 创建ContentValues对象  
				            ContentValues values = new ContentValues(); 
				            values.put("IndexNum", m+1);
				            values.put("MAC", clusterMac[n]);
				            values.put("Rssi", o[m][n]);
				            sqliteDatabase.insert("MainIndex", null, values); 
		        		}
		        	}
		        }
		        for(int[] o:result.values()){
		        	//插入索引指纹关系表
		        	for(int m=0;m<o.length;m++){
		        		String[] key=clusterOrder[m].split(",");
		        		sqliteDatabase.execSQL("insert into FingerIndex (IndexNum,FPId) select ?,ID from FingerData where Lat=? and Lng=? ",
		        				new Object[] { o[m]+1, Double.parseDouble(key[0]), Double.parseDouble(key[1]) });
		        	}
		        }
		        
		        msg.what = 2;
		        mHandler.sendMessage(msg);
	    	}
	    	catch(Exception e)
	    	{
		    	e.printStackTrace();
	    	}
	    }
	}
    
    //指纹采集+高斯加权滤波
    private void CollectFinger(){
    	Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间  
    	long diff = curDate.getTime() - startDate.getTime();//这样得到的差值是微秒级别 
    	long between=(diff)/1000;//除以1000是为了转换成秒
    	if(between>=maxNumber){
    		//此点指纹采集结束
    		gaussionmodel gm=new gaussionmodel();
    		Map<String, Double> result=new HashMap<String, Double>();
    		//高斯加权滤波处理
    		for (Map.Entry<String, List<Double>> o : wifilist.entrySet()) {
    			double[] array=new double[o.getValue().size()];
    			for(int i=0;i<o.getValue().size();i++){
    				array[i]=o.getValue().get(i);
    			}
    			if(array.length>rssiNum){
    				//指定总数大于一定值时加入指纹
        			gm.setData(array);
        			double gf=gm.GaussionFilter();
        			result.put(o.getKey(), gf);
    			}
    		}

    		//插入数据库
    		for (Map.Entry<String, Double> o : result.entrySet()) {
    			if(!o.getValue().isNaN()){
    			  String ssid=ssidlist.get(o.getKey());
            		// 创建ContentValues对象  
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
    		

    		//开启扫描
    		wifiManager.startScan();
    		//获取扫描结果
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
                	Toast.makeText(getContext(),"异常提示:"+msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    System.out.println("错误:"+msg.obj.toString());
                    break;
                case 1:
                	progressDialog.cancel();
                	Toast.makeText(getContext(),"采集完毕 ",Toast.LENGTH_SHORT).show();
                	flag=false;
                	isNew=true;//采集完毕自动新增一个点
                    break;
                case 2:
                	progressDialog.cancel();
                	Toast.makeText(getContext(),"分类完毕 ",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
}
