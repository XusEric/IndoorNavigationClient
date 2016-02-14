package com.pos.indoorpositioning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.pos.indoorpositioning.R;
import com.pos.util.WebServiceHelper;

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

public class MainTab extends Fragment{
	private EditText userNameEditText;  
    private EditText userPwdEditText;  
    private Button loginButton; 
    private TextView t;
    MapView mMapView = null;  
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.maintab, container,
				false); 
        //获取地图控件引用  
        mMapView = (MapView) view.findViewById(R.id.bmapView); 
        mMapView.showScaleControl(false);// 不显示默认比例尺控件
        BaiduMap mBaiduMap=mMapView.getMap();  
        //空白地图, 基础地图瓦片将不会被渲染。在地图类型中设置为NONE，将不会使用流量下载基础地图瓦片图层。使用场景：与瓦片图层一起使用，节省流量，提升自定义瓦片图下载速度。
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
      //定义多边形的五个顶点  
        LatLng pt1 = new LatLng(39.93923, 116.357428);  
        LatLng pt2 = new LatLng(39.91923, 116.327428);  
        LatLng pt3 = new LatLng(39.89923, 116.347428);  
        LatLng pt4 = new LatLng(39.89923, 116.367428);  
        LatLng pt5 = new LatLng(39.91923, 116.387428);  
        List<LatLng> pts = new ArrayList<LatLng>();  
        pts.add(pt1);  
        pts.add(pt2);  
        pts.add(pt3);  
        pts.add(pt4);  
        pts.add(pt5);  
        //构建用户绘制多边形的Option对象  
        OverlayOptions polygonOption = new PolygonOptions()  
            .points(pts)  
            .stroke(new Stroke(5, 0xAA00FF00))  
            .fillColor(0xAAFFFF00);  
        //在地图上添加多边形Option，用于显示  
        mBaiduMap.addOverlay(polygonOption);
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
	
	public class DoSomething implements Runnable {
	    private String name;

	    public DoSomething(String name) {
	        this.name = name;
	    }

	    public void run() {
	    	Message msg = new Message();
	    	try
	    	{
		    	msg.what = 1;
		    	//定义参数对象
		        HashMap<String, Object> paramsMap = new HashMap<String, Object>();  
		        //输入参数
		        paramsMap.put("name", name);  
		        //服务调用
		        SoapObject a= WebServiceHelper.getSoapObject("BasicData", "getUserList", null, paramsMap);
		        System.out.println("result:"+a.toString());
		        msg.obj=a;
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
	
	private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            //findViewById(R.id.Btn_Login).setEnabled(true);
            switch (msg.what)
            {
                case -1:
                    //exception
                    //MessageHelper.AlertDialog(Activity_Main.this, "异常提示", msg.obj.toString());
                	t.setText(msg.obj.toString()); 
                	System.out.println("异常提示:"+msg.obj.toString());
                    break;
                case 0:
                    //fail
                    //MessageHelper.AlertDialog(Activity_Main.this, "错误提示", msg.obj.toString());
                    System.out.println("错误:"+msg.obj.toString());
                    break;
                case 1:
                    //login success
                    //MessageHelper.AlertDialog(Activity_Main.this, "操作提示", "登录成功。");
        	        t.setText(msg.obj.toString()); 
                    System.out.println("操作提示:登录成功");
                    break;
                default:
                    break;
            }
        }
    };
    
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
