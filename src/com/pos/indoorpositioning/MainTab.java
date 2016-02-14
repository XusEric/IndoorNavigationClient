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
        //��ȡ��ͼ�ؼ�����  
        mMapView = (MapView) view.findViewById(R.id.bmapView); 
        mMapView.showScaleControl(false);// ����ʾĬ�ϱ����߿ؼ�
        BaiduMap mBaiduMap=mMapView.getMap();  
        //�հ׵�ͼ, ������ͼ��Ƭ�����ᱻ��Ⱦ���ڵ�ͼ����������ΪNONE��������ʹ���������ػ�����ͼ��Ƭͼ�㡣ʹ�ó���������Ƭͼ��һ��ʹ�ã���ʡ�����������Զ�����Ƭͼ�����ٶȡ�
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
      //�������ε��������  
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
        //�����û����ƶ���ε�Option����  
        OverlayOptions polygonOption = new PolygonOptions()  
            .points(pts)  
            .stroke(new Stroke(5, 0xAA00FF00))  
            .fillColor(0xAAFFFF00);  
        //�ڵ�ͼ����Ӷ����Option��������ʾ  
        mBaiduMap.addOverlay(polygonOption);
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
		    	//�����������
		        HashMap<String, Object> paramsMap = new HashMap<String, Object>();  
		        //�������
		        paramsMap.put("name", name);  
		        //�������
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
                    //MessageHelper.AlertDialog(Activity_Main.this, "�쳣��ʾ", msg.obj.toString());
                	t.setText(msg.obj.toString()); 
                	System.out.println("�쳣��ʾ:"+msg.obj.toString());
                    break;
                case 0:
                    //fail
                    //MessageHelper.AlertDialog(Activity_Main.this, "������ʾ", msg.obj.toString());
                    System.out.println("����:"+msg.obj.toString());
                    break;
                case 1:
                    //login success
                    //MessageHelper.AlertDialog(Activity_Main.this, "������ʾ", "��¼�ɹ���");
        	        t.setText(msg.obj.toString()); 
                    System.out.println("������ʾ:��¼�ɹ�");
                    break;
                default:
                    break;
            }
        }
    };
    
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
