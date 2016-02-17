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
	private boolean flag=true;//定位线程停止标志
	private WifiManager wifiManager;
	private SQLiteDatabase sqliteDatabase=null;//sqlite操作对象

	List<ScanResult> list; //周边wifi列表
	Map<String, List<Double>> wifilist = new HashMap<String, List<Double>>();
	private BaiduMap mBaiduMap;
	private Overlay  myOverlay;//当前覆盖点
    private int kNum=Integer.parseInt( ConfigHelper.getKNum());//k值
    BitmapDescriptor bitmap;//定位图标
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
        
		//获取Wi-Fi Manager对象
		wifiManager= (WifiManager)getActivity(). getSystemService(getContext().WIFI_SERVICE);

		//开启单步定位
		buttonLocation.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
	    		DoLocation ds1 = new DoLocation();
	            Thread t1 = new Thread(ds1);
	            t1.start();
            }  
        }); 
		
		//开启连续定位
		buttonContinue.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
	            flag=true;
	    		DoContinueLocation ds2 = new DoContinueLocation();
	            Thread t2 = new Thread(ds2);
	            t2.start();
            }  
        }); 
		

		//开启实验方式定位
		buttonLab.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	final EditText etFPNo = new EditText(v.getContext());
	    		//输入当前实验的对应指纹点编号
	    		new AlertDialog.Builder(v.getContext()).setTitle("输入指纹点编号").setView(etFPNo)
            	.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int which) {
            		FPNo=etFPNo.getText().toString();
            		flag=true;
    	            DoLabLocation ds3 = new DoLabLocation(FPNo);
    	            Thread t3 = new Thread(ds3);
    	            t3.start();
            	}})
            	.setNegativeButton("取消",null)
            	.show();
            }  
        }); 

		//停止连续定位
		buttonStop.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
            	flag=false;
            }  
        }); 

        //*********打开数据库相关**********
        // 创建了一个DatabaseHelper对象 
        DataBaseHelper dbHelper = new DataBaseHelper(this.getContext(),"MyWifiCollect",null,1);  
        // 创建或打开一个连接  
        sqliteDatabase = dbHelper.getReadableDatabase(); 

		//初始化室内图
		InitMap(view);
		
//		t=(TextView) view.findViewById(R.id.kuanTextView1); 
//		loginButton=(Button) view.findViewById(R.id.login_Button);  
//		userNameEditText=(EditText) view.findViewById(R.id.userName); 
//        loginButton.setOnClickListener(new OnClickListener() {  
//            @Override  
//            public void onClick(View arg0) {  
//                String userName=userNameEditText.getText().toString();  
//                if("".equals(userName)){  
//                    Toast.makeText(MainTab.this.getActivity(), "用户名不能为空", Toast.LENGTH_LONG).show();  
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
		//获取地图控件引用  
        mMapView = (MapView) view.findViewById(R.id.bmapView); 
        mMapView.showScaleControl(false);// 不显示默认比例尺控件
        mBaiduMap=mMapView.getMap();  
        //空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
        //设置缩放级别
        float zoomLevel = 3;
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
        mBaiduMap.animateMapStatus(u);
        //获取地图数据
    	Cursor cursor = sqliteDatabase.rawQuery("select * from Map where Floor=? ", new String[]{"1"});
    	cursor.moveToFirst(); 
        String polygon= cursor.getString(cursor.getColumnIndex("Polygon"));
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
        		Title="服装";
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
            BitmapDescriptor bitmapCart = BitmapDescriptorFactory.fromResource(R.drawable.cart); 
            LatLng point = new LatLng(Double.parseDouble(attr[1].split(",")[0]), Double.parseDouble(attr[1].split(",")[1])); 
	          MarkerOptions options = new MarkerOptions().position(point) 
	          .icon(bitmapCart)
	          .title(Title)
	          .zIndex(9)  //设置marker所在层级
	          .draggable(true);  //设置手势拖拽
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
		    	//定义参数对象
//		        HashMap<String, Object> paramsMap = new HashMap<String, Object>();  
//		        //输入参数
//		        paramsMap.put("name", name);  
//		        //服务调用
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
	
	//连续定位
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
	
	//实验连续定位，并记录日志
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
	    			startDate    =   new    Date(System.currentTimeMillis());//开始时间  
		    		String result=startLocation();
		    		endDate    =   new    Date(System.currentTimeMillis());//结束时间  
	    	    	long diff = endDate.getTime() - startDate.getTime();//微秒级别
	    	    	//long between=(diff)/1000;//除以1000是为了转换成秒
	    	    	
	    	    	// 创建ContentValues对象  
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
                	Toast.makeText(getContext(),"异常提示:"+msg.obj.toString(),Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    System.out.println("错误:"+msg.obj.toString());
                    break;
                case 1:
                	Toast.makeText(getContext(),"单步定位完毕 ",Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                	Toast.makeText(getContext(),"结束定位 ",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };
    
    //定位主程序
    public String startLocation(){
    	//开启扫描
		wifiManager.startScan();
		//获取扫描结果
        list = wifiManager.getScanResults(); 
        Map<String, Double> Rssi = new HashMap<String, Double>();//存储mac-rssi映射列表
        for (int i = 0; i < list.size(); i++) { 
        	Rssi.put(list.get(i).BSSID, (double)list.get(i).level);
    	}
        fingermodel testData = new fingermodel(0,0,Rssi);//待定位点原型
		//第一步，查找最近索引值
        //获取索引表
    	Cursor cursor = sqliteDatabase.rawQuery("select * from MainIndex ", new String[0]);
    	//索引目录
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
        int nearestIndex=0;//最近索引值
        
        double minD=0.00;
        for (Map.Entry<Integer,Map<String, Double>> entry : indexData.entrySet()) {
        	fingermodel fm=new fingermodel(0,0,entry.getValue());
        	double d=kwnn.calDistance(testData, fm);
        	if(minD==0||d<minD){
        		nearestIndex=entry.getKey();
        		minD=d;
        	}
        }
        
        //第二步，匹配索引拿到对应指纹数据
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
        //第三步，在地图标注
        // 定义Maker坐标点 
        double latitude=Double.parseDouble(result.split(",")[0]);
        double longitude=Double.parseDouble(result.split(",")[1]);
        LatLng point = new LatLng(latitude, longitude); 
//        DotOptions op=new  DotOptions().center(point) 
//                .color(0XFFfaa755)
//                .radius(25)
//                .zIndex(9);  //设置marker所在层级
        MarkerOptions options = new MarkerOptions().position(point) 
  	          .icon(bitmap)
  	          .title("test")
  	          .zIndex(9);  //设置marker所在层级
        if(myOverlay!=null){
        	myOverlay.remove();
        }
        myOverlay=mBaiduMap.addOverlay(options); 
        
        return result;
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
}
